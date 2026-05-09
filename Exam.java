public abstract class Exam {
    private int examId;
    private String categoryName;
    private String examName;
    private int maxSlotsPerDay;
    private double cost;
    private int doctorId;

    protected Exam(int examId, String categoryName, String examName, int maxSlotsPerDay, double cost, int doctorId) {
        this.examId = examId;
        this.categoryName = categoryName;
        this.examName = examName;
        this.maxSlotsPerDay = maxSlotsPerDay;
        this.cost = cost;
        this.doctorId = doctorId;
    }

    public int getExamId() {
        return examId;
    }

    public void setExamId(int examId) {
        this.examId = examId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getExamName() {
        return examName;
    }

    public void setExamName(String examName) {
        this.examName = examName;
    }

    public int getMaxSlotsPerDay() {
        return maxSlotsPerDay;
    }

    public void setMaxSlotsPerDay(int maxSlotsPerDay) {
        this.maxSlotsPerDay = maxSlotsPerDay;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public abstract double getCost(boolean fastResults);

    public double getBaseCost() {
        return cost;
    }

    protected abstract double getCostIncreaseRate();

}
