package Ex99_R1CB03;

public class Schedule {
    private int scheduleId;
    private String scheduleName;
    private int scheduleYear;
    private int scheduleMonth;

// コンストラクタ
    public Schedule(int scheduleId, String scheduleName, int scheduleYear, int scheduleMonth){
        this.scheduleId = scheduleId;
        setScheduleName(scheduleName);
        setScheduleYear(scheduleYear);
        setScheduleMonth(scheduleMonth);
    }

// setter(セッター)
    public void setScheduleName(String scheduleName) {
        this.scheduleName = scheduleName;
    }
    public void setScheduleYear(int scheduleYear) {
        this.scheduleYear = scheduleYear;
    }
    public void setScheduleMonth(int scheduleMonth) {
        this.scheduleMonth = scheduleMonth;
    }

// getter(ゲッター)
    public int getScheduleId() {
        return scheduleId;
    }
    public String getScheduleName() {
        return scheduleName;
    }
    public int getScheduleYear() {
        return scheduleYear;
    }
    public int getScheduleMonth() {
        return scheduleMonth;
    }
}
