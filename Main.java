package Ex99_R1CB03;


import java.util.*;

import java.io.File;
import java.awt.Desktop;
import java.time.*;
import java.time.format.*;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class Main extends Application{
    
    private ShiftsToolsDAO dao = new ShiftsToolsDAO();
    private Schedule schedule;
    public static void main(String[] args) {
        launch();
    }
    @Override
    public void start(Stage stage){ 
        showMainScene(stage);
    }


// シフト表新規作成画面構成
    private void createScene(Stage stage){
        Label shiftNameLabel = new Label("シフト表の名前を記入してください");
        Label shiftNameCarefulLabel = new Label("※名前は1~10文字で記入してください");
        shiftNameCarefulLabel.setTextFill(Color.RED);
        TextField shiftNameField = new TextField();

        Button clearButton = new Button("クリア");
        clearButton.setOnAction((ActionEvent event) -> {
            shiftNameField.setText("");
        });

        Label selectLabel = new Label("年と月を選択してください");
        LocalDateTime t1 = LocalDateTime.now();
        int nowYear = t1.getYear();
        ComboBox<Integer> yearCmb = new ComboBox<>();
        yearCmb.getItems().addAll(null,nowYear,nowYear+1,nowYear+2,nowYear+3,nowYear+4);
        Label yearLabel = new Label("年");
        ComboBox<Integer> monthCmb = new ComboBox<>();
        monthCmb.getItems().addAll(null,1,2,3,4,5,6,7,8,9,10,11,12);
        Label monthLabel = new Label("月");

        Button dicisionButton = new Button("決定");
        dicisionButton.setOnAction((ActionEvent event) -> {
            if((shiftNameField.getText().length()) == 0){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("入力エラー");
                alert.setHeaderText("名前が入力されていません");
                alert.setContentText("名前は一文字以上入力してください");
                alert.showAndWait();
                return;
            } else if (yearCmb.getValue() == null || monthCmb.getValue() == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("選択エラー");
                alert.setHeaderText("年月が選択されていません");
                alert.setContentText("年と月を選択してください");
                alert.showAndWait();
                return;
            } else if (shiftNameField.getText().length() >= 11) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("入力エラー");
                alert.setHeaderText("名前が長すぎます");
                alert.setContentText("名前は10文字以内で入力してください");
                alert.showAndWait();
                return;
            }
            schedule = new Schedule(0, shiftNameField.getText(), yearCmb.getValue(), monthCmb.getValue());
            dao.newShift(schedule);
            showMainScene(stage);
        });
        
        GridPane grid = new GridPane();
        grid.add(shiftNameLabel, 0, 0);
        grid.add(shiftNameCarefulLabel, 0, 1);
        grid.add(shiftNameField, 0, 2);
        grid.add(clearButton, 1, 2);
        grid.add(selectLabel, 0, 3);
        GridPane grid2 = new GridPane();
        grid2.add(yearCmb, 0, 0);
        grid2.add(yearLabel, 2, 0);
        grid2.add(monthCmb, 3, 0);
        grid2.add(monthLabel, 4, 0);
        grid2.add(dicisionButton, 0, 1);

        VBox root = new VBox(grid,grid2);
        Scene createScene = new Scene(root, 400,250);
        stage.setScene(createScene);
        stage.setTitle("スケジュール新規作成");
        stage.show();
    }

