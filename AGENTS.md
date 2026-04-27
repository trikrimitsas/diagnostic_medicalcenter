# Repository Guidelines

## Project Structure & Module Organization

This repository is a plain Java command-line application with no packages. All source files live in the repository root so the project can be compiled directly from the command line.

- `Main.java`: CLI menus, startup, shutdown, and save/load orchestration.
- `MedicalCenter.java`: in-memory collections and domain rules.
- `DataStore.java`: text-file persistence.
- `InputHelper.java`: console input validation.
- `Doctor.java`, `Patient.java`, `Appointment.java`: entity classes.
- `Exam.java` plus `ImagingExamination.java`, `MicrobiologicalExamination.java`, `SpecializedExamination.java`: examination hierarchy.
- `doctors.txt`, `patients.txt`, `exams.txt`, `appointments.txt`: sample persisted data.

Compiled `.class` files and submission `.zip` files are ignored by Git.

## Build, Test, and Development Commands

Run commands from the repository root:

```powershell
javac *.java
```

Compiles all Java source files.

```powershell
java Main
```

Starts the interactive diagnostic center CLI.

```powershell
"0" | java Main
```

Smoke-tests startup, data loading, exit, and save behavior.

## Coding Style & Naming Conventions

Use standard Java naming: `PascalCase` for classes, `camelCase` for fields, methods, and local variables, and `UPPER_SNAKE_CASE` for constants. Keep fields `private` and expose state through getters/setters where needed. Do not add package declarations; the assignment requires direct command-line compilation.

Use English comments and user-facing messages. Keep comments short and useful, especially around domain rules, file formats, and validation.

## Testing Guidelines

There is no automated test framework in this repository. Before committing, run `javac *.java` and at least one CLI smoke test. For behavior changes, manually verify the affected menu path, including invalid input where relevant.

Recommended manual checks:

- Create an appointment and confirm max daily slots are enforced.
- Exit and restart to confirm text-file persistence.
- Verify statistics after adding fast-results appointments.

## Commit & Pull Request Guidelines

The current history uses concise, imperative-style commit messages, for example:

```text
Initial diagnostic center implementation
```

Keep future commits focused on one logical change. Pull requests should include a short description, commands run, and any manual menu flows tested. Mention changes to text-file formats or sample data explicitly.

## Agent-Specific Instructions

Do not commit generated `.class` files. Preserve the no-package structure. Avoid broad rewrites unless required for assignment correctness.
