package com.vijendert.todo.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTimePicker;
import com.vijendert.todo.models.ToDoItem;
import com.vijendert.todo.services.ToDoExcelService;
import com.vijendert.todo.utils.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;
import javafx.scene.control.DateCell;
import javafx.scene.layout.AnchorPane;

public class AddTaskController implements Initializable {

    @FXML
    JFXButton add_btn;

    @FXML
    JFXButton delete_btn;

    @FXML
    JFXButton edit_btn;

    @FXML
    JFXButton clear_btn;

    @FXML
    JFXTextArea description_txt;

    @FXML
    JFXTextArea notes_txt;

    @FXML
    JFXCheckBox achievement_chk;

    @FXML
    TableView<ToDoItem> todo_table;

    @FXML
    JFXButton schedule_btn;

    @FXML
    JFXButton sync_btn;

    @FXML
    JFXButton add_task_btn;

    @FXML
    AnchorPane schedule_pane;

    @FXML
    AnchorPane addtask_pane;

    @FXML
    JFXDatePicker start_date_picker;

    @FXML
    JFXTimePicker start_time_picker;

    @FXML
    JFXButton schedule_task_btn;

    @FXML
    JFXCheckBox reminder_chk;

    @FXML
    JFXButton mark_complete_btn;

    @FXML
    JFXButton view_btn;

    public TableView<ToDoItem> getTodo_table() {
        return todo_table;
    }

    private ArrayList<ToDoItem> exceltoDoItems;

    private static int numberOfItemssynched;

    private ArrayList<ToDoItem> synchedData;

    public ArrayList<ToDoItem> getToDoItems() {
        return toDoItems;
    }

    private ArrayList<ToDoItem> toDoItems = new ArrayList<>();

    public ObservableList<ToDoItem> getData() {
        return data;
    }

