import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class MedicalCenterSnapshot {
    private final List<Doctor> doctors;
    private final List<Patient> patients;
    private final List<Exam> exams;
    private final List<Appointment> appointments;

    public MedicalCenterSnapshot(List<Doctor> doctors, List<Patient> patients, List<Exam> exams,
            List<Appointment> appointments) {
        this.doctors = copyOf(doctors);
        this.patients = copyOf(patients);
        this.exams = copyOf(exams);
        this.appointments = copyOf(appointments);
    }

    public List<Doctor> getDoctors() {
        return doctors;
    }

    public List<Patient> getPatients() {
        return patients;
    }

    public List<Exam> getExams() {
        return exams;
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }

    private static <T> List<T> copyOf(List<T> values) {
        if (values == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(new ArrayList<>(values));
    }
}
