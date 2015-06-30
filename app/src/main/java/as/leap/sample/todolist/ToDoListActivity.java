package as.leap.sample.todolist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;

import as.leap.LASDataManager;
import as.leap.LASObject;
import as.leap.callback.SaveCallback;
import as.leap.exception.LASException;

public class ToDoListActivity extends SingleFragmentActivity implements ToDoListFragment.ListViewClickCallback {

    private static final int ACTIVITY_CREATE = 0;
    private static final int ACTIVITY_EDIT = 1;

    public static final int INSERT_ID = Menu.FIRST;
    private static final int DELETE_ID = Menu.FIRST + 1;

    private LASObject todo;
    private ToDoListFragment fragment;

    @Override
    protected Fragment createFragment() {
        fragment = new ToDoListFragment();
        return fragment;
    }

    private void createTodo() {
        Intent i = new Intent(this, CreateTodoActivity.class);
        startActivityForResult(i, ACTIVITY_CREATE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (intent == null) {
            return;
        }
        final Bundle extras = intent.getExtras();

        switch (requestCode) {
            case ACTIVITY_CREATE:

                String name = extras.getString(CreateTodoActivity.EXTRA_NAME);
                todo = new LASObject("Todo");
                todo.put(CreateTodoActivity.EXTRA_NAME, name);
                fragment.setListShown(false);
                LASDataManager.saveInBackground(todo, new SaveCallback() {

                    @Override
                    public void done(LASException exception) {
                        if (exception != null) {
                            fragment.setListShown(true);
                        } else {
                            fragment.findResult();
                        }
                    }
                });

                break;
            case ACTIVITY_EDIT:
                // Edit the remote object
                fragment.update(extras.getString(CreateTodoActivity.EXTRA_NAME), extras.getInt(CreateTodoActivity.EXTRA_POSITION));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, INSERT_ID, 0, R.string.menu_insert);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case INSERT_ID:
                createTodo();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onItemClick(String name, int position) {
        Intent i = new Intent(this, CreateTodoActivity.class);
        i.putExtra(CreateTodoActivity.EXTRA_NAME, name);
        i.putExtra(CreateTodoActivity.EXTRA_POSITION, position);
        startActivityForResult(i, ACTIVITY_EDIT);
    }


}