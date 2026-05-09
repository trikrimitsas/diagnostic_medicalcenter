import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final String[] DOCTOR_SPECIALTIES = {
            "Cardiology", "Radiology", "Microbiology", "Neurology", "Pulmonology"
    };
    private static final String[] MACHINE_TYPES = {"MRI", "CT", "X-Ray"};
    private static final String[] SAMPLE_TYPES = {"Blood", "Urine", "Swab"};
    private static final String[] EXAM_SPECIALTIES = {"Cardiology", "Neurology", "Pulmonology"};

    public static void main(String[] args) {
        MedicalCenterStore store = new CsvMedicalCenterStore(new DataStore());
        MedicalCenter center = new MedicalCenter();
        try {
            DataSetStatus status = store.status();
            if (status == DataSetStatus.MISSING) {
                center.seedInitialData();
                store.saveSnapshot(center.toSnapshot());
                System.out.println("First run: sample data created and saved.");
            } else if (status == DataSetStatus.INCOMPLETE) {
                System.err.println("Data files are incomplete. Expected doctors.txt, patients.txt, exams.txt, and appointments.txt.");
                return;
            } else {
                center = MedicalCenter.fromSnapshot(store.loadSnapshot());
            }
        } catch (IOException e) {
            System.err.println("Failed to load or save data: " + e.getMessage());
            return;
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid data: " + e.getMessage());
            return;
        }

        Scanner scanner = new Scanner(System.in);
        InputHelper in = new InputHelper(scanner);

        boolean running = true;
        while (running) {
            System.out.println();
            System.out.println("=== Main menu ===");
            System.out.println("1. Doctors");
            System.out.println("2. Patients");
            System.out.println("3. Examinations");
            System.out.println("4. Appointments");
            System.out.println("5. Statistics");
            System.out.println("0. Exit");
            int choice = in.readIntInRange("Select: ", 0, 5);
            switch (choice) {
                case 1:
                    doctorsMenu(in, center);
                    break;
                case 2:
                    patientsMenu(in, center);
                    break;
                case 3:
                    examsMenu(in, center);
                    break;
                case 4:
                    appointmentsMenu(in, center);
                    break;
                case 5:
                    statisticsMenu(in, center);
                    break;
                case 0:
                    running = false;
                    break;
                default:
                    break;
            }
        }

        try {
            store.saveSnapshot(center.toSnapshot());
            System.out.println("Data saved. Goodbye.");
        } catch (IOException e) {
            System.err.println("Failed to save data: " + e.getMessage());
        }
        scanner.close();
    }

    private static void doctorsMenu(InputHelper in, MedicalCenter center) {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("--- Doctors ---");
            System.out.println("1. Add doctor");
            System.out.println("2. List all doctors");
            System.out.println("3. Show one doctor (exams supervised)");
            System.out.println("4. Appointments for a doctor");
            System.out.println("0. Back");
            int c = in.readIntInRange("Select: ", 0, 4);
            switch (c) {
                case 1:
                    addDoctor(in, center);
                    break;
                case 2:
                    for (Doctor d : center.getDoctors()) {
                        System.out.println(d);
                    }
                    break;
                case 3:
                    showDoctorWithExams(in, center);
                    break;
                case 4:
                    showDoctorAppointments(in, center);
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    break;
            }
        }
    }

    private static void addDoctor(InputHelper in, MedicalCenter center) {
        int id = center.nextDoctorId();
        String name = in.readLineNonEmpty("Doctor name: ");
        String phone = in.readLineNonEmpty("Phone: ");
        System.out.println("Specialties:");
        for (int i = 0; i < DOCTOR_SPECIALTIES.length; i++) {
            System.out.println((i + 1) + ". " + DOCTOR_SPECIALTIES[i]);
        }
        int s = in.readIntInRange("Choose specialty number: ", 1, DOCTOR_SPECIALTIES.length);
        String specialty = DOCTOR_SPECIALTIES[s - 1];
        int years = in.readPositiveInt("Years of experience: ");
        center.addDoctor(new Doctor(id, name, phone, specialty, years));
        System.out.println("Doctor added with id " + id + ".");
    }

    private static void showDoctorWithExams(InputHelper in, MedicalCenter center) {
        int id = in.readExistingDoctorId(center);
        if (id == 0) {
            return;
        }
        Doctor d = center.findDoctorById(id);
        System.out.println(d);
        System.out.println("Exams supervised:");
        for (Exam e : center.getExamsForDoctor(id)) {
            System.out.println("  " + e);
        }
    }

    private static void showDoctorAppointments(InputHelper in, MedicalCenter center) {
        int id = in.readExistingDoctorId(center);
        if (id == 0) {
            return;
        }
        Doctor d = center.findDoctorById(id);
        System.out.println(d);
        System.out.println("Appointments (exams for this doctor):");
        for (Appointment a : center.getAppointmentsForDoctor(id)) {
            System.out.println("  " + a);
        }
    }

    private static void patientsMenu(InputHelper in, MedicalCenter center) {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("--- Patients ---");
            System.out.println("1. Add patient");
            System.out.println("2. List all patients");
            System.out.println("3. Show one patient (appointments)");
            System.out.println("0. Back");
            int c = in.readIntInRange("Select: ", 0, 3);
            switch (c) {
                case 1:
                    addPatient(in, center);
                    break;
                case 2:
                    for (Patient p : center.getPatients()) {
                        System.out.println(p);
                    }
                    break;
                case 3:
                    showPatientAppointments(in, center);
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    break;
            }
        }
    }

    private static void addPatient(InputHelper in, MedicalCenter center) {
        int id = center.nextPatientId();
        String name = in.readLineNonEmpty("Patient name: ");
        String phone = in.readLineNonEmpty("Phone: ");
        String email = in.readLineNonEmpty("Email: ");
        center.addPatient(new Patient(id, name, phone, email));
        System.out.println("Patient added with id " + id + ".");
    }

    private static void showPatientAppointments(InputHelper in, MedicalCenter center) {
        int id = in.readExistingPatientId(center);
        if (id == 0) {
            return;
        }
        Patient p = center.findPatientById(id);
        System.out.println(p);
        System.out.println("Appointments:");
        for (Appointment a : center.getAppointmentsForPatient(id)) {
            System.out.println("  " + a);
        }
    }

    private static void examsMenu(InputHelper in, MedicalCenter center) {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("--- Examinations ---");
            System.out.println("1. Add examination");
            System.out.println("2. List all examinations (sorted by name)");
            System.out.println("3. Show one examination (appointments)");
            System.out.println("0. Back");
            int c = in.readIntInRange("Select: ", 0, 3);
            switch (c) {
                case 1:
                    addExam(in, center);
                    break;
                case 2:
                    for (Exam e : center.getExamsSortedByName()) {
                        System.out.println(e);
                    }
                    break;
                case 3:
                    showExamAppointments(in, center);
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    break;
            }
        }
    }

    private static void addExam(InputHelper in, MedicalCenter center) {
        int doctorId = in.readExistingDoctorId(center);
        if (doctorId == 0) {
            return;
        }
        System.out.println("Exam categories:");
        System.out.println("1. Imaging");
        System.out.println("2. Microbiological");
        System.out.println("3. Specialized");
        int cat = in.readIntInRange("Category: ", 1, 3);
        String examName = in.readLineNonEmpty("Exam name: ");
        int maxSlots = in.readStrictlyPositiveInt("Max appointments per day: ");
        double cost = in.readNonNegativeDouble("Base cost: ");
        int id = center.nextExamId();

        if (cat == 1) {
            System.out.println("Machine types:");
            for (int i = 0; i < MACHINE_TYPES.length; i++) {
                System.out.println((i + 1) + ". " + MACHINE_TYPES[i]);
            }
            int m = in.readIntInRange("Choose machine type: ", 1, MACHINE_TYPES.length);
            String machine = MACHINE_TYPES[m - 1];
            center.addExam(new ImagingExamination(id, "Imaging", examName, maxSlots, cost, doctorId, machine));
        } else if (cat == 2) {
            System.out.println("Sample types:");
            for (int i = 0; i < SAMPLE_TYPES.length; i++) {
                System.out.println((i + 1) + ". " + SAMPLE_TYPES[i]);
            }
            int s = in.readIntInRange("Choose sample type: ", 1, SAMPLE_TYPES.length);
            String sample = SAMPLE_TYPES[s - 1];
            center.addExam(new MicrobiologicalExamination(id, "Microbiological", examName, maxSlots, cost, doctorId,
                    sample));
        } else {
            System.out.println("Specialized exam specialties:");
            for (int i = 0; i < EXAM_SPECIALTIES.length; i++) {
                System.out.println((i + 1) + ". " + EXAM_SPECIALTIES[i]);
            }
            int s = in.readIntInRange("Choose specialty: ", 1, EXAM_SPECIALTIES.length);
            String spec = EXAM_SPECIALTIES[s - 1];
            center.addExam(new SpecializedExamination(id, "Specialized", examName, maxSlots, cost, doctorId, spec));
        }
        System.out.println("Exam added with id " + id + ".");
    }

    private static void showExamAppointments(InputHelper in, MedicalCenter center) {
        int id = in.readExistingExamId(center);
        if (id == 0) {
            return;
        }
        Exam e = center.findExamById(id);
        System.out.println(e);
        System.out.println("Appointments:");
        for (Appointment a : center.getAppointmentsForExam(id)) {
            System.out.println("  " + a);
        }
    }

    private static void appointmentsMenu(InputHelper in, MedicalCenter center) {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("--- Appointments ---");
            System.out.println("1. Add appointment");
            System.out.println("2. List all appointments");
            System.out.println("3. Appointments for a patient");
            System.out.println("4. Delete appointment");
            System.out.println("5. Appointments for a date");
            System.out.println("0. Back");
            int c = in.readIntInRange("Select: ", 0, 5);
            switch (c) {
                case 1:
                    addAppointment(in, center);
                    break;
                case 2:
                    for (Appointment a : center.getAppointments()) {
                        System.out.println(a);
                    }
                    break;
                case 3:
                    showPatientAppointments(in, center);
                    break;
                case 4:
                    deleteAppointment(in, center);
                    break;
                case 5:
                    appointmentsForDate(in, center);
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    break;
            }
        }
    }

    private static void addAppointment(InputHelper in, MedicalCenter center) {
        int patientId = in.readExistingPatientId(center);
        if (patientId == 0) {
            return;
        }
        int examId = in.readExistingExamId(center);
        if (examId == 0) {
            return;
        }
        Exam exam = center.findExamById(examId);
        if (exam == null) {
            System.out.println("Invalid exam.");
            return;
        }
        boolean fast = in.readYesNo("Fast results?");
        String date;
        while (true) {
            date = in.readValidDate("Appointment date (dd:MM:yyyy): ");
            int booked = center.countAppointmentsOnDateForExam(examId, date);
            if (booked >= exam.getMaxSlotsPerDay()) {
                System.out.println("No slots left for this exam on that date. Choose another date.");
                continue;
            }
            break;
        }
        int id = center.nextAppointmentId();
        center.addAppointment(new Appointment(id, patientId, examId, fast, date));
        System.out.println("Appointment added with id " + id + ".");
    }

    private static void deleteAppointment(InputHelper in, MedicalCenter center) {
        List<Appointment> list = center.getAppointments();
        if (list.isEmpty()) {
            System.out.println("No appointments.");
            return;
        }
        for (Appointment a : list) {
            System.out.println(a);
        }
        int id = in.readIntInRange("Enter appointment id to delete (0 to cancel): ", 0, Integer.MAX_VALUE);
        if (id == 0) {
            return;
        }
        Appointment a = center.findAppointmentById(id);
        if (a == null) {
            System.out.println("Appointment not found.");
            return;
        }
        if (in.readYesNo("Confirm delete?")) {
            if (center.removeAppointmentById(id)) {
                System.out.println("Appointment deleted.");
            }
        }
    }

    private static void appointmentsForDate(InputHelper in, MedicalCenter center) {
        String date = in.readValidDate("Date (dd:MM:yyyy): ");
        for (Appointment a : center.getAppointmentsOnDate(date)) {
            Patient p = center.findPatientById(a.getPatientId());
            Exam e = center.findExamById(a.getExamId());
            String pName = p != null ? p.getPatientName() : "?";
            String eName = e != null ? e.getExamName() : "?";
            System.out.println(a + " | patient=" + pName + " | exam=" + eName);
        }
    }

    private static void statisticsMenu(InputHelper in, MedicalCenter center) {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("--- Statistics ---");
            System.out.println("1. Total revenue per patient");
            System.out.println("2. Total revenue per examination");
            System.out.println("3. Total revenue per examination category");
            System.out.println("0. Back");
            int c = in.readIntInRange("Select: ", 0, 3);
            switch (c) {
                case 1:
                    statsByPatient(center);
                    break;
                case 2:
                    statsByExam(center);
                    break;
                case 3:
                    statsByCategory(center);
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    break;
            }
        }
    }

    private static void statsByPatient(MedicalCenter center) {
        double grand = 0.0;
        for (Patient p : center.getPatients()) {
            System.out.println("Patient: " + p);
            double sum = 0.0;
            for (Appointment a : center.getAppointmentsForPatient(p.getPatientId())) {
                double rev = center.revenueForAppointment(a);
                sum += rev;
                System.out.println("  " + a + " | cost=" + rev);
            }
            System.out.println("  Subtotal for patient: " + sum);
            grand += sum;
        }
        System.out.println("Grand total (all patients): " + grand);
    }

    private static void statsByExam(MedicalCenter center) {
        double grand = 0.0;
        for (Exam e : center.getExams()) {
            System.out.println("Exam: " + e);
            double sum = 0.0;
            for (Appointment a : center.getAppointmentsForExam(e.getExamId())) {
                double rev = center.revenueForAppointment(a);
                sum += rev;
                System.out.println("  " + a + " | cost=" + rev);
            }
            System.out.println("  Subtotal for exam: " + sum);
            grand += sum;
        }
        System.out.println("Grand total (all exams): " + grand);
    }

    private static void statsByCategory(MedicalCenter center) {
        String[] categories = {"Imaging", "Microbiological", "Specialized"};
        double grand = 0.0;
        for (String cat : categories) {
            System.out.println("Category: " + cat);
            double sum = 0.0;
            for (Appointment a : center.getAppointments()) {
                Exam e = center.findExamById(a.getExamId());
                if (e == null || !cat.equals(e.getCategoryName())) {
                    continue;
                }
                double rev = center.revenueForAppointment(a);
                sum += rev;
                System.out.println("  " + a + " | cost=" + rev);
            }
            System.out.println("  Subtotal for category: " + sum);
            grand += sum;
        }
        System.out.println("Grand total (all categories): " + grand);
    }
}
