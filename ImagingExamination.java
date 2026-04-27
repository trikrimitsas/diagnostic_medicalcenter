/**
 * Imaging examination: machine type + 10% fast-result surcharge.
 */
public class ImagingExamination extends Exam {
    public static final double COST_INCREASE_RATE = 0.10;

    private String machineType;

    public ImagingExamination(int examId, String categoryName, String examName, int maxSlotsPerDay, double cost,
            int doctorId, String machineType) {
        super(examId, categoryName, examName, maxSlotsPerDay, cost, doctorId);
        this.machineType = machineType;
    }

    public String getMachineType() {
        return machineType;
    }

    public void setMachineType(String machineType) {
        this.machineType = machineType;
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
        return "ImagingExam{id=" + getExamId() + ", name=" + getExamName() + ", category=" + getCategoryName()
                + ", machine=" + machineType + ", maxSlots=" + getMaxSlotsPerDay() + ", baseCost=" + getBaseCost()
                + ", doctorId=" + getDoctorId() + "}";
    }
}
