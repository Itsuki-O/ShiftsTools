package Ex99_R1CB03;

import java.sql.*;
import java.util.*;

public class ShiftsToolsDAO {
    // 読み込む
    private final String DB_PATH = "jdbc:sqlite:C:\\sqlite\\SHIFTSTOOL.db";
    // スケジュールの新規作成時、挿入用
    private final String SCHEDULE_INSERT_SQL = "INSERT INTO SCHEDULE(SCHEDULE_NAME, SCHEDULE_YEAR, SCHEDULE_MONTH) VALUES(?,?,?)";
    // 保存されているスケジュールを表示するとき用
    private final String SCHEDULE_SELECT_SQL = "SELECT * FROM SCHEDULE";
    // スケジュールを削除する用
    private final String SCHEDULE_DELETE_SQL = "DELETE FROM SCHEDULE WHERE SCHEDULE_ID = ?";
    // 役職追加用
    private final String ROLE_INSERT = "INSERT INTO ROLE(ROLE_NAME, SCHEDULE_ID) VALUES(?, ?)";
    // 特定のスケジュールから役職リストを取得
    private final String ROLE_SELECT_SQL = "SELECT * FROM ROLE WHERE SCHEDULE_ID = ?";
    // 勤務形態を追加
    private final String CATEGORY_INSERT_SQL = "INSERT INTO WORK_CATEGORIES(SCHEDULE_ID, WORK_CATEGORIES_NAME, START_TIME, FINISH_TIME, WORK_TIME) VALUES(?, ?, ?, ?, ?)";
    // 特定スケジュールから勤務形態を取得
    private final String WORK_CATEGORIES_SELECT_SQL = "SELECT * FROM WORK_CATEGORIES WHERE SCHEDULE_ID = ?";
    // スタッフ追加用
    private final String STUFF_INSERT_SQL = "INSERT INTO STUFF(STUFF_NAME, SCHEDULE_ID, ROLE_ID, STUFF_SHIFT_REQ, STUFF_TYPE_ID) VALUES(?, ?, ?, ?, ?)";
    // 特定スケジュールからスタッフを取得
    private final String STUFF_SELECT_SQL = "SELECT * FROM STUFF WHERE SCHEDULE_ID = ?";
    // シフト表を保存したいときに、上書きか新たに保存かを設定できる神みたいなSQL
    private final String SHIFT_INSERT_OR_REPLACE_SQL = "INSERT OR REPLACE INTO SHIFT(SCHEDULE_ID, STUFF_ID, SHIFT_DAY, WORK_CATEGORIES_ID) VALUES(?, ?, ?, ?)";
    // シフト表を表示するときのために、名前と日にちと勤務形態を取得
    private final String SHIFT_SELECT_SQL = "SELECT STUFF_ID, SHIFT_DAY, WORK_CATEGORIES_ID FROM SHIFT WHERE SCHEDULE_ID = ?";


// シフト表新規作成時新しく作成するテーブル
    public void newShift(Schedule schedule){

        try (Connection connection = DriverManager.getConnection(DB_PATH);
            PreparedStatement ps = connection.prepareStatement(SCHEDULE_INSERT_SQL);) {

                ps.setString(1, schedule.getScheduleName());
                ps.setInt(2, schedule.getScheduleYear());
                ps.setInt(3, schedule.getScheduleMonth());
                ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e);
            System.out.println("挿入失敗");
        }
    }

