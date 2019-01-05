package com.vijendert.todo.services;

import com.vijendert.todo.models.ToDoItem;
import com.vijendert.todo.utils.Utils;
import javafx.collections.ObservableList;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.ss.usermodel.Sheet;

public class ToDoExcelService {
    
    private static ToDoExcelService toDoExcelService;
    
    private ToDoExcelService(){
        
    }
    
    public static ToDoExcelService getInstance(){
        if(toDoExcelService == null){
            toDoExcelService = new ToDoExcelService();
        }
        return toDoExcelService;
    }

    /**
     * This will contain methods for manipulate of the Excel
     */
    private static final String EXCEL_PATH = "tasks.xlsx";
    private ObservableList<ToDoItem> data;
    private Map<String, ArrayList<ToDoItem>> monthMap = new HashMap<>();

    public Map<String, ArrayList<ToDoItem>> getMonthMap() {
        return monthMap;
    }

    public ArrayList<ToDoItem> getToDoItems() {
        return toDoItems;
    }

    private ArrayList<ToDoItem> toDoItems ;

    public ArrayList<ToDoItem> readAllDataOfYearExcel() {
          XSSFWorkbook workbook = createWorkBookForRead();
            
            String[] dataForExcelColumnName = {"Date", "Description", "Notes", "Is Achievement", "Start Time", "End Time", "duration", "Is Reminder", "Is Scheduled", "State"};
             Date date = new Date();
            String month = new SimpleDateFormat("MMMM").format(date);
            XSSFSheet sheet = workbook.getSheet(month);
            DataFormatter dataFormatter = new DataFormatter();
            if (sheet == null) {
                sheet = workbook.createSheet(month);
                Row row = sheet.createRow(0);
                for (int i = 0; i < dataForExcelColumnName.length; i++) {
                    Cell cellTemp = row.createCell(i);
                    dataFormatter.formatCellValue(cellTemp);
                    cellTemp.setCellValue(dataForExcelColumnName[i]);
                }
                createExcelSheet(workbook);
            }
        if (toDoItems == null) {
           toDoItems = new ArrayList<>();
            System.out.println("Workbook has " + workbook.getNumberOfSheets() + " Sheets : ");
            for (int j = 0; j < workbook.getNumberOfSheets(); j++) {
                sheet = workbook.getSheetAt(j);
                Iterator<Row> iterator = sheet.iterator();
                iterator.next();
                ArrayList<ToDoItem> tmpToDoItems = new ArrayList<>();
                while (iterator.hasNext()) {
                    ToDoItem item = new ToDoItem();
                    Row row = iterator.next();
                    for (int i = row.getFirstCellNum(); i <= row.getLastCellNum(); i++) {
                        Cell cellTemp = row.getCell(i);
                        String cellValue = dataFormatter.formatCellValue(cellTemp);
                        if (i == 0) {
                            try {
                                item.setDate(Utils.parseDate(cellValue, "E MMM dd HH:mm:ss Z yyyy"));
                            } catch (ParseException ex) {
                                Logger.getLogger(ToDoExcelService.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        if (i == 1) {
                            item.setDescription(cellValue);
                        }
                        if (i == 2) {
                            item.setNotes(cellValue);
                        }
                        if (i == 3) {
                            item.setAchievement(Boolean.parseBoolean(cellValue));
                        }
                        if (i == 4) {
                            try {
                                item.setStartTime(Utils.parseDate(cellValue, "E MMM dd HH:mm:ss Z yyyy"));
                            } catch (ParseException ex) {
                                Logger.getLogger(ToDoExcelService.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        if (i == 5) {
                            try {
                                item.setEndTime(Utils.parseDate(cellValue, "E MMM dd HH:mm:ss Z yyyy"));
                            } catch (ParseException ex) {
                                Logger.getLogger(ToDoExcelService.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        if (i == 6) {
                            item.setDuration(cellValue);
                        }
                        if (i == 7) {
                            item.setHasReminder(Boolean.parseBoolean(cellValue));
                        }
                        if (i == 8) {
                            item.setScheduled(Boolean.parseBoolean(cellValue));
                        }
                        if (i == 9) {
                            item.setState(ToDoItem.STATE.valueOf(cellValue));
                        }
                    }
                    System.out.println("Adding item "+ item);
                    //this is whole data
                    toDoItems.add(item);
                    //this is sheet wise data
                    tmpToDoItems.add(item);
                }
                monthMap.put(sheet.getSheetName(), tmpToDoItems);
            }

        }
        System.out.println("com.vijendert.todo.services.ToDoExcelService.readAllDataOfYearExcel() toDoItems"+toDoItems);
        return toDoItems;
    }

    public void addAllTasks(ArrayList<ToDoItem> toDoItemsToAdd) {
        toDoItems.addAll(toDoItemsToAdd);
    }

    public void writeToDoItemExcel(ArrayList<ToDoItem> toDoItemsToAdd) {

        XSSFWorkbook workbook = createWorkBookForRead();
        XSSFSheet toDoItemsSheet = getWorkSheet(workbook);
        int rowIndex = toDoItemsSheet.getLastRowNum()+1;//an exra row with descriptions this get last row
        for (ToDoItem toDoItem : toDoItemsToAdd) {
            Row row = toDoItemsSheet.createRow(rowIndex++);
            int cellIndex = 0;
            row.createCell(cellIndex++).setCellValue(toDoItem.getDate().toString());
            row.createCell(cellIndex++).setCellValue(toDoItem.getDescription());
            row.createCell(cellIndex++).setCellValue(toDoItem.getNotes());
            row.createCell(cellIndex++).setCellValue(toDoItem.isAchievement());
            if (toDoItem.getStartTime() != null) {
                row.createCell(cellIndex++).setCellValue(toDoItem.getStartTime().toString());
            } else {
                row.createCell(cellIndex++).setCellValue("null");
            }
            if (toDoItem.getEndTime() != null) {
                row.createCell(cellIndex++).setCellValue(toDoItem.getEndTime().toString());
            } else {
                row.createCell(cellIndex++).setCellValue("null");
            }
            if (toDoItem.getDuration() != null) {
                row.createCell(cellIndex++).setCellValue(toDoItem.getDuration());
            } else {
                row.createCell(cellIndex++).setCellValue("null");
            }
            row.createCell(cellIndex++).setCellValue(toDoItem.isHasReminder());
            row.createCell(cellIndex++).setCellValue(toDoItem.isScheduled());
            if (toDoItem.getState() != null) {
                row.createCell(cellIndex++).setCellValue(toDoItem.getState().toString());
            } else {
                row.createCell(cellIndex++).setCellValue("null");
            }
        }
        //write this workbook in excel file.
        try {
            FileOutputStream fos = new FileOutputStream(EXCEL_PATH);
            workbook.write(fos);
            fos.close();
            toDoItems.addAll(toDoItemsToAdd);
            System.out.println("com.vijendert.todo.services.ToDoExcelService.writeToDoItemExcel() lenght"+toDoItems.size());
                    
            //vij update on 4th jan
            updateteDataInMonthMap(toDoItemsToAdd);
            
            //print
            System.out.println("com.vijendert.todo.services.ToDoExcelService.writeToDoItemExcel()");
            monitorTodoItems();
            monitorMonthMapArray();
            
            System.out.println(toDoItems.toString());
            System.out.println(EXCEL_PATH + " is successfully written");

        } catch (FileNotFoundException e) {

            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private XSSFSheet getWorkSheet(XSSFWorkbook workbook) {
        Date date = new Date();
        String month = new SimpleDateFormat("MMMM").format(date);
        XSSFSheet toDoItemsSheet = workbook.getSheet(month);
        if (toDoItemsSheet == null) {
            toDoItemsSheet = workbook.createSheet(month);
        }
        return toDoItemsSheet;
    }

    private XSSFWorkbook createWorkBookForRead() {
        XSSFWorkbook workbook = null;
        try {
            FileInputStream file = new FileInputStream(new File(EXCEL_PATH));
            workbook = new XSSFWorkbook(file);
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return workbook;
    }

    public void deleteToDoItemExcel(int noItemsToDelete) {
        XSSFWorkbook workbook = createWorkBookForRead();
        XSSFSheet toDoItemsSheet = getWorkSheet(workbook);
        int rowIndex = toDoItemsSheet.getLastRowNum() - noItemsToDelete + 1;
        int lastRow = toDoItemsSheet.getLastRowNum() + 1;//c
        for (int i = lastRow; i >= rowIndex; i--) {
            Row row = toDoItemsSheet.getRow(i);
            toDoItemsSheet.removeRow(row);
            ToDoItem deletedToDoItem = toDoItems.remove(i);
            removeFromMap(deletedToDoItem);
        }
        System.out.println("com.vijendert.todo.services.ToDoExcelService.deleteToDoItemExcel()");
        monitorTodoItems();
        monitorMonthMapArray();
        createExcelSheet(workbook);
    }

   

    public void updateToDoItemListExcel(List<ToDoItem> toDoItemsToAdd) {
        System.out.println("com.vijendert.todo.services.ToDoExcelService.updateToDoItemListExcel() BEFORE");
         monitorTodoItems();
         monitorMonthMapArray();
        for (ToDoItem toDoItem : toDoItemsToAdd) {
            String month = new SimpleDateFormat("MMMM").format(toDoItem.getDate());
            int index = findIndexInTheExcel(month, toDoItem);
            XSSFWorkbook workbook = createWorkBookForRead();
            Sheet sheet = workbook.getSheet(month);
            if (index == -1) {
                System.err.println("Please reload data. it appears to be out of sync");
                return;
            }
            Row row = sheet.getRow(index + 1);

            for (int i = row.getFirstCellNum(); i <= row.getLastCellNum(); i++) {
                Cell cellTemp = row.getCell(i);
                if (i == 0) {
                    cellTemp.setCellValue(toDoItem.getDate().toString());
                }
                if (i == 1) {
                    cellTemp.setCellValue(toDoItem.getDescription());
                }
                if (i == 2) {
                    cellTemp.setCellValue(toDoItem.getNotes());
                }
                if (i == 3) {
                    cellTemp.setCellValue(toDoItem.isAchievement());
                }
                if (i == 4) {
                    if (toDoItem.getStartTime() != null) {
                        cellTemp.setCellValue(toDoItem.getStartTime().toString());
                    } else {
                        cellTemp.setCellValue("null");
                    }

                }
                if (i == 5) {
                    if (toDoItem.getEndTime() != null) {
                        cellTemp.setCellValue(toDoItem.getEndTime().toString());
                    } else {
                        cellTemp.setCellValue("null");
                    }
                }
                if (i == 6) {
                    cellTemp.setCellValue(toDoItem.getDuration());
                }
                if (i == 7) {
                    cellTemp.setCellValue(toDoItem.isHasReminder());
                }
                if (i == 8) {
                    cellTemp.setCellValue(toDoItem.isScheduled());
                }
                if (i == 9) {
                    if (toDoItem.getState() != null) {
                        cellTemp.setCellValue(toDoItem.getState().toString());
                    } else {
                        cellTemp.setCellValue("null");
                    }

                }
            }
            createExcelSheet(workbook);
        }
        System.out.println("com.vijendert.todo.services.ToDoExcelService.updateToDoItemListExcel() AFTER");
         monitorTodoItems();
         monitorMonthMapArray();

    }

       private void createExcelSheet(XSSFWorkbook workbook) {
        try {
            FileOutputStream fos = new FileOutputStream(EXCEL_PATH);
            workbook.write(fos);
            fos.close();
            System.out.println(EXCEL_PATH + " is successfully written");
        } catch (FileNotFoundException e) {

            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

   

    private int findIndexInTheExcel(String month, ToDoItem toDoItem) {
        ArrayList<ToDoItem> toDoItems = getMonthMap().get(month);
        for (int i = 0; i < toDoItems.size(); i++) {
            System.out.println(" items uuid "+toDoItems.get(i).getUuid()+"   toDoItem uiid "+toDoItem.getUuid());
            if (toDoItems.get(i).getUuid().equals(toDoItem.getUuid())) {
                System.out.println("The returned index is " + i);
                return i;
            }
        }
        return -1;
    }

    private void updateteDataInMonthMap(ArrayList<ToDoItem> toDoItemsToAdd) {
        for (ToDoItem toDoItem : toDoItemsToAdd) {
            String month = new SimpleDateFormat("MMMM").format(toDoItem.getDate());
            System.out.println("com.vijendert.todo.services.ToDoExcelService.updateteDataInMonthMap  " + month);
            if (getMonthMap().containsKey(month)) {
                ArrayList<ToDoItem> toDoItemsMonth = getMonthMap().get(month);
                toDoItemsMonth.add(toDoItem);
            }

        }
    }

    private void removeFromMap(ToDoItem deletedToDoItem) {
        String month = new SimpleDateFormat("MMMM").format(deletedToDoItem.getDate());
        System.out.println("com.vijendert.todo.services.ToDoExcelService.removeFromMap  " + month);
        if (getMonthMap().containsKey(month)) {
            ArrayList<ToDoItem> toDoItemsMonth = getMonthMap().get(month);
            toDoItemsMonth.remove(deletedToDoItem);
        }
    }

    public void deleteLastSynchedItems(ArrayList<ToDoItem> toDoItemSet) {
        toDoItems.removeAll(toDoItemSet);
        for(ToDoItem toDoItem:toDoItemSet){
            removeFromMap(toDoItem);
        }
    }
    
    public void monitorTodoItems(){
        System.out.println("======================TODOITEM===============================================");
        System.out.println("Monitor todo Items size = " + toDoItems.size() );
        for(ToDoItem toDoItem : toDoItems){
            System.out.println("ToDoItem in todoItems "+ toDoItem.getUuid() + " | "+ toDoItem.getDescription());
        }
        System.out.println("======================TODOITEM END===============================================");
    }
    public void monitorMonthMapArray(){
        System.out.println("======================MONTHMAP===============================================");
        ArrayList<ToDoItem> january = getMonthMap().get("January");
        System.out.println("January arraylist size "+ january.size());
        
        for(ToDoItem toDoItemJan: january ){
            System.out.println("January ToDoItem in todoItems "+ toDoItemJan.getUuid() + " | "+ toDoItemJan.getDescription());
        }
        
        
        ArrayList<ToDoItem> december = getMonthMap().get("December");
        System.out.println("December arraylist size "+ december.size());
        
        for(ToDoItem toDoItemDec: december ){
            System.out.println("December ToDoItem in todoItems "+ toDoItemDec.getUuid() + " | "+ toDoItemDec.getDescription());
        }
         System.out.println("======================MONTHMAP END===============================================");
    }

}