// 最初に表示するスケジュール管理画面構成
    private void showMainScene(Stage stage){    
        
        Button newButton = new Button("✚ 新規作成");
        newButton.setOnAction((ActionEvent event) -> {
            createScene(stage);
        });
        
        // スケジュール一覧の取得と表示
        VBox scheduleVB = new VBox(5);
        List<Schedule> allSchedules = dao.getAllSchedules(); // DBから全件取得
        
        for (Schedule s : allSchedules) {
            Label nameLabel = new Label("・ " + s.getScheduleName() + " (" + s.getScheduleYear() + "/" + s.getScheduleMonth() + ")");
            
            Button editButton = new Button("編集");
            editButton.setOnAction((ActionEvent event) -> {
                showEditScene(stage, s); // 編集画面へ、選択されたスケジュール情報を渡す
            });
            
            Button deleteButton = new Button("削除");
            deleteButton.setOnAction((ActionEvent event) -> {
                dao.deleteSchedule(s.getScheduleId(),s.getScheduleName()); // dbから削除
                showMainScene(stage); // 再表示
            });
            
            HBox row = new HBox(10);
            row.getChildren().addAll(nameLabel, editButton, deleteButton);
            scheduleVB.getChildren().add(row);
        }
        
        VBox root = new VBox(10);
        root.getChildren().addAll(newButton, scheduleVB);
        stage.setScene(new Scene(root, 400, 500));
        stage.setTitle("スケジュール管理");
        stage.show();
    }

