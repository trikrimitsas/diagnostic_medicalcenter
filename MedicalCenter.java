import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * In-memory domain: four collections and business rules.
 */
public class MedicalCenter {
    private final List<Doctor> doctors = new ArrayList<>();
    private final List<Patient> patients = new ArrayList<>();
    private final List<Exam> exams = new ArrayList<>();
    private final List<Appointment> appointments = new ArrayList<>();

    public static MedicalCenter fromSnapshot(MedicalCenterSnapshot snapshot) {
        MedicalCenter center = new MedicalCenter();
        for (Doctor doctor : snapshot.getDoctors()) {
            center.addDoctor(doctor);
        }
        for (Patient patient : snapshot.getPatients()) {
            center.addPatient(patient);
        }
        for (Exam exam : snapshot.getExams()) {
            center.addExam(exam);
        }
        for (Appointment appointment : snapshot.getAppointments()) {
            center.addAppointment(appointment);
        }
        center.validateIntegrity();
        return center;
    }

    public MedicalCenterSnapshot toSnapshot() {
        return new MedicalCenterSnapshot(doctors, patients, exams, appointments);
    }

    public List<Doctor> getDoctors() {
        return Collections.unmodifiableList(doctors);
    }

    public List<Patient> getPatients() {
        return Collections.unmodifiableList(patients);
    }

    public List<Exam> getExams() {
        return Collections.unmodifiableList(exams);
    }

    public List<Appointment> getAppointments() {
        return Collections.unmodifiableList(appointments);
    }

    public Doctor findDoctorById(int id) {
        for (Doctor d : doctors) {
            if (d.getDoctorId() == id) {
                return d;
            }
        }
        return null;
    }

    public Patient findPatientById(int id) {
        for (Patient p : patients) {
            if (p.getPatientId() == id) {
                return p;
            }
        }
        return null;
    }

    public Exam findExamById(int id) {
        for (Exam e : exams) {
            if (e.getExamId() == id) {
                return e;
            }
        }
        return null;
    }

    public Appointment findAppointmentById(int id) {
        for (Appointment a : appointments) {
            if (a.getAppointmentId() == id) {
                return a;
            }
        }
        return null;
    }

