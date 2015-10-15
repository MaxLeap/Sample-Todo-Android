package com.maxleap.todo;

import com.maxleap.MLClassName;
import com.maxleap.MLObject;
import com.maxleap.MLQuery;
import com.maxleap.MLRelation;

@MLClassName(value = "Lists")
public class TodoList extends MLObject {

    public static final String NAME = "Name";
    public static final String ITEMS = "Items";

    public void setName(String name) {
        put(NAME, name);
    }

    public String getName() {
        return getString(NAME);
    }

    public MLRelation<TodoItem> getTodoItems() {
        MLRelation<TodoItem> relation = getRelation(ITEMS);
        relation.setTargetClass("Items");
        return relation;
    }

    public static MLQuery<TodoList> getQuery() {
        return MLQuery.getQuery(TodoList.class);
    }

}
