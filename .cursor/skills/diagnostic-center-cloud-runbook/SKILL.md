---
name: diagnostic-center-cloud-runbook
description: This skill should be used when Cloud agents need to compile, run, smoke-test, or manually verify the Java diagnostic center CLI in this repository (build commands, stdin-driven flows, persistence, and per-area menu checks).
compatibility: JDK on PATH (`javac`, `java`). No Maven/Gradle. Working directory must be the repo root so `*.java` and `*.txt` data files resolve correctly.
---

# Diagnostic center — Cloud agent runbook

Plain Java CLI (no packages, no build tool). All sources and sample data live in the repository root.

## Environment and “login”

- **No authentication**: the app is a local console program. There is no API key, OAuth, or remote “login.”
- **JDK**: ensure `javac` and `java` work (`java -version`). Cloud images usually have OpenJDK preinstalled; if not, install a JDK and retry.
- **Working directory**: always `cd` to the repo root before compile/run so:
  - `javac *.java` finds all `*.java` files.
  - `DataStore` reads/writes `doctors.txt`, `patients.txt`, `exams.txt`, `appointments.txt` relative to the current directory (`.`).
- **Feature flags / env vars**: the codebase does **not** read environment variables or system properties for toggles. There is nothing to set or mock for flags—behavior is fixed unless the code is changed. For isolated runs, use a **separate directory** (copy sources + compile there) or temporary `*.txt` files if you must not touch committed sample data.

## Quick start (every agent session)

```bash
cd /path/to/repo
javac *.java
printf '0\n' | java Main
```

- First run with **all four** data files missing: app seeds data, writes the four files, prints `First run: sample data created and saved.`, then shows the main menu.
- If **some but not all** of the four files exist: app prints an incomplete-dataset error and exits without starting the menu—avoid half-populated roots or restore all four files.
- On exit (`0`), state is saved and you should see `Data saved. Goodbye.`

Do **not** commit `*.class` files (they are gitignored).

---

## By codebase area

### `Main.java` — CLI orchestration

**What it does:** loads/saves via `MedicalCenterStore`, main loop, submenus for doctors, patients, exams, appointments, statistics.

**How to test:**

1. **Smoke (startup + exit + save):** `printf '0\n' | java Main`
2. **Menu wiring:** pipe a short sequence of choices (main `1`–`5`, then submenu options, then `0` to go back). Example pattern: `4` (appointments) → `0` (back) → `0` (exit):  
   `printf '4\n0\n0\n' | java Main`
3. **Invalid input:** exercise paths that call `InputHelper` (out-of-range ints, bad dates). Prefer driving the real CLI over guessing error text.

### `InputHelper.java` — console validation

**What it does:** bounded integers, non-empty strings, dates, yes/no, and “existing id” helpers used by menus.

**How to test:** any menu that prompts for input; send values **outside** allowed ranges and confirm reprompting or safe exit (e.g. `0` cancel where offered). Pair with the menu area under test (appointments date format `dd:MM:yyyy` per prompts).

### `MedicalCenter.java` — domain rules and collections

**What it does:** in-memory model, IDs, business rules (e.g. appointment slot limits per exam per day).

**How to test:** use **Appointments** and **Examinations** flows (see below). Rules surface as console messages or exceptions during load—no separate unit test runner.

### `DataStore.java` + `CsvMedicalCenterStore.java` + `MedicalCenterStore.java` / `MedicalCenterSnapshot.java` — persistence

**What it does:** CSV lines in the four `*.txt` files; `DataSetStatus` distinguishes missing vs complete vs incomplete sets.

**How to test:**

1. **Load path:** run with existing valid four files (normal checkout).
2. **First-run seed:** run in an empty temp dir with only `*.java` copied (or delete the four txt files), compile, run once, confirm four files created.
3. **Incomplete set:** leave only one or two txt files present, run—expect stderr about incomplete data and immediate exit.
4. **Round-trip:** add or change an entity through the CLI, exit, grep or `cat` the relevant file, restart and confirm data still there.

### Entity classes — `Doctor.java`, `Patient.java`, `Appointment.java`

**How to test:** add/list/show flows under **Doctors** (main menu `1`) and **Patients** (`2`); list/delete appointments under **Appointments** (`4`). Confirm IDs and fields match persisted lines after save.

### Exam hierarchy — `Exam.java`, `ImagingExamination.java`, `MicrobiologicalExamination.java`, `SpecializedExamination.java`

**How to test:** main menu `3` (Examinations) → add each category (imaging / microbiological / specialized), list sorted by name, show one exam’s appointments. After exit, confirm `exams.txt` format still parses on next run.

### `DataSetStatus.java` — dataset presence

**How to test:** covered by persistence scenarios (all missing → seed; all present → load; partial → error).

### Statistics (menu in `Main.java`)

**How to test:** main menu `5`; after creating appointments with **fast results** yes/no as prompted, revisit statistics and confirm counts change as expected (see `AGENTS.md` recommended checks).

---

## Concrete workflows (copy-paste patterns)

Use `printf` with `\n`-separated lines matching **exact prompt order**. Adjust IDs if your dataset differs from a fresh seed.

| Goal | Idea |
|------|------|
| Exit immediately | `printf '0\n' | java Main` |
| Open appointments then back out | `printf '4\n0\n0\n' | java Main` |
| Slot limit (same exam, same date) | Add appointments from menu `4` → `1` until “No slots left…”; need valid patient/exam ids and repeated same date. |
| Persistence | Change data via CLI → `0` exit → `java Main` again → verify listing menus show the change. |

For long scripted inputs, a here-doc is readable:

```bash
java Main <<'EOF'
0
EOF
```

---

## Updating this skill

When you discover a new manual trick, data edge case, or runbook step:

1. **Add it under the right “By codebase area” section** (or add a short row to the workflow table).
2. **If the behavior is contractual** (file names, date format, incomplete dataset rules), consider a one-line pointer in `AGENTS.md` so humans and agents stay aligned.
3. **Keep `description` in the YAML accurate** if the skill’s scope changes (e.g. new env toggles—document real variable names only if the code reads them).
4. **Avoid duplicating long prose** between this skill and `AGENTS.md`: this file is for **agent execution order** and stdin patterns; `AGENTS.md` remains the project overview.