// スケジュール編集
    @SuppressWarnings("unused")
    private void showEditScene(Stage stage, Schedule schedule) {
        this.schedule = schedule;
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));

        // タイトル
        BorderPane bp = new BorderPane();
        Label title = new Label(
            schedule.getScheduleName() + " シフト編集 (" + schedule.getScheduleYear() + "年" + schedule.getScheduleMonth() + "月)"
        );
        title.setFont(Font.font("Verdana", FontWeight.BOLD, 20)); // 書体、太字、大きさ

        bp.setLeft(title);
        // 追加ボタン
        HBox threeSetButtons = new HBox(10);
        Button addRoleButton = new Button("✚役職追加");
        addRoleButton.setOnAction((ActionEvent event) -> {
            showRoleDialog(schedule.getScheduleId());
            showEditScene(stage, schedule);
        });
        Button addCategoryButton = new Button("✚勤務形態追加");
        addCategoryButton.setOnAction((ActionEvent event) -> {
            showCategoryDialog(schedule.getScheduleId());
            showEditScene(stage, schedule);
        });
        Button addStuffButton = new Button("✚スタッフ追加");
        addStuffButton.setOnAction((ActionEvent event) -> {
            showStuffDialog(schedule.getScheduleId());
            showEditScene(stage, schedule);
        });
        threeSetButtons.getChildren().addAll(addRoleButton, addCategoryButton, addStuffButton);

        // 勤務時間詳細
        Map<Integer, WorkCategories> categoriesMap = dao.getWorkCategories(schedule.getScheduleId());
        HBox timeDetailsArea = new HBox(15);
        // cssで背景色、間隔、HBox境界線色
        timeDetailsArea.setStyle("-fx-background-color: #f4f4f4; -fx-padding: 10; -fx-border-color: #ccc;");
        Label workCategoriesLabel = new Label("【勤務形態一覧】");
        timeDetailsArea.getChildren().add(workCategoriesLabel);
        for (WorkCategories wc : categoriesMap.values()) {

            Label oneWorkCategorieLabel = new Label(
                wc.getWorkCategoriesName() + "：" + wc.getWorkCategoriesHours() + "h" + "【"+ wc.getWorkCategoriesStart() + "~" + wc.getWorkCategoriesEnd() + "】"
            );
            timeDetailsArea.getChildren().add(oneWorkCategorieLabel);
        }

        // シフト用グリッドと週計算
        GridPane grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(9);

        List<Stuff> stuffList = dao.getStuffByScheduleId(schedule.getScheduleId());
        Map<String, Integer> savedShifts = dao.getSavedShifts(schedule.getScheduleId());
        int daysInMonth = YearMonth.of(schedule.getScheduleYear(), schedule.getScheduleMonth()).lengthOfMonth();

        // ヘッダー作成(日付と週合計列)
        Label stuff = new Label("スタッフ名");
        stuff.setStyle("-fx-font-weight: bold;");
        grid.add(stuff, 0, 0);
        int GridCounter = 1;
        
        // 週の区切りを特定するためのリスト
        List<Integer> weeknessArrayList = new ArrayList<>(); 

        for (int d = 1; d <= daysInMonth; d++) {
            LocalDate date = LocalDate.of(schedule.getScheduleYear(), schedule.getScheduleMonth(), d);
            String dayOfWeek = date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.JAPANESE);
            // 週の曜日の名前を一文字日本語で取得
            
            Label dayLabel = new Label(d + "日 (" + dayOfWeek + ")");
            dayLabel.setAlignment(Pos.CENTER);
            dayLabel.setMinWidth(65);
            if (date.getDayOfWeek() == DayOfWeek.SATURDAY) {
                dayLabel.setTextFill(Color.BLUE);
            }
            if (date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                dayLabel.setTextFill(Color.RED);
            }
            dayLabel.setStyle("-fx-font-weight: bold;");
            grid.add(dayLabel, GridCounter++, 0);

            // 日曜日、または月末の場合に「週合計」列を挿入
            if (date.getDayOfWeek() == DayOfWeek.SUNDAY || d == daysInMonth) {
                Label weekTotalHeader = new Label("週合計");
                weekTotalHeader.setMinWidth(60);
                weekTotalHeader.setStyle("-fx-font-weight: bold;");
                grid.add(weekTotalHeader, GridCounter, 0);
                weeknessArrayList.add(GridCounter); // どこに週合計があるか記録
                GridCounter++;
            }
        }

        // スタッフごとのデータ行
        for (int row = 0; row < stuffList.size(); row++) {
            Stuff stuffs = stuffList.get(row);
            int limit = stuffs.getWorkHour(); // 週の上限(40or20)(多態性)
            
            String displayName = stuffs.getName();

            if (stuffs instanceof FullTimeStuff) {
                displayName += "[正]";
            } else {
                displayName += "[バ]";
            }

            Label nameLabel = new Label(displayName);
            nameLabel.setStyle("-fx-font-weight: bold;");
            grid.add(nameLabel, 0, row + 1);

            int colCounter = 1;
            List<ComboBox<String>> currentWeekCombos = new ArrayList<>();
            List<Label> weekTotalLabels = new ArrayList<>();
            
            // 各週の合計値を保持するLabelを先に生成して配置
            for (Integer weekCol : weeknessArrayList) {
                Label weekLimitLabel = new Label("0.0 / " + limit + " h");
                weekLimitLabel.setStyle("-fx-font-weight: bold;");
                weekTotalLabels.add(weekLimitLabel);
            }

            int weekIdx = 0;
            for (int d = 1; d <= daysInMonth; d++) {
                // 日にち
                LocalDate date = LocalDate.of(schedule.getScheduleYear(), schedule.getScheduleMonth(), d);
                
                ComboBox<String> shiftCombo = new ComboBox<>();
                shiftCombo.setPrefWidth(85);
                shiftCombo.getItems().add(""); 
                categoriesMap.values().forEach(wc -> // forEachは次に続く文を繰り返し実行するもの
                    shiftCombo.getItems().add(wc.getWorkCategoriesName())
                );

                // 当時保存していたcomboboxの中身を出すための復元メソッド
                String key = stuffs.getId() + "-" + d;
                if (savedShifts.containsKey(key)) {
                    int savedId = savedShifts.get(key);
                    if (categoriesMap.containsKey(savedId)) {
                        shiftCombo.setValue(categoriesMap.get(savedId).getWorkCategoriesName());
                    }
                }

                currentWeekCombos.add(shiftCombo);
                // 固定したい
                final List<ComboBox<String>> THIS_WEEK_LIST = new ArrayList<>(currentWeekCombos);
                final Label THIS_WEEK_TOTAL_LABEL = weekTotalLabels.get(weekIdx);
                final int DAY = d;

                // シフト選択時時間整合性確認システム
                // シフトのweekListはcoboboxのその瞬間の中身であり、
                // その中身を取り出し、からじゃなかったらそれと一緒の値をdbから取り出して、その時間分をトータル時間に加算
                shiftCombo.setOnAction(e -> {
                    double weekTotal = 0;
                    for (ComboBox<String> cb : THIS_WEEK_LIST) {
                        String val = cb.getValue();
                        if (val != null && !val.isEmpty()) {
                            for (WorkCategories wc : categoriesMap.values()) {
                                if (wc.getWorkCategoriesName().equals(val)) {
                                    weekTotal += wc.getWorkCategoriesHours();
                                }
                            }
                        }
                    }

                    if (weekTotal > limit) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("週労働時間超過");
                        alert.setHeaderText(null);
                        alert.setContentText(stuffs.getName() + "さんの今週の合計が " + limit + " h を超えています！");
                        alert.showAndWait();
                        shiftCombo.setValue(""); // 選択をリセット
                    } else {
                        THIS_WEEK_TOTAL_LABEL.setText(String.format("%.1f / %d h", weekTotal, limit));
                        // DB保存
                        int categoryId = 0;
                        String selectCombo = shiftCombo.getValue();
                        if (selectCombo != null && !selectCombo.isEmpty()) {
                            for (WorkCategories wc : categoriesMap.values()) {
                                if (wc.getWorkCategoriesName().equals(selectCombo)) {
                                    categoryId = wc.getWorkCategoriesId();
                                }
                            }
                        }
                        dao.saveShift(schedule.getScheduleId(), stuffs.getId(), DAY, categoryId);
                    }
                });

                grid.add(shiftCombo, colCounter++, row + 1);

                // 週の終わり日曜日または月末
                if (date.getDayOfWeek() == DayOfWeek.SUNDAY || d == daysInMonth) {
                    grid.add(THIS_WEEK_TOTAL_LABEL, colCounter++, row + 1);
                    
                    // 初回表示時の合計計算
                    double initialTotal = 0;
                    for(ComboBox<String> cb : currentWeekCombos) {
                        String val = cb.getValue();
                        if(val != null) {
                            for(WorkCategories wc : categoriesMap.values()) {
                                if(wc.getWorkCategoriesName().equals(val)) {
                                    initialTotal += wc.getWorkCategoriesHours();
                                }
                            }
                        }
                    }
                    THIS_WEEK_TOTAL_LABEL.setText(String.format("%.1f / %dh", initialTotal, limit));
                    
                    currentWeekCombos.clear(); // 次の週へ
                    weekIdx++;
                }
            }
        }

        ScrollPane scrollPane = new ScrollPane(grid);
        scrollPane.setMinHeight(400);
        scrollPane.setPrefHeight(400);
        scrollPane.setMaxHeight(400);
        scrollPane.setFitToHeight(true);
        scrollPane.setPadding(new Insets(10));

        Button backButtom = new Button("完了して戻る");
        backButtom.setPrefSize(100, 20);
        backButtom.setOnAction((ActionEvent event) -> {
            showMainScene(stage);
        });


        Button helpButton = new Button("？");
        helpButton.setFont(new Font(15));
        bp.setRight(helpButton);

        helpButton.setOnAction((ActionEvent event) -> {
            File file = new File("Z:\\BP2\\Basic2\\src\\Ex99_R1CB03\\ReadMe.txt");
            try {
                if (file.exists()) {
                    Desktop.getDesktop().open(file);
                } else {
                    System.out.println("fileが存在しません");
                }
            } catch (Exception e) {
                System.out.println("失敗");
            }
        });
        
        root.getChildren().addAll(bp, threeSetButtons, timeDetailsArea, scrollPane, backButtom);
        Scene scene = new Scene(root, 1280, 750);
        stage.setScene(scene);
    }

