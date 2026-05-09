import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class EducationalMedicalCenter {
    private static final String DOCTORS_FILE = "simple_doctors.txt";
    private static final String PATIENTS_FILE = "simple_patients.txt";
    private static final String EXAMS_FILE = "simple_exams.txt";
    private static final String APPOINTMENTS_FILE = "simple_appointments.txt";

    private static final ArrayList<Doctor> doctors = new ArrayList<>();
    private static final ArrayList<Patient> patients = new ArrayList<>();
    private static final ArrayList<Exam> exams = new ArrayList<>();
    private static final ArrayList<Appointment> appointments = new ArrayList<>();
    private static final Scanner input = new Scanner(System.in);

    public static void main(String[] args) {
        loadData();

        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = readInt("Choice: ");

            if (choice == 1) {
                addDoctor();
            } else if (choice == 2) {
                addPatient();
            } else if (choice == 3) {
                addExam();
            } else if (choice == 4) {
                addAppointment();
            } else if (choice == 5) {
                listDoctors();
            } else if (choice == 6) {
                listPatients();
            } else if (choice == 7) {
                listExams();
            } else if (choice == 8) {
                listAppointments();
            } else if (choice == 9) {
                printStatistics();
            } else if (choice == 0) {
                running = false;
            } else {
                System.out.println("Wrong choice.");
            }
        }

        saveData();
        System.out.println("Data saved.");
    }

    private static void printMainMenu() {
        System.out.println();
        System.out.println("=== Diagnostic Medical Center ===");
        System.out.println("1. Add doctor");
        System.out.println("2. Add patient");
        System.out.println("3. Add exam");
        System.out.println("4. Add appointment");
        System.out.println("5. List doctors");
        System.out.println("6. List patients");
        System.out.println("7. List exams");
        System.out.println("8. List appointments");
        System.out.println("9. Statistics");
        System.out.println("0. Exit");
    }

    private static void addDoctor() {
        int id = nextDoctorId();
        String name = readText("Doctor name: ");
        String phone = readText("Phone: ");
        String specialty = readText("Specialty: ");
        int years = readInt("Years of experience: ");

        doctors.add(new Doctor(id, name, phone, specialty, years));
        System.out.println("Doctor added with id " + id + ".");
    }

    private static void addPatient() {
        int id = nextPatientId();
        String name = readText("Patient name: ");
        String phone = readText("Phone: ");
        String email = readText("Email: ");

        patients.add(new Patient(id, name, phone, email));
        System.out.println("Patient added with id " + id + ".");
    }

    private static void addExam() {
        if (doctors.isEmpty()) {
            System.out.println("Add a doctor first.");
            return;
        }

        listDoctors();
        int doctorId = readInt("Doctor id: ");
        if (findDoctor(doctorId) == null) {
            System.out.println("Doctor not found.");
            return;
        }

        System.out.println("Exam type:");
        System.out.println("1. Imaging");
        System.out.println("2. Microbiological");
        System.out.println("3. Specialized");
        int type = readInt("Type: ");

        int id = nextExamId();
        String name = readText("Exam name: ");
        int maxSlots = readInt("Max appointments per day: ");
        double cost = readDouble("Base cost: ");

        if (type == 1) {
            String machineType = readText("Machine type: ");
            exams.add(new ImagingExam(id, name, maxSlots, cost, doctorId, machineType));
        } else if (type == 2) {
            String sampleType = readText("Sample type: ");
            exams.add(new MicrobiologicalExam(id, name, maxSlots, cost, doctorId, sampleType));
        } else if (type == 3) {
            String specialty = readText("Exam specialty: ");
            exams.add(new SpecializedExam(id, name, maxSlots, cost, doctorId, specialty));
        } else {
            System.out.println("Wrong exam type.");
            return;
        }

        System.out.println("Exam added with id " + id + ".");
    }

    private static void addAppointment() {
        if (patients.isEmpty() || exams.isEmpty()) {
            System.out.println("Add at least one patient and one exam first.");
            return;
        }

        listPatients();
        int patientId = readInt("Patient id: ");
        if (findPatient(patientId) == null) {
            System.out.println("Patient not found.");
            return;
        }

        listExams();
        int examId = readInt("Exam id: ");
        Exam exam = findExam(examId);
        if (exam == null) {
            System.out.println("Exam not found.");
            return;
        }

        String date = readText("Date (dd:mm:yyyy): ");
        int alreadyBooked = countAppointments(examId, date);
        if (alreadyBooked >= exam.maxSlotsPerDay) {
            System.out.println("No free slots for this exam on this date.");
            return;
        }

        boolean fastResults = readYesNo("Fast results? (y/n): ");
        int id = nextAppointmentId();
        appointments.add(new Appointment(id, patientId, examId, date, fastResults));
        System.out.println("Appointment added with id " + id + ".");
    }

    private static void listDoctors() {
        System.out.println();
        System.out.println("Doctors");
        for (Doctor doctor : doctors) {
            System.out.println(doctor);
        }
    }

    private static void listPatients() {
        System.out.println();
        System.out.println("Patients");
        for (Patient patient : patients) {
            System.out.println(patient);
        }
    }

    private static void listExams() {
        System.out.println();
        System.out.println("Exams");
        exams.sort(Comparator.comparing(exam -> exam.name.toLowerCase()));
        for (Exam exam : exams) {
            System.out.println(exam);
        }
    }

    private static void listAppointments() {
        System.out.println();
        System.out.println("Appointments");
        for (Appointment appointment : appointments) {
            Patient patient = findPatient(appointment.patientId);
            Exam exam = findExam(appointment.examId);
            String patientName = patient == null ? "?" : patient.name;
            String examName = exam == null ? "?" : exam.name;
            double cost = exam == null ? 0.0 : exam.getCost(appointment.fastResults);

            System.out.println(appointment + ", patient=" + patientName + ", exam=" + examName + ", cost=" + cost);
        }
    }

    private static void printStatistics() {
        System.out.println();
        System.out.println("Revenue per patient");
        for (Patient patient : patients) {
            double total = 0.0;
            for (Appointment appointment : appointments) {
                if (appointment.patientId == patient.id) {
                    Exam exam = findExam(appointment.examId);
                    if (exam != null) {
                        total += exam.getCost(appointment.fastResults);
                    }
                }
            }
            System.out.println(patient.name + ": " + total);
        }

        System.out.println();
        System.out.println("Revenue per exam");
        for (Exam exam : exams) {
            double total = 0.0;
            for (Appointment appointment : appointments) {
                if (appointment.examId == exam.id) {
                    total += exam.getCost(appointment.fastResults);
                }
            }
            System.out.println(exam.name + ": " + total);
        }
    }

    private static Doctor findDoctor(int id) {
        for (Doctor doctor : doctors) {
            if (doctor.id == id) {
                return doctor;
            }
        }
        return null;
    }

    private static Patient findPatient(int id) {
        for (Patient patient : patients) {
            if (patient.id == id) {
                return patient;
            }
        }
        return null;
    }

    private static Exam findExam(int id) {
        for (Exam exam : exams) {
            if (exam.id == id) {
                return exam;
            }
        }
        return null;
    }

    private static int countAppointments(int examId, String date) {
        int count = 0;
        for (Appointment appointment : appointments) {
            if (appointment.examId == examId && appointment.date.equals(date)) {
                count++;
            }
        }
        return count;
    }

    private static int nextDoctorId() {
        int max = 0;
        for (Doctor doctor : doctors) {
            if (doctor.id > max) {
                max = doctor.id;
            }
        }
        return max + 1;
    }

    private static int nextPatientId() {
        int max = 0;
        for (Patient patient : patients) {
            if (patient.id > max) {
                max = patient.id;
            }
        }
        return max + 1;
    }

    private static int nextExamId() {
        int max = 0;
        for (Exam exam : exams) {
            if (exam.id > max) {
                max = exam.id;
            }
        }
        return max + 1;
    }

    private static int nextAppointmentId() {
        int max = 0;
        for (Appointment appointment : appointments) {
            if (appointment.id > max) {
                max = appointment.id;
            }
        }
        return max + 1;
    }

    private static String readText(String message) {
        System.out.print(message);
        return input.nextLine().trim();
    }

    private static int readInt(String message) {
        while (true) {
            try {
                System.out.print(message);
                return Integer.parseInt(input.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Give an integer number.");
            }
        }
    }

    private static double readDouble(String message) {
        while (true) {
            try {
                System.out.print(message);
                return Double.parseDouble(input.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Give a number.");
            }
        }
    }

    private static boolean readYesNo(String message) {
        System.out.print(message);
        String answer = input.nextLine().trim().toLowerCase();
        return answer.equals("y") || answer.equals("yes");
    }

    private static void loadData() {
        if (!Files.exists(Paths.get(DOCTORS_FILE))) {
            createStartingData();
            return;
        }

        try {
            for (String line : readLines(DOCTORS_FILE)) {
                String[] p = line.split(",", -1);
                doctors.add(new Doctor(toInt(p[0]), p[1], p[2], p[3], toInt(p[4])));
            }

            for (String line : readLines(PATIENTS_FILE)) {
                String[] p = line.split(",", -1);
                patients.add(new Patient(toInt(p[0]), p[1], p[2], p[3]));
            }

            for (String line : readLines(EXAMS_FILE)) {
                String[] p = line.split(",", -1);
                int id = toInt(p[1]);
                String name = p[2];
                int maxSlots = toInt(p[3]);
                double cost = toDouble(p[4]);
                int doctorId = toInt(p[5]);
                String extra = p[6];

                if (p[0].equals("IMAGING")) {
                    exams.add(new ImagingExam(id, name, maxSlots, cost, doctorId, extra));
                } else if (p[0].equals("MICRO")) {
                    exams.add(new MicrobiologicalExam(id, name, maxSlots, cost, doctorId, extra));
                } else if (p[0].equals("SPEC")) {
                    exams.add(new SpecializedExam(id, name, maxSlots, cost, doctorId, extra));
                }
            }

            for (String line : readLines(APPOINTMENTS_FILE)) {
                String[] p = line.split(",", -1);
                appointments.add(new Appointment(toInt(p[0]), toInt(p[1]), toInt(p[2]), p[3],
                        Boolean.parseBoolean(p[4])));
            }
        } catch (Exception e) {
            System.out.println("Could not load data. Starting with example data.");
            doctors.clear();
            patients.clear();
            exams.clear();
            appointments.clear();
            createStartingData();
        }
    }

    private static void saveData() {
        try {
            ArrayList<String> doctorLines = new ArrayList<>();
            for (Doctor doctor : doctors) {
                doctorLines.add(doctor.id + "," + doctor.name + "," + doctor.phone + "," + doctor.specialty + ","
                        + doctor.years);
            }
            writeLines(DOCTORS_FILE, doctorLines);

            ArrayList<String> patientLines = new ArrayList<>();
            for (Patient patient : patients) {
                patientLines.add(patient.id + "," + patient.name + "," + patient.phone + "," + patient.email);
            }
            writeLines(PATIENTS_FILE, patientLines);

            ArrayList<String> examLines = new ArrayList<>();
            for (Exam exam : exams) {
                examLines.add(exam.fileLine());
            }
            writeLines(EXAMS_FILE, examLines);

            ArrayList<String> appointmentLines = new ArrayList<>();
            for (Appointment appointment : appointments) {
                appointmentLines.add(appointment.id + "," + appointment.patientId + "," + appointment.examId + ","
                        + appointment.date + "," + appointment.fastResults);
            }
            writeLines(APPOINTMENTS_FILE, appointmentLines);
        } catch (IOException e) {
            System.out.println("Could not save data: " + e.getMessage());
        }
    }

    private static List<String> readLines(String fileName) throws IOException {
        Path path = Paths.get(fileName);
        if (!Files.exists(path)) {
            return new ArrayList<>();
        }
        return Files.readAllLines(path, StandardCharsets.UTF_8);
    }

    private static void writeLines(String fileName, List<String> lines) throws IOException {
        Files.write(Paths.get(fileName), lines, StandardCharsets.UTF_8);
    }

    private static int toInt(String value) {
        return Integer.parseInt(value.trim());
    }

    private static double toDouble(String value) {
        return Double.parseDouble(value.trim());
    }

    private static void createStartingData() {
        doctors.add(new Doctor(1, "D1", "111", "Cardiology", 10));
        doctors.add(new Doctor(2, "D2", "222", "Radiology", 8));
        doctors.add(new Doctor(3, "D3", "333", "Microbiology", 3));

        patients.add(new Patient(1, "P1", "6901", "a@a.com"));
        patients.add(new Patient(2, "P2", "6902", "b@b.com"));

        exams.add(new ImagingExam(1, "XRAY", 10, 30.0, 2, "X-Ray"));
        exams.add(new MicrobiologicalExam(2, "PCR", 20, 20.0, 3, "Blood"));
        exams.add(new SpecializedExam(3, "Stress", 3, 80.0, 1, "Cardiology"));

        appointments.add(new Appointment(1, 1, 1, "10:05:2026", true));
        appointments.add(new Appointment(2, 2, 2, "16:05:2026", false));
    }

    static class Doctor {
        int id;
        String name;
        String phone;
        String specialty;
        int years;

        Doctor(int id, String name, String phone, String specialty, int years) {
            this.id = id;
            this.name = name;
            this.phone = phone;
            this.specialty = specialty;
            this.years = years;
        }

        public String toString() {
            return id + " | " + name + " | " + phone + " | " + specialty + " | " + years + " years";
        }
    }

    static class Patient {
        int id;
        String name;
        String phone;
        String email;

        Patient(int id, String name, String phone, String email) {
            this.id = id;
            this.name = name;
            this.phone = phone;
            this.email = email;
        }

        public String toString() {
            return id + " | " + name + " | " + phone + " | " + email;
        }
    }

    static abstract class Exam {
        int id;
        String name;
        String category;
        int maxSlotsPerDay;
        double baseCost;
        int doctorId;

        Exam(int id, String name, String category, int maxSlotsPerDay, double baseCost, int doctorId) {
            this.id = id;
            this.name = name;
            this.category = category;
            this.maxSlotsPerDay = maxSlotsPerDay;
            this.baseCost = baseCost;
            this.doctorId = doctorId;
        }

        abstract double getCost(boolean fastResults);

        abstract String fileLine();

        public String toString() {
            return id + " | " + name + " | " + category + " | max/day=" + maxSlotsPerDay + " | cost=" + baseCost
                    + " | doctorId=" + doctorId;
        }
    }

    static class ImagingExam extends Exam {
        String machineType;

        ImagingExam(int id, String name, int maxSlotsPerDay, double baseCost, int doctorId, String machineType) {
            super(id, name, "Imaging", maxSlotsPerDay, baseCost, doctorId);
            this.machineType = machineType;
        }

        double getCost(boolean fastResults) {
            if (fastResults) {
                return baseCost * 1.10;
            }
            return baseCost;
        }

        String fileLine() {
            return "IMAGING," + id + "," + name + "," + maxSlotsPerDay + "," + baseCost + "," + doctorId + ","
                    + machineType;
        }

        public String toString() {
            return super.toString() + " | machine=" + machineType;
        }
    }

    static class MicrobiologicalExam extends Exam {
        String sampleType;

        MicrobiologicalExam(int id, String name, int maxSlotsPerDay, double baseCost, int doctorId,
                String sampleType) {
            super(id, name, "Microbiological", maxSlotsPerDay, baseCost, doctorId);
            this.sampleType = sampleType;
        }

        double getCost(boolean fastResults) {
            if (fastResults) {
                return baseCost * 1.20;
            }
            return baseCost;
        }

        String fileLine() {
            return "MICRO," + id + "," + name + "," + maxSlotsPerDay + "," + baseCost + "," + doctorId + ","
                    + sampleType;
        }

        public String toString() {
            return super.toString() + " | sample=" + sampleType;
        }
    }

    static class SpecializedExam extends Exam {
        String specialty;

        SpecializedExam(int id, String name, int maxSlotsPerDay, double baseCost, int doctorId, String specialty) {
            super(id, name, "Specialized", maxSlotsPerDay, baseCost, doctorId);
            this.specialty = specialty;
        }

        double getCost(boolean fastResults) {
            if (fastResults) {
                return baseCost * 1.30;
            }
            return baseCost;
        }

        String fileLine() {
            return "SPEC," + id + "," + name + "," + maxSlotsPerDay + "," + baseCost + "," + doctorId + ","
                    + specialty;
        }

        public String toString() {
            return super.toString() + " | specialty=" + specialty;
        }
    }

    static class Appointment {
        int id;
        int patientId;
        int examId;
        String date;
        boolean fastResults;

        Appointment(int id, int patientId, int examId, String date, boolean fastResults) {
            this.id = id;
            this.patientId = patientId;
            this.examId = examId;
            this.date = date;
            this.fastResults = fastResults;
        }

        public String toString() {
            return id + " | patientId=" + patientId + " | examId=" + examId + " | date=" + date + " | fast="
                    + fastResults;
        }
    }
}
