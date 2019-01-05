package com.vijendert.todo.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTimePicker;
import com.vijendert.todo.models.ToDoItem;
import com.vijendert.todo.services.ToDoExcelService;
import com.vijendert.todo.utils.Utils;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class ViewController implements Initializable {

    @FXML
    TableView<ToDoItem> todo_table;

    @FXML
    JFXTextArea description_txt;

    @FXML
    JFXTextArea notes_txt;

    @FXML
    JFXCheckBox achievement_chk;

    @FXML
    JFXDatePicker start_date_picker;

    @FXML
    JFXTimePicker start_time_picker;

    @FXML
    JFXCheckBox reminder_chk;

    @FXML
    JFXButton schedule_task_btn;

    @FXML
    JFXButton back_btn;

    @FXML
    JFXCheckBox achievements_chk;

    @FXML
    JFXButton mark_complete_btn;

    @FXML
        JFXButton sync_btn;

    @FXML
    ComboBox<String> filter_combo;

    @FXML
    JFXDatePicker filter_dp;

    @FXML
    ComboBox<String> month_combo;

    @FXML
    JFXCheckBox completed_chk;

    @FXML
    JFXCheckBox scheduled_chk;

    @FXML
    JFXCheckBox created_chk;

    @FXML
    JFXButton search_btn;

    @FXML
    JFXButton load_btn;

    private ArrayList<ToDoItem> exceltoDoItems;
    private final static ToDoExcelService toDoExcelService = ToDoExcelService.getInstance();
    private ObservableList<ToDoItem> data;
    private static List<ToDoItem> cacheUpdatedTodoItems = new ArrayList<>();
    private ObservableList<String> filterComboData;
    private ObservableList<String> monthComboData;
    private ObservableList<ToDoItem> todayData;
    private ObservableList<ToDoItem> selectedDayData;
    private ObservableList<ToDoItem> monthData;
    private ObservableList<ToDoItem> currentDataView;
    private ObservableList<ToDoItem> filteredView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        showSearchFilters(true);
        disableAllComponents(true);
        exceltoDoItems = toDoExcelService.readAllDataOfYearExcel();
        setTableViewColumns();
        filterComboData = FXCollections.observableArrayList();
        filterComboData.addAll("All", "Today", "Select Day", "Select Month");
        filter_combo.setItems(filterComboData);
        monthComboData = FXCollections.observableArrayList();
        monthComboData.addAll("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December");
        month_combo.setItems(monthComboData);
        filter_combo.getSelectionModel().selectFirst();

        data = FXCollections.observableArrayList(
                exceltoDoItems
        );
        currentDataView = data;
        todo_table.setItems(data);
        todo_table.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("old " + oldValue + " new " + newValue);
            if (newValue != null) {
                if (newValue.getState().equals(ToDoItem.STATE.COMPLETE)) {
                    disableAllComponents(true);
                }
                if (newValue.getState().equals(ToDoItem.STATE.PENDING)) {
                    disableCreatedComponents(false);
                    disableScheduleComponents(true);
                }
                if (newValue.getState().equals(ToDoItem.STATE.CREATED)) {
                    disableAllComponents(false);
                }
                description_txt.setText(newValue.getDescription());
                notes_txt.setText(newValue.getNotes());
                achievement_chk.setSelected(newValue.isAchievement());
                reminder_chk.setSelected(newValue.isHasReminder());
                if (newValue.isScheduled()) {
                    Date dt = newValue.getStartTime();
                    LocalDate date = dt.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    LocalTime time = dt.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
                    start_date_picker.setValue(date);
                    start_time_picker.setValue(time);
                } else if (newValue.getState().equals(ToDoItem.STATE.CREATED)) {
                    start_date_picker.setValue(LocalDate.now());
                    start_time_picker.setValue(LocalTime.now());
                } else {
                    start_date_picker.setValue(null);
                    start_time_picker.setValue(null);
                }
            }
        });
        filter_combo.getSelectionModel().selectedIndexProperty().addListener((observable,
                oldValue,
                newValue) -> {
            filter_dp.setVisible(false);
            month_combo.setVisible(false);
            todo_table.setItems(data);
            todo_table.refresh();
            todo_table.requestFocus();
            todo_table.getSelectionModel().select(0);
            todo_table.getFocusModel().focus(0);
            if (newValue.equals(0)) {
                data = FXCollections.observableArrayList(
                        exceltoDoItems
                );
                currentDataView = data;

                showSearchFilters(true);
            } else if (newValue.equals(1)) {
                todayData = FXCollections.observableArrayList();
                for (ToDoItem toDoItem : exceltoDoItems) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                        Date todayDate = sdf.parse(sdf.format(new Date()));
                        if (sdf.format(todayDate).equals(sdf.format(toDoItem.getDate()))) {
                            todayData.add(toDoItem);

                        }
                    } catch (ParseException ex) {
                        Logger.getLogger(ViewController.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
                currentDataView = todayData;
                todo_table.setItems(todayData);
                todo_table.refresh();
                todo_table.requestFocus();
                todo_table.getSelectionModel().select(0);
                todo_table.getFocusModel().focus(0);
                showSearchFilters(true);
            } else if (newValue.equals(2)) {
                showSearchFilters(false);
                filter_dp.setVisible(true);
                Date dt = new Date();
                LocalDate ldate = dt.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                filter_dp.valueProperty().addListener((ov, loldValue, lnewValue) -> {
                    System.out.println("date old val " + loldValue + " new val " + lnewValue);
                    if (lnewValue != null) {
                        selectedDayData = FXCollections.observableArrayList();
                        for (ToDoItem toDoItem : exceltoDoItems) {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                            LocalDate selectedDate = filter_dp.getValue();
                            Date date = Date.from(selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                            if (sdf.format(date).equals(sdf.format(toDoItem.getDate()))) {
                                selectedDayData.add(toDoItem);
                            }
                        }
                        showSearchFilters(true);
                        currentDataView = selectedDayData;
                        todo_table.setItems(selectedDayData);
                        todo_table.refresh();
                        todo_table.requestFocus();
                        todo_table.getSelectionModel().select(0);
                        todo_table.getFocusModel().focus(0);
                    }

                });

            } else {
                showSearchFilters(false);
                month_combo.setVisible(true);
                month_combo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        ArrayList<ToDoItem> monthItems = toDoExcelService.getMonthMap().get(newVal);
                        if (monthItems != null) {
                            monthData = FXCollections.observableArrayList(monthItems);
                            currentDataView = monthData;
                            todo_table.setItems(monthData);
                            todo_table.refresh();
                            todo_table.getSelectionModel().select(0);
                            todo_table.getFocusModel().focus(0);
                            todo_table.requestFocus();
                            showSearchFilters(true);
                        }

                    }
                });

            }
        });

        /*
        If completed chk is selected -- from the current view add completed
        If schedules is chk put completed and checked
         */
        search_btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                int index = todo_table.getSelectionModel().getFocusedIndex();
                filteredView = FXCollections.observableArrayList();
                if (completed_chk.isSelected()) {
                    for (ToDoItem toDoItem : currentDataView) {
                        if (toDoItem.getState().equals(ToDoItem.STATE.COMPLETE)) {
                            filteredView.add(toDoItem);
                        }
                    }
                }
                if (scheduled_chk.isSelected()) {
                    for (ToDoItem toDoItem : currentDataView) {
                        if (toDoItem.getState().equals(ToDoItem.STATE.PENDING)) {
                            filteredView.add(toDoItem);
                        }
                    }
                }
                if (created_chk.isSelected()) {
                    for (ToDoItem toDoItem : currentDataView) {
                        if (toDoItem.getState().equals(ToDoItem.STATE.CREATED)) {
                            filteredView.add(toDoItem);
                        }
                    }
                }
                if (!completed_chk.isSelected() && !scheduled_chk.isSelected() && !created_chk.isSelected()) {
                    filteredView = currentDataView;
                }
                ObservableList<ToDoItem> achievmentItems = null;
                if (achievements_chk.isSelected()) {
                    achievmentItems = FXCollections.observableArrayList();
                    for (ToDoItem toDoItem : filteredView) {
                        if (toDoItem.isAchievement()) {
                            System.out.println(".handle() isAchievement " + toDoItem.isAchievement());
                            achievmentItems.add(toDoItem);
                        }
                    }
                    filteredView.clear();
                    filteredView.addAll(achievmentItems);
                }

                todo_table.setItems(filteredView);
                todo_table.refresh();
                todo_table.requestFocus();
                todo_table.getSelectionModel().select(index);
                todo_table.getFocusModel().focus(index);

            }
        });
        /*completed_chk.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if(filteredView == null){
                filteredView = FXCollections.observableArrayList();
            }
          
            if (newVal) {
                filteredView = FXCollections.observableArrayList();
                for (ToDoItem toDoItem : currentDataView) {
                    if (!toDoItem.getState().equals(ToDoItem.STATE.COMPLETE)) {
                        filteredView.add(toDoItem);
                    }
                }
                currentDataView.removeAll(filteredView);
                todo_table.setItems(currentDataView);
                todo_table.refresh();
                todo_table.requestFocus();
            } else {
                if(filteredView!=null){
                    currentDataView.addAll(filteredView);
                    filteredView = null;
                }
                    todo_table.setItems(currentDataView);
                    todo_table.refresh();
                    todo_table.requestFocus();
            }
        });

        scheduled_chk.selectedProperty().addListener((obs, oldVal, newVal) -> {
           if(filteredView == null){
                filteredView = FXCollections.observableArrayList();
            }
            if (newVal) {
                filteredView = FXCollections.observableArrayList();
                for (ToDoItem toDoItem : currentDataView) {
                    if (!toDoItem.getState().equals(ToDoItem.STATE.PENDING)) {
                        filteredView.add(toDoItem);
                    }
                }
                currentDataView.removeAll(filteredView);
                todo_table.setItems(currentDataView);
                todo_table.refresh();
                todo_table.requestFocus();
            } else {
                if(filteredView!=null){
                    currentDataView.addAll(filteredView);
                    filteredView = null;
                }
                    todo_table.setItems(currentDataView);
                    todo_table.refresh();
                    todo_table.requestFocus();
            }
        });*/

        //---------------------SCHEDULE BUTTONS---------------------------
        /**
         * On click action we need to edit data on taskItems
         */
        schedule_task_btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    TableView.TableViewSelectionModel<ToDoItem> todo_tableSelectionModel = todo_table.getSelectionModel();

                    int index = todo_tableSelectionModel.getFocusedIndex();
                    if (index != -1) {
                        //ToDoItem toDoItem = exceltoDoItems.get(index);
                        ToDoItem toDoItem = todo_tableSelectionModel.getSelectedItem();
                        if (!toDoItem.getState().equals(ToDoItem.STATE.COMPLETE)) {
                            String st_date = start_date_picker.getValue().toString();
                            String st_time = start_time_picker.getValue().toString();
                            Date dt = Utils.parseDate(st_date + " " + st_time, "yyyy-MM-dd hh:mm");
                            toDoItem.setStartTime(dt);
                            toDoItem.setState(ToDoItem.STATE.PENDING);
                            toDoItem.setScheduled(true);
                            toDoItem.setHasReminder(reminder_chk.isSelected());
                            toDoItem.setNotes(notes_txt.getText());
                            toDoItem.setDescription(description_txt.getText());
                            toDoItem.setAchievement(achievement_chk.isSelected());
                            /* exceltoDoItems.set(index, toDoItem);

                            data = FXCollections.observableArrayList(
                                    exceltoDoItems
                            );*/
                            //todo_table.setItems(data);
                            todo_table.refresh();
                            todo_table.requestFocus();
                            todo_table.getSelectionModel().select(index);
                            todo_table.getFocusModel().focus(index);
                            cacheUpdatedTodoItems.add(toDoItem);
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        });

        mark_complete_btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    TableView.TableViewSelectionModel<ToDoItem> todo_tableSelectionModel = todo_table.getSelectionModel();

                    int index = todo_tableSelectionModel.getFocusedIndex();
                    if (index != -1) {
                        ToDoItem toDoItem = todo_tableSelectionModel.getSelectedItem();
                        Date dt = new Date();
                        toDoItem.setEndTime(dt);
                        if (toDoItem.isScheduled() && toDoItem.getState().equals(ToDoItem.STATE.PENDING)) {
                            Date start = toDoItem.getStartTime();
                            Date end = toDoItem.getEndTime();
                            toDoItem.setDuration(Utils.printDifference(start, end));
                        }
                        toDoItem.setNotes(notes_txt.getText());
                        toDoItem.setDescription(description_txt.getText());
                        toDoItem.setAchievement(achievement_chk.isSelected());
                        toDoItem.setState(ToDoItem.STATE.COMPLETE);
                        /*exceltoDoItems.set(index, toDoItem);
                        data = FXCollections.observableArrayList(
                                exceltoDoItems
                        );
                        todo_table.setItems(data);*/
                        todo_table.refresh();
                        todo_table.requestFocus();
                        todo_table.getSelectionModel().select(index);
                        todo_table.getFocusModel().focus(index);
                        cacheUpdatedTodoItems.add(toDoItem);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        //----------------------MAIN BUTTONS-------------------------
        sync_btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Adding to excel");
                toDoExcelService.updateToDoItemListExcel(cacheUpdatedTodoItems);
            }
        });

        back_btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                MainLoaderController mainLoaderController = (MainLoaderController) Utils.getController("MainLoaderController");
                mainLoaderController.getLoader_anchor_pane().getChildren().get(2).setVisible(false);
                mainLoaderController.getLoader_anchor_pane().getChildren().get(1).setVisible(true);
                AddTaskController addTaskController = (AddTaskController) Utils.getController("AddTaskController");
                addTaskController.syncAction();
            }
        });

        load_btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                reset();
            }

        });
        //
        System.out.println("com.vijendert.todo.controllers.ViewController.initialize()");
        toDoExcelService.monitorTodoItems();
        toDoExcelService.monitorMonthMapArray();
        //------------------------FILTER EVENTS HANDLING-----------------------------------
    }

    private void setTableViewColumns() {

        TableColumn dateFieldCol = new TableColumn("Date");
        dateFieldCol.setMinWidth(150);
        dateFieldCol.setCellValueFactory(
                new PropertyValueFactory<>("date"));

        TableColumn descriptionFieldCol = new TableColumn("Description");
        descriptionFieldCol.setMinWidth(200);
        descriptionFieldCol.setCellValueFactory(
                new PropertyValueFactory<>("description"));

        TableColumn notesFieldCol = new TableColumn("Notes");
        notesFieldCol.setMinWidth(200);
        notesFieldCol.setCellValueFactory(
                new PropertyValueFactory<>("notes"));

        TableColumn achievementFieldCol = new TableColumn("Achievement");
        achievementFieldCol.setMinWidth(75);
        achievementFieldCol.setCellValueFactory(
                new PropertyValueFactory<>("achievement"));

        TableColumn startTimeFieldCol = new TableColumn("StartTime");
        startTimeFieldCol.setMinWidth(150);
        startTimeFieldCol.setCellValueFactory(
                new PropertyValueFactory<>("startTime"));

        TableColumn endTimeFieldCol = new TableColumn("EndTime");
        endTimeFieldCol.setMinWidth(150);
        endTimeFieldCol.setCellValueFactory(
                new PropertyValueFactory<>("endTime"));

        TableColumn durationFieldCol = new TableColumn("Duration");
        durationFieldCol.setMinWidth(100);
        durationFieldCol.setCellValueFactory(
                new PropertyValueFactory<>("duration"));

        TableColumn hasReminderFieldCol = new TableColumn("Reminder");
        hasReminderFieldCol.setMinWidth(100);
        hasReminderFieldCol.setCellValueFactory(
                new PropertyValueFactory<>("hasReminder"));

        TableColumn isScheduledFieldCol = new TableColumn("Scheduled");
        isScheduledFieldCol.setMinWidth(100);
        isScheduledFieldCol.setCellValueFactory(
                new PropertyValueFactory<>("scheduled"));

        TableColumn stateFieldCol = new TableColumn("State");
        stateFieldCol.setMinWidth(100);
        stateFieldCol.setCellValueFactory(
                new PropertyValueFactory<>("state"));

        todo_table.getColumns().addAll(dateFieldCol, descriptionFieldCol, notesFieldCol, achievementFieldCol,
                startTimeFieldCol, endTimeFieldCol, durationFieldCol, hasReminderFieldCol, isScheduledFieldCol, stateFieldCol
        );
    }

    private void disableAllComponents(boolean val) {
        description_txt.setDisable(val);
        notes_txt.setDisable(val);
        start_date_picker.setDisable(val);
        start_time_picker.setDisable(val);
        reminder_chk.setDisable(val);
        achievement_chk.setDisable(val);
        schedule_task_btn.setDisable(val);
        mark_complete_btn.setDisable(val);
    }

    private void disableCreatedComponents(boolean val) {
        description_txt.setDisable(val);
        notes_txt.setDisable(val);
        achievement_chk.setDisable(val);
        schedule_task_btn.setDisable(val);
        mark_complete_btn.setDisable(val);
    }

    private void disableScheduleComponents(boolean val) {
        start_date_picker.setDisable(val);
        start_time_picker.setDisable(val);
        reminder_chk.setDisable(val);
        schedule_task_btn.setDisable(true);
        mark_complete_btn.setDisable(false);
    }

    private void showSearchFilters(boolean val) {
        completed_chk.setVisible(val);
        scheduled_chk.setVisible(val);
        created_chk.setVisible(val);
        achievements_chk.setVisible(val);
        search_btn.setVisible(val);

    }

    public void reset() {
        exceltoDoItems = toDoExcelService.readAllDataOfYearExcel();
        data = FXCollections.observableArrayList(
                exceltoDoItems
        );
        System.out.println("com.vijendert.todo.controllers.ViewController.reset() lenght"+data.size());
        currentDataView = data;
        todo_table.setItems(data);
        completed_chk.setSelected(false);
        scheduled_chk.setSelected(false);
        achievements_chk.setSelected(false);
        created_chk.setSelected(false);
        showSearchFilters(true);
        disableAllComponents(true);
        filter_combo.getSelectionModel().selectFirst();
        System.out.println("com.vijendert.todo.controllers.ViewController.reset()");
        toDoExcelService.monitorTodoItems();
        toDoExcelService.monitorMonthMapArray();
    }

}
