import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CsvMedicalCenterStore implements MedicalCenterStore {
    private final DataStore dataStore;

    public CsvMedicalCenterStore(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    @Override
    public DataSetStatus status() {
        if (dataStore.allDataFilesMissing()) {
            return DataSetStatus.MISSING;
        }
        if (dataStore.allDataFilesExist()) {
            return DataSetStatus.COMPLETE;
        }
        return DataSetStatus.INCOMPLETE;
    }

    @Override
    public MedicalCenterSnapshot loadSnapshot() throws IOException {
        List<Doctor> doctors = new ArrayList<>();
        for (String line : DataStore.nonEmptyLines(dataStore.readAllLines(DataStore.DOCTORS_FILE))) {
            doctors.add(parseDoctor(line));
        }

        List<Patient> patients = new ArrayList<>();
        for (String line : DataStore.nonEmptyLines(dataStore.readAllLines(DataStore.PATIENTS_FILE))) {
            patients.add(parsePatient(line));
        }

        List<Exam> exams = new ArrayList<>();
        for (String line : DataStore.nonEmptyLines(dataStore.readAllLines(DataStore.EXAMS_FILE))) {
            exams.add(parseExam(line));
        }

        List<Appointment> appointments = new ArrayList<>();
        for (String line : DataStore.nonEmptyLines(dataStore.readAllLines(DataStore.APPOINTMENTS_FILE))) {
            appointments.add(parseAppointment(line));
        }

        return new MedicalCenterSnapshot(doctors, patients, exams, appointments);
    }

    @Override
    public void saveSnapshot(MedicalCenterSnapshot snapshot) throws IOException {
        List<String> doctorLines = new ArrayList<>();
        for (Doctor doctor : snapshot.getDoctors()) {
            doctorLines.add(formatDoctor(doctor));
        }
        dataStore.writeAllLines(DataStore.DOCTORS_FILE, doctorLines);

        List<String> patientLines = new ArrayList<>();
        for (Patient patient : snapshot.getPatients()) {
            patientLines.add(formatPatient(patient));
        }
        dataStore.writeAllLines(DataStore.PATIENTS_FILE, patientLines);

        List<String> examLines = new ArrayList<>();
        for (Exam exam : snapshot.getExams()) {
            examLines.add(formatExam(exam));
        }
        dataStore.writeAllLines(DataStore.EXAMS_FILE, examLines);

        List<String> appointmentLines = new ArrayList<>();
        for (Appointment appointment : snapshot.getAppointments()) {
            appointmentLines.add(formatAppointment(appointment));
        }
        dataStore.writeAllLines(DataStore.APPOINTMENTS_FILE, appointmentLines);
    }

    private static Doctor parseDoctor(String line) {
        String[] parts = split(line, 5, "doctor");
        return new Doctor(Integer.parseInt(parts[0].trim()), unescape(parts[1].trim()), unescape(parts[2].trim()),
                unescape(parts[3].trim()), Integer.parseInt(parts[4].trim()));
    }

    private static String formatDoctor(Doctor doctor) {
        return doctor.getDoctorId() + "," + escape(doctor.getDoctorName()) + "," + escape(doctor.getDoctorPhone())
                + "," + escape(doctor.getSpecialty()) + "," + doctor.getYears();
    }

    private static Patient parsePatient(String line) {
        String[] parts = split(line, 4, "patient");
        return new Patient(Integer.parseInt(parts[0].trim()), unescape(parts[1].trim()), unescape(parts[2].trim()),
                unescape(parts[3].trim()));
    }

    private static String formatPatient(Patient patient) {
        return patient.getPatientId() + "," + escape(patient.getPatientName()) + ","
                + escape(patient.getPatientPhone()) + "," + escape(patient.getEmail());
    }

    private static Appointment parseAppointment(String line) {
        String[] parts = split(line, 5, "appointment");
        return new Appointment(Integer.parseInt(parts[0].trim()), Integer.parseInt(parts[1].trim()),
                Integer.parseInt(parts[2].trim()), Boolean.parseBoolean(parts[3].trim()), unescape(parts[4].trim()));
    }

    private static String formatAppointment(Appointment appointment) {
        return appointment.getAppointmentId() + "," + appointment.getPatientId() + "," + appointment.getExamId() + ","
                + appointment.isFastResults() + "," + escape(appointment.getExamDate());
    }

    private static Exam parseExam(String line) {
        String[] parts = split(line, 8, "exam");
        String kind = parts[0].trim();
        int examId = Integer.parseInt(parts[1].trim());
        String category = unescape(parts[2].trim());
        String name = unescape(parts[3].trim());
        int maxSlots = Integer.parseInt(parts[4].trim());
        double cost = Double.parseDouble(parts[5].trim());
        int doctorId = Integer.parseInt(parts[6].trim());
        String detail = unescape(parts[7].trim());

        if ("IMAGING".equals(kind)) {
            return new ImagingExamination(examId, category, name, maxSlots, cost, doctorId, detail);
        }
        if ("MICRO".equals(kind)) {
            return new MicrobiologicalExamination(examId, category, name, maxSlots, cost, doctorId, detail);
        }
        if ("SPEC".equals(kind)) {
            return new SpecializedExamination(examId, category, name, maxSlots, cost, doctorId, detail);
        }
        throw new IllegalArgumentException("Unknown exam type: " + kind);
    }

    private static String formatExam(Exam exam) {
        if (exam instanceof ImagingExamination) {
            ImagingExamination imaging = (ImagingExamination) exam;
            return examPrefix("IMAGING", imaging) + "," + escape(imaging.getMachineType());
        }
        if (exam instanceof MicrobiologicalExamination) {
            MicrobiologicalExamination microbiological = (MicrobiologicalExamination) exam;
            return examPrefix("MICRO", microbiological) + "," + escape(microbiological.getSampleType());
        }
        if (exam instanceof SpecializedExamination) {
            SpecializedExamination specialized = (SpecializedExamination) exam;
            return examPrefix("SPEC", specialized) + "," + escape(specialized.getExamSpecialty());
        }
        throw new IllegalArgumentException("Unsupported exam type: " + exam.getClass().getName());
    }

    private static String examPrefix(String kind, Exam exam) {
        return kind + "," + exam.getExamId() + "," + escape(exam.getCategoryName()) + ","
                + escape(exam.getExamName()) + "," + exam.getMaxSlotsPerDay() + "," + exam.getBaseCost() + ","
                + exam.getDoctorId();
    }

    private static String[] split(String line, int expectedParts, String recordName) {
        String[] parts = line.split(",", -1);
        if (parts.length != expectedParts) {
            throw new IllegalArgumentException("Invalid " + recordName + " record: " + line);
        }
        return parts;
    }

    // Keep commas and ampersands stable when text fields are written to one-line records.
    private static String escape(String value) {
        if (value == null) {
            throw new IllegalArgumentException("CSV value cannot be null.");
        }
        return value.replace("&", "&amp;").replace(",", "&comma;");
    }

    private static String unescape(String value) {
        return value.replace("&comma;", ",").replace("&amp;", "&");
    }
}
