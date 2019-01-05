package com.vijendert.todo.models;

import java.util.Date;
import java.util.UUID;

public class ToDoItem {

    public enum  STATE{
        CREATED,PENDING,COMPLETE
    }

    private String uuid = UUID.randomUUID().toString();
    private Date date;
    private String description;
    private String notes;
    private boolean isAchievement;
    private Date startTime;
    private  Date endTime;
    private String duration;
    private boolean hasReminder;
    private boolean isScheduled;
    private STATE state;
    
    @Override
    public boolean equals(Object o){
        // If the object is compared with itself then return true   
        if (o == this) { 
            return true; 
        } 
 
        if (!(o instanceof ToDoItem)) { 
            return false; 
        } 
          
        // typecast o to Complex so that we can compare data members  
        ToDoItem c = (ToDoItem) o; 
        return this.getUuid().equals(c.getUuid());
    }
    

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
     public String getUuid() {
        return uuid;
    }

    public STATE getState() {
        return state;
    }

    public void setState(STATE state) {
        this.state = state;
    }

    public boolean isAchievement() {
        return isAchievement;
    }

    public void setAchievement(boolean achievement) {
        isAchievement = achievement;
    }

    public boolean isHasReminder() {
        return hasReminder;
    }

    public void setHasReminder(boolean hasReminder) {
        this.hasReminder = hasReminder;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }


     public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isScheduled() {
        return isScheduled;
    }

    public void setScheduled(boolean scheduled) {
        isScheduled = scheduled;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public ToDoItem(){

    }
}

