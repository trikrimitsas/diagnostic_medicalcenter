import java.util.Objects;

public class Doctor {
    private int doctorId;
    private String doctorName;
    private String doctorPhone;
    private String specialty;
    private int years;

    public Doctor(int doctorId, String doctorName, String doctorPhone, String specialty, int years) {
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.doctorPhone = doctorPhone;
        this.specialty = specialty;
        this.years = years;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getDoctorPhone() {
        return doctorPhone;
    }

    public void setDoctorPhone(String doctorPhone) {
        this.doctorPhone = doctorPhone;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public int getYears() {
        return years;
    }

    public void setYears(int years) {
        this.years = years;
    }

    @Override
    public String toString() {
        return "Doctor{id=" + doctorId + ", name=" + doctorName + ", phone=" + doctorPhone
                + ", specialty=" + specialty + ", years=" + years + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Doctor)) {
            return false;
        }
        Doctor doctor = (Doctor) o;
        return doctorId == doctor.doctorId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(doctorId);
    }
}
