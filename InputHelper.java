import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Scanner;

/**
 * Console input validation: menu ranges, dates dd:MM:yyyy, yes/no.
 */
public class InputHelper {
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd:MM:uuuu")
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter LEGACY_DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/uuuu")
            .withResolverStyle(ResolverStyle.STRICT);

    private final Scanner scanner;

    public InputHelper(Scanner scanner) {
        this.scanner = scanner;
    }

    public int readIntInRange(String prompt, int minInclusive, int maxInclusive) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                int v = Integer.parseInt(line);
                if (v >= minInclusive && v <= maxInclusive) {
                    return v;
                }
            } catch (NumberFormatException ignored) {
                // retry
            }
            System.out.println("Invalid choice. Enter a number between " + minInclusive + " and " + maxInclusive + ".");
        }
    }

    public String readLineNonEmpty(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            if (!line.isEmpty()) {
                return line;
            }
            System.out.println("Value cannot be empty.");
        }
    }

    public int readPositiveInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                int v = Integer.parseInt(line);
                if (v >= 0) {
                    return v;
                }
            } catch (NumberFormatException ignored) {
                // retry
            }
            System.out.println("Enter a non-negative integer.");
        }
    }

    public int readStrictlyPositiveInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                int v = Integer.parseInt(line);
                if (v > 0) {
                    return v;
                }
            } catch (NumberFormatException ignored) {
                // retry
            }
            System.out.println("Enter a positive integer.");
        }
    }

    public double readNonNegativeDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                double v = Double.parseDouble(line);
                if (v >= 0) {
                    return v;
                }
            } catch (NumberFormatException ignored) {
                // retry
            }
            System.out.println("Enter a non-negative number.");
        }
    }

    public String readValidDate(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            if (isValidDate(line)) {
                return normalizeDate(line);
            }
            System.out.println("Invalid date. Use format dd:MM:yyyy (e.g. 10:05:2026).");
        }
    }

    public boolean isValidDate(String s) {
        try {
            LocalDate.parse(s, DATE_FMT);
            return true;
        } catch (DateTimeParseException e) {
            try {
                LocalDate.parse(s, LEGACY_DATE_FMT);
                return true;
            } catch (DateTimeParseException ignored) {
                return false;
            }
        }
    }

    public String normalizeDate(String s) {
        LocalDate date;
        try {
            date = LocalDate.parse(s, DATE_FMT);
        } catch (DateTimeParseException e) {
            date = LocalDate.parse(s, LEGACY_DATE_FMT);
        }
        return DATE_FMT.format(date);
    }

    /**
     * @return true if user confirms (yes/y), false for no/n
     */
    public boolean readYesNo(String prompt) {
        while (true) {
            System.out.print(prompt + " (yes/no): ");
            String line = scanner.nextLine().trim().toLowerCase();
            if (line.equals("yes") || line.equals("y")) {
                return true;
            }
            if (line.equals("no") || line.equals("n")) {
                return false;
            }
            System.out.println("Type yes or no.");
        }
    }

    public int readExistingDoctorId(MedicalCenter center) {
        while (true) {
            listDoctorsBrief(center);
            int id = readIntInRange("Enter doctor id (0 to cancel): ", 0, Integer.MAX_VALUE);
            if (id == 0) {
                return 0;
            }
            if (center.findDoctorById(id) != null) {
                return id;
            }
            System.out.println("No doctor with this id.");
        }
    }

    public int readExistingPatientId(MedicalCenter center) {
        while (true) {
            listPatientsBrief(center);
            int id = readIntInRange("Enter patient id (0 to cancel): ", 0, Integer.MAX_VALUE);
            if (id == 0) {
                return 0;
            }
            if (center.findPatientById(id) != null) {
                return id;
            }
            System.out.println("No patient with this id.");
        }
    }

    public int readExistingExamId(MedicalCenter center) {
        while (true) {
            listExamsBrief(center);
            int id = readIntInRange("Enter exam id (0 to cancel): ", 0, Integer.MAX_VALUE);
            if (id == 0) {
                return 0;
            }
            if (center.findExamById(id) != null) {
                return id;
            }
            System.out.println("No exam with this id.");
        }
    }

    private void listDoctorsBrief(MedicalCenter center) {
        System.out.println("Doctors:");
        for (Doctor d : center.getDoctors()) {
            System.out.println("  id=" + d.getDoctorId() + "  " + d.getDoctorName());
        }
    }

    private void listPatientsBrief(MedicalCenter center) {
        System.out.println("Patients:");
        for (Patient p : center.getPatients()) {
            System.out.println("  id=" + p.getPatientId() + "  " + p.getPatientName());
        }
    }

    private void listExamsBrief(MedicalCenter center) {
        System.out.println("Exams:");
        for (Exam e : center.getExams()) {
            System.out.println("  id=" + e.getExamId() + "  " + e.getExamName());
        }
    }
}
