public class MicrobiologicalExamination extends Exam {
    public static final double COST_INCREASE_RATE = 0.20;

    private String sampleType;

    public MicrobiologicalExamination(int examId, String categoryName, String examName, int maxSlotsPerDay,
            double cost, int doctorId, String sampleType) {
        super(examId, categoryName, examName, maxSlotsPerDay, cost, doctorId);
        this.sampleType = sampleType;
    }

    public String getSampleType() {
        return sampleType;
    }

    public void setSampleType(String sampleType) {
        this.sampleType = sampleType;
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
        return "MicroExam{id=" + getExamId() + ", name=" + getExamName() + ", category=" + getCategoryName()
                + ", sample=" + sampleType + ", maxSlots=" + getMaxSlotsPerDay() + ", baseCost=" + getBaseCost()
                + ", doctorId=" + getDoctorId() + "}";
    }
}
