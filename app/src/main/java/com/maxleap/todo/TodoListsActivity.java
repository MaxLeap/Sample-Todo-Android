package com.maxleap.todo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import com.maxleap.*;
import com.maxleap.exception.MLException;

import java.util.List;

public class TodoListsActivity extends BaseActivity {

    public static final String TAG = TodoListsActivity.class.getName();

    private ListView mListView;
    private ProgressBar mProgressBar;
    private List<TodoList> mTodoLists;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_list);
        setTitle(getString(R.string.title_list));

        mListView = (ListView) findViewById(R.id.list);
        mProgressBar = (ProgressBar) findViewById(R.id.progress);
        setListShown(false);
        findResult();

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view,
                                    final int position, final long id) {
                Intent intent = new Intent(mContext, TodoItemsActivity.class);
                intent.putExtra(TodoItemsActivity.EXTRA_TODOLIST_ID, mTodoLists.get(position).getObjectId());
                startActivity(intent);
            }
        });
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, final View view,
                                           final int position, final long id) {
                new AlertDialog.Builder(mContext)
                        .setMessage(R.string.delete_todolist)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, final int which) {
                                TodoList todoList = mTodoLists.get(position);
                                setListShown(false);
                                MLDataManager.deleteInBackground(todoList, new DeleteCallback() {
                                    @Override
                                    public void done(final MLException e) {
                                        if (isFinishing()) {
                                            return;
                                        }

                                        if (null != e) {
                                            setListShown(true);

                                            if (e.getCode() != MLException.OBJECT_NOT_FOUND) {
                                                MLLog.e(TAG, e);
                                                Utils.toast(mContext, e.getMessage());
                                            }
                                            return;
                                        }
                                        findResult();
                                    }
                                });
                            }
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .create()
                        .show();


                return true;
            }
        });
    }

    private void setListShown(boolean shown) {
        if (shown) {
            mListView.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
        } else {
            mListView.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    private void setListAdapter(ListAdapter adapter) {
        mListView.setAdapter(adapter);
        setListShown(true);
    }

    private void findResult() {
        MLQuery<TodoList> query = TodoList.getQuery();
        query.orderByDescending(MLObject.KEY_UPDATED_AT);
        MLQueryManager.findAllInBackground(query, new FindCallback<TodoList>() {
            @Override
            public void done(final List<TodoList> list, final MLException e) {
                if (isFinishing()) {
                    return;
                }
                setListShown(true);

                if (null != e) {
                    if (e.getCode() == MLException.OBJECT_NOT_FOUND) {
                        Utils.toast(mContext, getString(R.string.empty_list));
                        setListAdapter(null);
                    } else {
                        MLLog.e(TAG, e);
                        Utils.toast(mContext, e.getMessage());
                    }
                    return;
                }
                mTodoLists = list;
                Apt apt = new Apt(mContext);
                for (TodoList todoList : list) {
                    apt.add(todoList);
                }
                setListAdapter(apt);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                final EditText editText = new EditText(mContext);
                LinearLayout linearLayout = new LinearLayout(mContext);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                params.leftMargin = getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin);
                params.rightMargin = getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin);
                linearLayout.addView(editText, params);
                new AlertDialog.Builder(mContext)
                        .setTitle(R.string.create_todo)
                        .setView(linearLayout)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, final int which) {
                                String name = editText.getText().toString();
                                TodoList todoList = new TodoList();
                                todoList.setName(name);
                                setListShown(false);
                                MLDataManager.saveInBackground(todoList, new SaveCallback() {
                                    @Override
                                    public void done(final MLException e) {
                                        if (isFinishing()) {
                                            return;
                                        }

                                        if (null != e) {
                                            setListShown(true);
                                            if (e.getCode() != MLException.OBJECT_NOT_FOUND) {
                                                MLLog.e(TAG, e);
                                                Utils.toast(mContext, e.getMessage());
                                            }
                                            return;
                                        }
                                        findResult();
                                    }
                                });
                            }
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .create().show();

                return true;
            case R.id.action_refresh:
                setListShown(false);
                findResult();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class Apt extends ArrayAdapter<TodoList> {

        public Apt(Context context) {
            super(context, 0);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.todolist_row, null);
            TextView textView = (TextView) view.findViewById(R.id.item);
            textView.setText(getItem(position).getName());
            return view;
        }

        @Override
        public TodoList getItem(final int position) {
            return mTodoLists.get(position);
        }

        @Override
        public int getCount() {
            return mTodoLists.size();
        }
    }

}
