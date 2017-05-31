package com.jack.todolist.datamodel;//Creating Object class

import java.time.LocalDate;

/**
 * Created by lamkeong on 5/29/2017.
 */
public class TodoItem {

    private String shortDescription;
    private String details;
    private LocalDate deadline;

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public TodoItem(String shortDescription, String details, LocalDate deadline) {

        this.shortDescription = shortDescription;
        this.details = details;
        this.deadline = deadline;
    }

//    public String toString(){ // overriding toString to print shortdescription instead of object reference
//        return this.shortDescription;
//    }
}
