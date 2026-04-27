import java.util.Objects;

/**
 * Entity: patient (primary key: patientId).
 */
public class Patient {
    private int patientId;
    private String patientName;
    private String patientPhone;
    private String email;

    public Patient(int patientId, String patientName, String patientPhone, String email) {
        this.patientId = patientId;
        this.patientName = patientName;
        this.patientPhone = patientPhone;
        this.email = email;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientPhone() {
        return patientPhone;
    }

    public void setPatientPhone(String patientPhone) {
        this.patientPhone = patientPhone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Patient{id=" + patientId + ", name=" + patientName + ", phone=" + patientPhone + ", email=" + email + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Patient)) {
            return false;
        }
        Patient patient = (Patient) o;
        return patientId == patient.patientId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(patientId);
    }
}
