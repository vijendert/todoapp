package com.vijendert.todo.utils;

import com.vijendert.todo.controllers.AddTaskController;
import com.vijendert.todo.models.ToDoItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Utils {

    private static HashMap<String,Object> controllerMap = new HashMap<>();

    public static Date parseDate(String date, String format) throws ParseException
    {
        Date result = null;
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(format);
            if(!date.equals("null"))
                result =  formatter.parse(date);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return  result;
    }

    public static void testMethod(){
        AddTaskController controller = (AddTaskController)(getController("AddTaskController"));
        System.out.println(controller);
        TableView<ToDoItem> tv = controller.getTodo_table();
        ArrayList<ToDoItem> toDoItems = controller.getToDoItems();
        ToDoItem toDoItem = new ToDoItem();
        toDoItem.setDate(new Date());
        toDoItem.setDescription("testing");
        toDoItem.setNotes("A testing note");
        toDoItems.add(toDoItem);
        ObservableList<ToDoItem> data =FXCollections.observableArrayList(
                toDoItems
        );
        tv.setItems(data);

    }

    public static void storeController(String controllerName, Object controller) {
        controllerMap.put(controllerName,controller);
    }

    public static  Object getController(String controllerName){
        return controllerMap.get(controllerName);
    }
    
    public static String printDifference(Date startDate, Date endDate){
	
		//milliseconds
		long different = endDate.getTime() - startDate.getTime();
		long secondsInMilli = 1000;
		long minutesInMilli = secondsInMilli * 60;
		long hoursInMilli = minutesInMilli * 60;
		long daysInMilli = hoursInMilli * 24;

		long elapsedDays = different / daysInMilli;
		different = different % daysInMilli;
		
		long elapsedHours = different / hoursInMilli;
		different = different % hoursInMilli;
		
		long elapsedMinutes = different / minutesInMilli;
		different = different % minutesInMilli;
		
		long elapsedSeconds = different / secondsInMilli;
		
		return Long.toString(elapsedDays)+":"+Long.toString(elapsedHours)+":"+Long.toString(elapsedMinutes)+":"+Long.toString(elapsedSeconds);
	}
}
