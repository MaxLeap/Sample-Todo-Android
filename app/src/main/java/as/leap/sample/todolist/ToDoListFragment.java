package as.leap.sample.todolist;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

import as.leap.DeleteCallback;
import as.leap.FindCallback;
import as.leap.LCDataManager;
import as.leap.LCObject;
import as.leap.LCQuery;
import as.leap.LCQueryManager;
import as.leap.SaveCallback;
import as.leap.exception.LCException;

public class ToDoListFragment extends ListFragment {

    private List<LCObject> todos;
    private ListViewClickCallback listViewClickCallback;

    public static final int INSERT_ID = Menu.FIRST;
    private static final int DELETE_ID = Menu.FIRST + 1;

    interface ListViewClickCallback {
        void onItemClick(String name, int position);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() == null || getActivity().isFinishing()) {
            return;
        }
        registerForContextMenu(getListView());
        setListShown(false);
        findResult();
    }

    public void findResult() {
        LCQuery<LCObject> query = new LCQuery<LCObject>("Todo");
        query.orderByDescending("createdAt");
        LCQueryManager.findAllInBackground(query,
                new FindCallback<LCObject>() {

                    @Override
                    public void done(List<LCObject> results, LCException e) {
                        if (getActivity() == null || getActivity().isFinishing()) {
                            return;
                        }
                        setListShown(true);

                        if (e == null) {
                            todos = results;
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                    getActivity(), R.layout.todo_row);
                            if (todos != null) {
                                for (LCObject todo : todos) {
                                    adapter.add(todo.getString(CreateTodoActivity.EXTRA_NAME));
                                }
                            }
                            setListAdapter(adapter);

                        } else {
                            if (e.getCode() == LCException.OBJECT_NOT_FOUND) {
                                setListAdapter(null);
                            }
                            setEmptyText(getString(R.string.empty));
                            e.printStackTrace();
                        }
                    }
                });
    }

    public void update(String name, int position) {
        LCObject todo = todos.get(position);
        todo.put("name", name);
        setListShown(false);
        LCDataManager.saveInBackground(todo, new SaveCallback() {

            @Override
            public void done(LCException exception) {
                if (exception != null) {
                    setListShown(true);
                } else {
                    findResult();
                }
            }
        });
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        listViewClickCallback.onItemClick(todos.get(position).getString(CreateTodoActivity.EXTRA_NAME), position);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, DELETE_ID, 0, R.string.menu_delete);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case DELETE_ID:
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
                        .getMenuInfo();

                // Delete the remote object
                final LCObject todo = todos.get(info.position);

                setListShown(false);
                LCDataManager.deleteInBackground(todo, new DeleteCallback() {

                    @Override
                    public void done(LCException exception) {
                        if (exception != null) {
                            setListShown(true);
                        } else {
                            findResult();
                        }
                    }
                });
                return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        listViewClickCallback = (ListViewClickCallback) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listViewClickCallback = null;
    }
}
