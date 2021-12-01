package edu.utep.cs.cs4330.wheretodo.model;

import java.io.Serializable;

/**
 * A to-do item.
 */
public class ToDoItem implements Serializable {

    private int id;
    private String description;
    private String notes;
    private String location;
    private int priority;
    private boolean done;

    public ToDoItem(int id, String description, String notes, String location, boolean done, int priority) {
        this.id = id;
        this.description = description;
        this.notes = notes;
        this.done = done;
        this.location = location;
        this.priority = priority;
    }


    public ToDoItem(String description, String location, String notes, int priority) {
        this.description = description;
        this.notes = notes;
        this.location = location;
        this.priority = priority;
    }


    public ToDoItem(String description) {
        this(description, false);
    }

    public ToDoItem(String description, String location) {
        this.description = description;
        this.location = location;
    }

    public ToDoItem(String description, boolean done) {
        this(0, description, done);
    }

    public ToDoItem(int id, String description, boolean done) {
        this.id = id;
        this.description = description;
        this.done = done;
    }

    public ToDoItem(int id, String description, boolean done, String location) {
        this.id = id;
        this.description = description;
        this.done = done;
        this.location = location;
    }

    public int id() {
        return id;
    }

    public String description() {
        return description;
    }

    public boolean isDone() {
        return done;
    }

    public String location() {
        return location;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String notes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int priority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}