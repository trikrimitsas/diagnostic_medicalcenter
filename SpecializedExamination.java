/**
 * Specialized examination: medical specialty tag + 30% fast-result surcharge.
 */
public class SpecializedExamination extends Exam {
    public static final double COST_INCREASE_RATE = 0.30;

    private String examSpecialty;

    public SpecializedExamination(int examId, String categoryName, String examName, int maxSlotsPerDay, double cost,
            int doctorId, String examSpecialty) {
        super(examId, categoryName, examName, maxSlotsPerDay, cost, doctorId);
        this.examSpecialty = examSpecialty;
    }

    public String getExamSpecialty() {
        return examSpecialty;
    }

    public void setExamSpecialty(String examSpecialty) {
        this.examSpecialty = examSpecialty;
    }

    @Override
    protected double getCostIncreaseRate() {
        return COST_INCREASE_RATE;
    }

    @Override
    public double getCost(boolean fastResults) {
        double base = getBaseCost();
        if (fastResults) {
            return base + base * COST_INCREASE_RATE;
        }
        return base;
    }

    @Override
    public String toString() {
        return "SpecializedExam{id=" + getExamId() + ", name=" + getExamName() + ", category=" + getCategoryName()
                + ", specialty=" + examSpecialty + ", maxSlots=" + getMaxSlotsPerDay() + ", baseCost=" + getBaseCost()
                + ", doctorId=" + getDoctorId() + "}";
    }
}