// 役職追加のダイアログ
// こっからダイアログのメソッド
    private void showRoleDialog(int scheduleId) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("役職追加");
        dialog.setHeaderText("新しい役職名を入力 ※6文字以内\n（例：店長、部長）");
        dialog.showAndWait().ifPresent(name -> {
            if (name.length() >= 6){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("選択エラー");
                alert.setHeaderText("名前が長すぎます");
                alert.setContentText("6文字以内で入力してください");
                alert.showAndWait();
                return;
            }
            if (!name.isEmpty()) {
                dao.addRole(name, scheduleId);
            }
        });
    }

// 勤務形態追加ダイアログ

    private void showCategoryDialog(int scheduleId) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("勤務形態の追加");
        
        Label labelWorkName = new Label("勤務形態名:");

        TextField name = new TextField();
        name.setPromptText("例：早番、深夜 (3文字以内)"); 
        
        Label carefulLabel = new Label("※名前は【3文字以内】で入力してください");
        carefulLabel.setTextFill(Color.RED);

        Label startMessagLabel = new Label("開始時間:");
        TextField start = new TextField("09:00");
        
        Label finishMessageLabel = new Label("終了時間:");
        TextField finish = new TextField("18:00");

        VBox setPaneScene = new VBox(10);
        setPaneScene.getChildren().addAll(labelWorkName, name, carefulLabel, startMessagLabel, start, finishMessageLabel, finish);
        
        dialog.getDialogPane().setContent(setPaneScene);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(re -> {
            if (re == ButtonType.OK) {
                
                String inputName = name.getText();
                String startTime = start.getText();
                String finishTime = finish.getText();
                // ^←こっから始まる
                // $←これで終わり
                String timeRegex = "^([01][0-9]|2[0-3]):[0-5][0-9]$";
                
                if (inputName.length() == 0){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("入力エラー");
                    alert.setHeaderText("名前が入力されていません");
                    alert.setContentText("名前は一文字以上で入力してください");
                    alert.showAndWait();

                    return;
                }
                if (!startTime.matches(timeRegex) || !finishTime.matches(timeRegex)) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("入力エラー");
                    alert.setHeaderText("時刻の入力が正しくありません");
                    alert.setContentText("時刻は「09:00」のように、半角数字とコロンで入力してください。");
                    alert.showAndWait();

                    return; 
                }
                // 4文字以上入力されたら、3文字にする
                if (inputName.length() > 3) {
                    inputName = inputName.substring(0, 3);
                }

                dao.addCategory(scheduleId, inputName, startTime, finishTime); 
            }
        });
    }

