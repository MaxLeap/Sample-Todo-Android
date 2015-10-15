package com.maxleap.todo;

import com.maxleap.MLClassName;
import com.maxleap.MLObject;
import com.maxleap.MLQuery;

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

    public static MLQuery<TodoItem> getQuery() {
        return MLQuery.getQuery(TodoItem.class);
    }


}