// 全部のスケジュールを取得するためにリストに順次追加していく
    public List<Schedule> getAllSchedules() {

        List<Schedule> list = new ArrayList<>();

        try (Connection con = DriverManager.getConnection(DB_PATH);
            PreparedStatement ps = con.prepareStatement(SCHEDULE_SELECT_SQL);
            ResultSet rs = ps.executeQuery()) {
        
            while (rs.next()) {
                list.add(new Schedule(
                    rs.getInt("SCHEDULE_ID"), rs.getString("SCHEDULE_NAME"), rs.getInt("SCHEDULE_YEAR"), rs.getInt("SCHEDULE_MONTH")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("格納失敗");
        }
        return list;
    }

// スケジュールの削除ボタンを押したときに削除するためのメソッド
    public void deleteSchedule(int scheduleId,String scheduleName) {
        try (Connection connection = DriverManager.getConnection(DB_PATH);
            PreparedStatement ps = connection.prepareStatement(SCHEDULE_DELETE_SQL)
        ) {
            ps.setInt(1, scheduleId);
            int result = ps.executeUpdate(); // executeUpdateは更新ができたら1を返すので、それで成功したか判断

            if (result > 0) {
                System.out.println("ID: " + scheduleId + " NAME:" + scheduleName + " を削除。");
            } else {
                System.out.println("削除対象が見つかりませんでした。");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("削除失敗");
        }
    }

// そのスケジュールのシフトにいるスタッフを取得するためにこれまたリストに入れている
    public List<Stuff> getStuffByScheduleId(int scheduleId) {
        List<Stuff> list = new ArrayList<>();
        try (Connection con = DriverManager.getConnection(DB_PATH);
            PreparedStatement ps = con.prepareStatement(STUFF_SELECT_SQL)
        ) {
            ps.setInt(1, scheduleId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                int stuffId = rs.getInt("STUFF_ID");
                String stuffName = rs.getString("STUFF_NAME");
                int roleId = rs.getInt("ROLE_ID");
                int shiftReq = rs.getInt("STUFF_SHIFT_REQ");
                int stuffTypeId = rs.getInt("STUFF_TYPE_ID");

                Stuff st;
                // スタッフ（正社員かパート）によって作るタイプ変更（継承）
                if (stuffTypeId == 1) {
                    st = new FullTimeStuff(stuffId, stuffName, 0, roleId, shiftReq, stuffTypeId);
                } else {
                    st = new PartTimeStuff(stuffId, stuffName, 0, roleId, shiftReq, stuffTypeId);
                }
                list.add(st);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("取得失敗");
        }
        return list;
    }

    
// そのスケジュールに関連する役職だけを取得（スタッフ追加用）
    public Map<Integer, String> getRoles(int scheduleId) {
        
        Map<Integer, String> roles = new LinkedHashMap<>();
        
        try (Connection con = DriverManager.getConnection(DB_PATH);
        PreparedStatement ps = con.prepareStatement(ROLE_SELECT_SQL)) {
            ps.setInt(1, scheduleId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                roles.put(rs.getInt("ROLE_ID"), 
                rs.getString("ROLE_NAME"));
            }
            
        } catch (SQLException e) { 
            e.printStackTrace(); 
            System.out.println("取得失敗");
        }
        return roles;
    }

// 役職の追加
    public void addRole(String name, int scheduleId) {
        try (Connection con = DriverManager.getConnection(DB_PATH);
            PreparedStatement ps = con.prepareStatement(ROLE_INSERT)) {

            ps.setString(1, name);
            ps.setInt(2, scheduleId);
            ps.executeUpdate();

        } catch (SQLException e) { 
            e.printStackTrace();
            System.out.println("挿入失敗");
        }
    }

// 勤務形態の追加（WORK_CATEGORIESテーブル）
    public void addCategory(int scheduleId, String name, String start, String end) {
        try (Connection con = DriverManager.getConnection(DB_PATH);
            PreparedStatement ps = con.prepareStatement(CATEGORY_INSERT_SQL)) {

            ps.setInt(1, scheduleId);
            ps.setString(2, name);
            ps.setString(3, start);
            ps.setString(4, end);

            WorkCategories workCategories = new WorkCategories(scheduleId, name, start, end);
            double workTime = workCategories.StringTimeChange(start, end);
            ps.setInt(5, (int)workTime);
            ps.executeUpdate();

        } catch (SQLException e) { 
            e.printStackTrace(); 
            System.out.println("挿入失敗");
        }
    }

// スタッフの追加
    public void addStuff(String name, int scheduleId, int roleId, int reqDays, int stuffTypeId) {
        try (Connection con = DriverManager.getConnection(DB_PATH);
            PreparedStatement ps = con.prepareStatement(STUFF_INSERT_SQL)) {

            ps.setString(1, name);
            ps.setInt(2, scheduleId);
            ps.setInt(3, roleId);
            ps.setInt(4, reqDays);
            ps.setInt(5, stuffTypeId);
            ps.executeUpdate();

        } catch (SQLException e) { 
            e.printStackTrace();
            System.out.println("挿入失敗");
        }
    }

// 登録済みの勤務形態（早番など）を取得する
    public Map<Integer, WorkCategories> getWorkCategories(int scheduleId) {
        Map<Integer, WorkCategories> categories = new LinkedHashMap<>();
        try (Connection con = DriverManager.getConnection(DB_PATH);
            PreparedStatement ps = con.prepareStatement(WORK_CATEGORIES_SELECT_SQL)) {

            ps.setInt(1, scheduleId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("WORK_CATEGORIES_ID");
                String name = rs.getString("WORK_CATEGORIES_NAME");
                String startTime = rs.getString("START_TIME");
                String finishTime = rs.getString("FINISH_TIME");
                categories.put(id, new WorkCategories(id, name, startTime, finishTime));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }




// シフトの保存(既にデータがあれば上書き、なければ挿入)
    public void saveShift(int scheduleId, int stuffId, int day, Integer categoryId) {
        try (Connection con = DriverManager.getConnection(DB_PATH);
            PreparedStatement ps = con.prepareStatement(SHIFT_INSERT_OR_REPLACE_SQL)) {

            ps.setInt(1, scheduleId);
            ps.setInt(2, stuffId);
            ps.setInt(3, day);

            if (categoryId == null || categoryId == -1) {
                ps.setNull(4, Types.INTEGER); // 空の指定
            } else { 
                ps.setInt(4, categoryId);
            }

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace(); 
            System.out.println("保存失敗");
        }
    }

// 保存済みシフトの取得(スタッフID-日 をキー、カテゴリIDのマップ)
    public Map<String, Integer> getSavedShifts(int scheduleId) {

        Map<String, Integer> shiftMap = new HashMap<>();

        try (Connection con = DriverManager.getConnection(DB_PATH);
            PreparedStatement ps = con.prepareStatement(SHIFT_SELECT_SQL)) {

            ps.setInt(1, scheduleId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String key = rs.getInt("STUFF_ID") + "-" + rs.getInt("SHIFT_DAY");
                shiftMap.put(key, rs.getInt("WORK_CATEGORIES_ID"));
            }

        } catch (SQLException e) { 
            e.printStackTrace();
            System.out.println("取得失敗");
        }
        return shiftMap;
    }

}