// スタッフ追加
    private void showStuffDialog(int scheduleId) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("スタッフ追加");
        
        TextField name = new TextField();
        ComboBox<String> roleCmb = new ComboBox<>();
        
        Map<Integer, String> roles = dao.getRoles(scheduleId);
        roleCmb.getItems().addAll(new ArrayList<>(roles.values()));
        
        ComboBox<Integer> req = new ComboBox<>();
        req.getItems().addAll(0,1,2,3,4,5,6,7);

        ToggleGroup group = new ToggleGroup();
        RadioButton rb1 = new RadioButton("正社員");
        RadioButton rb2 = new RadioButton("パート");

        rb1.setToggleGroup(group);
        rb2.setToggleGroup(group);
        rb1.setSelected(true);

        VBox vbRadioContents = new VBox();
        vbRadioContents.setSpacing(10);
        vbRadioContents.getChildren().addAll(rb1, rb2);
        
        VBox content = new VBox(10);
        content.getChildren().addAll(
            new Label("スタッフ名"), name, 
            new Label("役職を選択"), roleCmb, 
            new Label("週の希望勤務日数(0~7)"), req,
            new Label("雇用形態を選択"), vbRadioContents
        );
        
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(re -> {
            if (re == ButtonType.OK && roleCmb.getValue() != null) {
                // Mapの逆引き、名前から該当IDを探している
                int roleId = roles.entrySet().stream()
                    .filter(e -> e.getValue().equals(roleCmb.getValue()))
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElse(-1);
                try {
                    int reqDays = req.getValue();
                    int stuffTypeId = 0;
                    if (rb1.isSelected()) {
                        stuffTypeId = 1;
                    } else if (rb2.isSelected()) {
                        stuffTypeId = 2;
                    }
                    dao.addStuff(name.getText(), scheduleId, roleId, reqDays, stuffTypeId);
                } catch (NumberFormatException e) {
                    System.out.println("希望日数は数字で入力してください");
                }
            }
        });
    }
}