    public List<Exam> getExamsSortedByName() {
        return exams.stream()
                .sorted(Comparator.comparing(Exam::getExamName, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    public List<Exam> getExamsForDoctor(int doctorId) {
        List<Exam> out = new ArrayList<>();
        for (Exam e : exams) {
            if (e.getDoctorId() == doctorId) {
                out.add(e);
            }
        }
        return out;
    }

    public List<Appointment> getAppointmentsForPatient(int patientId) {
        List<Appointment> out = new ArrayList<>();
        for (Appointment a : appointments) {
            if (a.getPatientId() == patientId) {
                out.add(a);
            }
        }
        return out;
    }

    public List<Appointment> getAppointmentsForExam(int examId) {
        List<Appointment> out = new ArrayList<>();
        for (Appointment a : appointments) {
            if (a.getExamId() == examId) {
                out.add(a);
            }
        }
        return out;
    }

    public List<Appointment> getAppointmentsForDoctor(int doctorId) {
        List<Appointment> out = new ArrayList<>();
        for (Appointment a : appointments) {
            Exam e = findExamById(a.getExamId());
            if (e != null && e.getDoctorId() == doctorId) {
                out.add(a);
            }
        }
        return out;
    }

    public List<Appointment> getAppointmentsOnDate(String examDate) {
        List<Appointment> out = new ArrayList<>();
        for (Appointment a : appointments) {
            if (Objects.equals(normalizeDate(a.getExamDate()), normalizeDate(examDate))) {
                out.add(a);
            }
        }
        return out;
    }

    public int countAppointmentsOnDateForExam(int examId, String examDate) {
        String nd = normalizeDate(examDate);
        int c = 0;
        for (Appointment a : appointments) {
            if (a.getExamId() == examId && Objects.equals(normalizeDate(a.getExamDate()), nd)) {
                c++;
            }
        }
        return c;
    }

    private static String normalizeDate(String d) {
        return d == null ? "" : d.trim().replace('/', ':');
    }

    public int nextDoctorId() {
        return doctors.stream().mapToInt(Doctor::getDoctorId).max().orElse(0) + 1;
    }

    public int nextPatientId() {
        return patients.stream().mapToInt(Patient::getPatientId).max().orElse(0) + 1;
    }

    public int nextExamId() {
        return exams.stream().mapToInt(Exam::getExamId).max().orElse(0) + 1;
    }

    public int nextAppointmentId() {
        return appointments.stream().mapToInt(Appointment::getAppointmentId).max().orElse(0) + 1;
    }

    public void addDoctor(Doctor d) {
        validateDoctor(d);
        if (findDoctorById(d.getDoctorId()) != null) {
            throw new IllegalArgumentException("Duplicate doctor id: " + d.getDoctorId());
        }
        doctors.add(d);
    }

    public void addPatient(Patient p) {
        validatePatient(p);
        if (findPatientById(p.getPatientId()) != null) {
            throw new IllegalArgumentException("Duplicate patient id: " + p.getPatientId());
        }
        patients.add(p);
    }

    public void addExam(Exam e) {
        validateExam(e);
        if (findExamById(e.getExamId()) != null) {
            throw new IllegalArgumentException("Duplicate exam id: " + e.getExamId());
        }
        exams.add(e);
    }

    public void addAppointment(Appointment a) {
        validateAppointment(a);
        if (findAppointmentById(a.getAppointmentId()) != null) {
            throw new IllegalArgumentException("Duplicate appointment id: " + a.getAppointmentId());
        }
        Exam exam = findExamById(a.getExamId());
        int booked = countAppointmentsOnDateForExam(a.getExamId(), a.getExamDate());
        if (exam != null && booked >= exam.getMaxSlotsPerDay()) {
            throw new IllegalArgumentException("No slots left for exam " + a.getExamId() + " on " + a.getExamDate());
        }
        appointments.add(a);
    }

    public boolean removeAppointmentById(int appointmentId) {
        return appointments.removeIf(a -> a.getAppointmentId() == appointmentId);
    }

    /**
     * Seed sample data when no data files exist (first run).
     */
    public void seedInitialData() {
        doctors.clear();
        doctors.add(new Doctor(1, "D1", "111", "Cardiology", 10));
        doctors.add(new Doctor(2, "D2", "222", "Radiology", 8));
        doctors.add(new Doctor(3, "D3", "333", "Microbiology", 3));
        doctors.add(new Doctor(4, "D4", "444", "Neurology", 2));

        patients.clear();
        patients.add(new Patient(1, "P1", "6901", "a@a.com"));
        patients.add(new Patient(2, "P2", "6902", "b@b.com"));
        patients.add(new Patient(3, "P3", "6903", "c@c.com"));

        exams.clear();
        exams.add(new ImagingExamination(1, "Imaging", "XRAY", 10, 30.0, 2, "X-Ray"));
        exams.add(new MicrobiologicalExamination(2, "Microbiological", "PCR", 20, 20.0, 3, "Blood"));
        exams.add(new SpecializedExamination(3, "Specialized", "Stress", 3, 80.0, 1, "Cardiology"));
        exams.add(new SpecializedExamination(4, "Specialized", "Holter", 5, 50.0, 1, "Cardiology"));
        exams.add(new SpecializedExamination(5, "Specialized", "EEG", 10, 20.0, 4, "Neurology"));
        exams.add(new ImagingExamination(6, "Imaging", "MRI_Scan", 5, 100.0, 2, "MRI"));
        exams.add(new MicrobiologicalExamination(7, "Microbiological", "Urine_Panel", 15, 25.0, 3, "Urine"));
        exams.add(new SpecializedExamination(8, "Specialized", "SPT", 12, 40.0, 4, "Pulmonology"));
        exams.add(new ImagingExamination(9, "Imaging", "CT_Chest", 6, 90.0, 2, "CT"));
        exams.add(new MicrobiologicalExamination(10, "Microbiological", "Throat_Culture", 18, 35.0, 3, "Swab"));

        appointments.clear();
        appointments.add(new Appointment(1, 1, 1, true, "10:05:2026"));
        appointments.add(new Appointment(2, 2, 2, false, "16:05:2026"));
        appointments.add(new Appointment(3, 1, 2, false, "11:05:2026"));
        appointments.add(new Appointment(4, 1, 3, true, "16:05:2026"));
    }

    public double revenueForAppointment(Appointment a) {
        Exam e = findExamById(a.getExamId());
        if (e == null) {
            return 0.0;
        }
        return e.getCost(a.isFastResults());
    }

    private void validateIntegrity() {
        Set<Integer> doctorIds = new HashSet<>();
        for (Doctor d : doctors) {
            validateDoctor(d);
            if (!doctorIds.add(d.getDoctorId())) {
                throw new IllegalArgumentException("Duplicate doctor id: " + d.getDoctorId());
            }
        }

        Set<Integer> patientIds = new HashSet<>();
        for (Patient p : patients) {
            validatePatient(p);
            if (!patientIds.add(p.getPatientId())) {
                throw new IllegalArgumentException("Duplicate patient id: " + p.getPatientId());
            }
        }

        Set<Integer> examIds = new HashSet<>();
        for (Exam e : exams) {
            validateExam(e);
            if (!examIds.add(e.getExamId())) {
                throw new IllegalArgumentException("Duplicate exam id: " + e.getExamId());
            }
        }

        Set<Integer> appointmentIds = new HashSet<>();
        for (Appointment a : appointments) {
            validateAppointment(a);
            if (!appointmentIds.add(a.getAppointmentId())) {
                throw new IllegalArgumentException("Duplicate appointment id: " + a.getAppointmentId());
            }
        }

        for (Exam e : exams) {
            for (Appointment a : appointments) {
                if (a.getExamId() == e.getExamId()
                        && countAppointmentsOnDateForExam(e.getExamId(), a.getExamDate()) > e.getMaxSlotsPerDay()) {
                    throw new IllegalArgumentException("Too many appointments for exam " + e.getExamId()
                            + " on " + a.getExamDate());
                }
            }
        }
    }

    private void validateDoctor(Doctor d) {
        if (d == null) {
            throw new IllegalArgumentException("Doctor cannot be null.");
        }
        if (d.getDoctorId() <= 0) {
            throw new IllegalArgumentException("Doctor id must be positive.");
        }
    }

    private void validatePatient(Patient p) {
        if (p == null) {
            throw new IllegalArgumentException("Patient cannot be null.");
        }
        if (p.getPatientId() <= 0) {
            throw new IllegalArgumentException("Patient id must be positive.");
        }
    }

    private void validateExam(Exam e) {
        if (e == null) {
            throw new IllegalArgumentException("Exam cannot be null.");
        }
        if (e.getExamId() <= 0) {
            throw new IllegalArgumentException("Exam id must be positive.");
        }
        if (findDoctorById(e.getDoctorId()) == null) {
            throw new IllegalArgumentException("Exam " + e.getExamId() + " references missing doctor " + e.getDoctorId());
        }
        if (e.getMaxSlotsPerDay() <= 0) {
            throw new IllegalArgumentException("Exam max slots must be positive.");
        }
    }

    private void validateAppointment(Appointment a) {
        if (a == null) {
            throw new IllegalArgumentException("Appointment cannot be null.");
        }
        if (a.getAppointmentId() <= 0) {
            throw new IllegalArgumentException("Appointment id must be positive.");
        }
        if (findPatientById(a.getPatientId()) == null) {
            throw new IllegalArgumentException("Appointment " + a.getAppointmentId()
                    + " references missing patient " + a.getPatientId());
        }
        if (findExamById(a.getExamId()) == null) {
            throw new IllegalArgumentException("Appointment " + a.getAppointmentId()
                    + " references missing exam " + a.getExamId());
        }
        if (normalizeDate(a.getExamDate()).isEmpty()) {
            throw new IllegalArgumentException("Appointment date cannot be empty.");
        }
    }
}
