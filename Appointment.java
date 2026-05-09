import java.util.Objects;

public class Appointment {
    private int appointmentId;
    private int patientId;
    private int examId;
    private boolean fastResults;
    private String examDate;

    public Appointment(int appointmentId, int patientId, int examId, boolean fastResults, String examDate) {
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.examId = examId;
        this.fastResults = fastResults;
        this.examDate = examDate;
    }

    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public int getExamId() {
        return examId;
    }

    public void setExamId(int examId) {
        this.examId = examId;
    }

    public boolean isFastResults() {
        return fastResults;
    }

    public void setFastResults(boolean fastResults) {
        this.fastResults = fastResults;
    }

    public String getExamDate() {
        return examDate;
    }

    public void setExamDate(String examDate) {
        this.examDate = examDate;
    }

    @Override
    public String toString() {
        return "Appointment{id=" + appointmentId + ", patientId=" + patientId + ", examId=" + examId
                + ", fastResults=" + fastResults + ", date=" + examDate + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Appointment)) {
            return false;
        }
        Appointment that = (Appointment) o;
        return appointmentId == that.appointmentId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(appointmentId);
    }
}