    private ObservableList<ToDoItem> data;
    private final static ToDoExcelService toDoExcelService = ToDoExcelService.getInstance();
    private boolean isSynched = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //toDoExcelService.readExcel();
        //exceltoDoItems = toDoExcelService.getToDoItems();
        /*
        This guys has all the items of the excel never uses it
         */
        exceltoDoItems = toDoExcelService.readAllDataOfYearExcel();
        System.out.println(todo_table.getParent());
        setTableViewColumns();
        schedule_btn.setDisable(true);
        start_date_picker.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();
                setDisable(empty || date.compareTo(today) < 0);
            }
        });
        start_date_picker.setValue(LocalDate.now());
        start_time_picker.setValue(LocalTime.now());
        add_btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ToDoItem toDoItem = new ToDoItem();
                toDoItem.setDate(new Date());
                toDoItem.setDescription(description_txt.getText());
                toDoItem.setNotes(notes_txt.getText());
                System.out.println(achievement_chk.isSelected());
                toDoItem.setAchievement(achievement_chk.isSelected());
                toDoItem.setState(ToDoItem.STATE.CREATED);
                toDoItems.add(toDoItem);
                data = FXCollections.observableArrayList(
                        toDoItems
                );
                todo_table.setItems(data);
                clearAddTask();
            }
        });

        edit_btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                TableViewSelectionModel<ToDoItem> todo_tableSelectionModel = todo_table.getSelectionModel();

                int index = todo_tableSelectionModel.getFocusedIndex();
                if (index != -1) {
                    ToDoItem toDoItem = toDoItems.get(index);
                    toDoItem.setDescription(description_txt.getText());
                    toDoItem.setNotes(notes_txt.getText());
                    toDoItem.setAchievement(achievement_chk.isSelected());
                    toDoItems.set(index, toDoItem);
                    data = FXCollections.observableArrayList(
                            toDoItems
                    );
                    todo_table.setItems(data);
                    todo_table.refresh();
                    todo_table.requestFocus();
                    todo_table.getSelectionModel().select(index);
                    todo_table.getFocusModel().focus(index);

                }

            }
        });

        clear_btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                clearAddTask();
            }
        });

        delete_btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                /**
                 * Need to delete data from the todoItems
                 */
                TableViewSelectionModel<ToDoItem> toDoItem = todo_table.getSelectionModel();
                if (toDoItem != null) {
                    int index = toDoItem.getFocusedIndex();
                    if (index != -1) {
                        toDoItems.remove(index);
                        data = FXCollections.observableArrayList(
                                toDoItems
                        );
                        todo_table.setItems(data);
                    }

                }

            }

        });
        //---------------------SCHEDULE BUTTONS---------------------------
        /**
         * On click action we need to edit data on taskItems
         */
        schedule_task_btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    TableViewSelectionModel<ToDoItem> todo_tableSelectionModel = todo_table.getSelectionModel();

                    int index = todo_tableSelectionModel.getFocusedIndex();
                    if (index != -1) {
                        ToDoItem toDoItem = toDoItems.get(index);
                        if (!toDoItem.getState().equals(ToDoItem.STATE.COMPLETE)) {
                            String st_date = start_date_picker.getValue().toString();
                            String st_time = start_time_picker.getValue().toString();
                            Date dt = Utils.parseDate(st_date + " " + st_time, "yyyy-MM-dd hh:mm");
                            toDoItem.setStartTime(dt);
                            toDoItem.setState(ToDoItem.STATE.PENDING);
                            toDoItem.setScheduled(true);
                            toDoItem.setHasReminder(reminder_chk.isSelected());
                            toDoItems.set(index, toDoItem);

                            data = FXCollections.observableArrayList(
                                    toDoItems
                            );
                            todo_table.setItems(data);
                            todo_table.refresh();
                            todo_table.requestFocus();
                            todo_table.getSelectionModel().select(index);
                            todo_table.getFocusModel().focus(index);
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
                    TableViewSelectionModel<ToDoItem> todo_tableSelectionModel = todo_table.getSelectionModel();

                    int index = todo_tableSelectionModel.getFocusedIndex();
                    if (index != -1) {
                        ToDoItem toDoItem = toDoItems.get(index);
                        Date dt = new Date();
                        toDoItem.setEndTime(dt);
                        if (toDoItem.isScheduled() && toDoItem.getState().equals(ToDoItem.STATE.PENDING)) {
                            Date start = toDoItem.getStartTime();
                            Date end = toDoItem.getEndTime();
                            toDoItem.setDuration(Utils.printDifference(start, end));
                        }
                        toDoItem.setState(ToDoItem.STATE.COMPLETE);
                        toDoItems.set(index, toDoItem);
                        data = FXCollections.observableArrayList(
                                toDoItems
                        );
                        todo_table.setItems(data);
                        todo_table.refresh();
                        todo_table.requestFocus();
                        todo_table.getSelectionModel().select(index);
                        todo_table.getFocusModel().focus(index);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        //---------------------------TABLE_VIEW SELECTION EVENT---------------------------------
        todo_table.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("old " + oldValue + " new " + newValue);
            if (newValue != null) {
                description_txt.setText(newValue.getDescription());
                notes_txt.setText(newValue.getNotes());
                achievement_chk.setSelected(newValue.isAchievement());
                schedule_btn.setDisable(false);
            } else {
                clearAddTask();
                schedule_btn.setDisable(true);
            }
        });
        //------------------------MAIN BUTTONS-------------------------------
        add_task_btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                addtask_pane.setVisible(true);
                description_txt.setDisable(false);
                notes_txt.setDisable(false);
                achievement_chk.setDisable(false);
                schedule_pane.setVisible(false);
                schedule_btn.setDisable(true);
                int index = todo_table.getSelectionModel().getFocusedIndex();
                todo_table.refresh();
                todo_table.getSelectionModel().select(null);
                todo_table.getFocusModel().focus(null);
                todo_table.requestFocus();

            }
        });
        schedule_btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                addtask_pane.setVisible(false);
                description_txt.setDisable(true);
                notes_txt.setDisable(true);
                //achievement_chk.setDisable(true);
                schedule_pane.setVisible(true);

            }
        });
        view_btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                MainLoaderController mainLoaderController = (MainLoaderController) Utils.getController("MainLoaderController");
                mainLoaderController.getLoader_anchor_pane().getChildren().get(2).setVisible(true);
                mainLoaderController.getLoader_anchor_pane().getChildren().get(1).setVisible(false);
                //syncAction();
                ViewController viewController = (ViewController) Utils.getController("ViewController");
                viewController.reset();
            }
        });
        sync_btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Adding to excel");
                syncAction();

            }
        });
    }

    public void syncAction() {
        if (!isSynched) {
            toDoExcelService.writeToDoItemExcel(toDoItems);
            //check if current transaction is synched with excel
            numberOfItemssynched = toDoItems.size();
            isSynched = true;
        } else {
            toDoExcelService.deleteToDoItemExcel(numberOfItemssynched);
            toDoExcelService.writeToDoItemExcel(toDoItems);
            numberOfItemssynched = toDoItems.size();

        }
        /* if(synchedData == null){
            toDoExcelService.writeToDoItemExcel(toDoItems);
            synchedData = new ArrayList<>();
            synchedData.addAll(toDoItems);
        }
        else{
            //delete all last synched items
            toDoExcelService.deleteLastSynchedItems(synchedData);
            toDoExcelService.writeToDoItemExcel(toDoItems);
            //refresh synched data
            synchedData.clear();
            synchedData.addAll(toDoItems);
        }*/
        todo_table.setItems(FXCollections.observableArrayList(toDoItems));
        todo_table.refresh();

    }

    private void clearAddTask() {
        description_txt.setText("");
        notes_txt.setText("");
        achievement_chk.setSelected(false);
    }

    private void setTableViewColumns() {

        TableColumn dateFieldCol = new TableColumn("Date");
        dateFieldCol.setMinWidth(150);
        dateFieldCol.setCellValueFactory(
                new PropertyValueFactory<ToDoItem, String>("date"));

        TableColumn descriptionFieldCol = new TableColumn("Description");
        descriptionFieldCol.setMinWidth(200);
        descriptionFieldCol.setCellValueFactory(
                new PropertyValueFactory<ToDoItem, String>("description"));

        TableColumn notesFieldCol = new TableColumn("Notes");
        notesFieldCol.setMinWidth(200);
        notesFieldCol.setCellValueFactory(
                new PropertyValueFactory<ToDoItem, String>("notes"));

        TableColumn achievementFieldCol = new TableColumn("Achievement");
        achievementFieldCol.setMinWidth(75);
        achievementFieldCol.setCellValueFactory(
                new PropertyValueFactory<ToDoItem, String>("achievement"));

        TableColumn startTimeFieldCol = new TableColumn("StartTime");
        startTimeFieldCol.setMinWidth(150);
        startTimeFieldCol.setCellValueFactory(
                new PropertyValueFactory<ToDoItem, Date>("startTime"));

        TableColumn endTimeFieldCol = new TableColumn("EndTime");
        endTimeFieldCol.setMinWidth(150);
        endTimeFieldCol.setCellValueFactory(
                new PropertyValueFactory<ToDoItem, Date>("endTime"));

        TableColumn durationFieldCol = new TableColumn("Duration");
        durationFieldCol.setMinWidth(100);
        durationFieldCol.setCellValueFactory(
                new PropertyValueFactory<ToDoItem, String>("duration"));

        TableColumn hasReminderFieldCol = new TableColumn("Reminder");
        hasReminderFieldCol.setMinWidth(100);
        hasReminderFieldCol.setCellValueFactory(
                new PropertyValueFactory<ToDoItem, Boolean>("hasReminder"));

        TableColumn isScheduledFieldCol = new TableColumn("Scheduled");
        isScheduledFieldCol.setMinWidth(100);
        isScheduledFieldCol.setCellValueFactory(
                new PropertyValueFactory<ToDoItem, Boolean>("scheduled"));

        TableColumn stateFieldCol = new TableColumn("State");
        stateFieldCol.setMinWidth(100);
        stateFieldCol.setCellValueFactory(
                new PropertyValueFactory<ToDoItem, ToDoItem.STATE>("state"));

        todo_table.getColumns().addAll(dateFieldCol, descriptionFieldCol, notesFieldCol, achievementFieldCol,
                startTimeFieldCol, endTimeFieldCol, durationFieldCol, hasReminderFieldCol, isScheduledFieldCol, stateFieldCol
        );
    }
}
