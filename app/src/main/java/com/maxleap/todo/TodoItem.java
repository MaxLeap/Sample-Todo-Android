package com.maxleap.todo;

import com.maxleap.MLClassName;
import com.maxleap.MLObject;

@MLClassName(value = "Items")
public class TodoItem extends MLObject {

    public static final String NAME = "Name";
    public static final String STATUS = "Status";


    public void setName(String name) {
        put(NAME, name);
    }

    public String getName() {
        return getString(NAME);
    }

    public boolean isDone() {
        return getBoolean(STATUS);
    }

    public void setDone(boolean done) {
        put(STATUS, done);
    }

}
