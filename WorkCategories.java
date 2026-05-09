package Ex99_R1CB03;
 
import java.time.*;
 
public class WorkCategories {
    private int workCategoriesId;
    private String workCategoriesName;
    private String workCategoriesStart;
    private String workCategoriesEnd;
    private double workCategoriesHours;
 
    // コンストラクタ
    public WorkCategories(int workCategoriesId, String workCategoriesName, String workCategoriesStart, String workCategoriesEnd){
        this.workCategoriesId = workCategoriesId;
        this.workCategoriesName = workCategoriesName;
        this.workCategoriesStart = workCategoriesStart;
        this.workCategoriesEnd = workCategoriesEnd;
        this.workCategoriesHours = this.StringTimeChange(workCategoriesStart, workCategoriesEnd);
    }
 
    // 時間計算
    public double StringTimeChange(String start, String end) {
        try {
            LocalTime startTime = LocalTime.parse(start);
            LocalTime endTime = LocalTime.parse(end);
           
            // 終了時間が開始時間より前の場合翌日扱い
            Duration duration = Duration.between(startTime, endTime);
            if (duration.isNegative()) {
                duration = duration.plusDays(1);
            }
           
            return duration.toMinutes() / 60.0;
            
        } catch (DateTimeException e) {
            e.getStackTrace();
            System.out.println("時間変換失敗: " + start + " - " + end);
            return 0.0;
        }
    }
 
    // getter
    public int getWorkCategoriesId() {
        return workCategoriesId;
    }
    public String getWorkCategoriesName() {
        return workCategoriesName;
    }
    public String getWorkCategoriesStart() {
        return workCategoriesStart;
    }
    public String getWorkCategoriesEnd() {
        return workCategoriesEnd;
    }
    public double getWorkCategoriesHours() {
        return workCategoriesHours;
    }
}