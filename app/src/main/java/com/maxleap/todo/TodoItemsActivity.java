package com.maxleap.todo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import com.maxleap.*;
import com.maxleap.exception.MLException;

import java.util.List;

public class TodoItemsActivity extends BaseActivity {

    public static final String TAG = TodoItemsActivity.class.getName();

    public static final String EXTRA_TODOLIST_ID = "todolistId";

    private String mTodolistId;
    private ListView mListView;
    private ProgressBar mProgressBar;
    private List<TodoItem> mTodoItems;
    private TodoList mTodoList;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_list);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.title_item));

        mTodolistId = getIntent().getStringExtra(EXTRA_TODOLIST_ID);
        mListView = (ListView) findViewById(R.id.list);
        mProgressBar = (ProgressBar) findViewById(R.id.progress);

        mTodoList = new TodoList();
        mTodoList.setObjectId(mTodolistId);

        setListShown(false);
        findResult();

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view,
                                    final int position, final long id) {
                final CheckedTextView textView = (CheckedTextView) view.findViewById(R.id.item);
                final TodoItem todoItem = mTodoItems.get(position);
                final boolean previous = todoItem.isDone();
                todoItem.setDone(!previous);
                textView.setChecked(todoItem.isDone());

                setListShown(false);
                MLDataManager.saveInBackground(todoItem, new SaveCallback() {
                    @Override
                    public void done(final MLException e) {
                        setListShown(true);
                        if (null != e) {
                            textView.setChecked(previous);
                            todoItem.setDone(previous);

                            Utils.toast(mContext, e.getMessage());
                        }
                    }
                });
            }
        });
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, final View view,
                                           final int position, final long id) {
                new AlertDialog.Builder(mContext)
                        .setMessage(R.string.delete_todoitem)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, final int which) {
                                TodoItem todoItem = mTodoItems.get(position);
                                setListShown(false);
                                MLDataManager.deleteInBackground(todoItem, new DeleteCallback() {
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
        MLQuery<TodoItem> query = mTodoList.getTodoItems().getQuery();
        query.orderByDescending(MLObject.KEY_UPDATED_AT);
        MLQueryManager.findAllInBackground(query, new FindCallback<TodoItem>() {
            @Override
            public void done(final List<TodoItem> list, final MLException e) {
                if (isFinishing()) {
                    return;
                }
                setListShown(true);

                if (null != e) {
                    if (e.getCode() == MLException.OBJECT_NOT_FOUND) {
                        Utils.toast(mContext, getString(R.string.empty_item));
                        setListAdapter(null);
                    } else {
                        MLLog.e(TAG, e);
                        Utils.toast(mContext, e.getMessage());
                    }
                    return;
                }
                mTodoItems = list;
                Apt apt = new Apt(mContext);
                for (TodoItem todoItem : list) {
                    apt.add(todoItem);
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
                        .setTitle(R.string.create_todo_item)
                        .setView(linearLayout)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, final int which) {
                                String name = editText.getText().toString();
                                final TodoItem todoItem = new TodoItem();
                                todoItem.setName(name);
                                setListShown(false);


                                MLDataManager.saveInBackground(todoItem, new SaveCallback() {
                                    @Override
                                    public void done(final MLException e) {
                                        if (null != e) {
                                            setListShown(true);
                                            if (e.getCode() != MLException.OBJECT_NOT_FOUND) {
                                                MLLog.e(TAG, e);
                                                Utils.toast(mContext, e.getMessage());
                                            }
                                            return;
                                        }

                                        mTodoList.getTodoItems().add(todoItem);

                                        MLDataManager.saveInBackground(mTodoList, new SaveCallback() {
                                            @Override
                                            public void done(final MLException e) {
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

    class Apt extends ArrayAdapter<TodoItem> {

        public Apt(Context context) {
            super(context, 0);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.todo_row, null);
            CheckedTextView textView = (CheckedTextView) view.findViewById(R.id.item);
            TodoItem todoItem = getItem(position);
            textView.setText(todoItem.getName());
            textView.setChecked(todoItem.isDone());
            return view;
        }

        @Override
        public TodoItem getItem(final int position) {
            return mTodoItems.get(position);
        }

        @Override
        public int getCount() {
            return mTodoItems.size();
        }
    }
}
