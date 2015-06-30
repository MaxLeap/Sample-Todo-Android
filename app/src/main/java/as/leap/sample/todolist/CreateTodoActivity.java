package as.leap.sample.todolist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class CreateTodoActivity extends AppCompatActivity {

    public static final String EXTRA_NAME = "name";
    public static final String EXTRA_POSITION = "position";

    private EditText mNameEditText;
    private int mPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_todo);
        setTitle(R.string.create_todo);

        mNameEditText = (EditText) findViewById(R.id.name_edit_text);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String name = extras.getString(EXTRA_NAME);
            mPosition = extras.getInt(EXTRA_POSITION);

            if (name != null) {
                mNameEditText.setText(name);
            }
        }

        Button confirmButton = (Button) findViewById(R.id.confirm_button);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString(EXTRA_NAME, mNameEditText.getText().toString());
                bundle.putInt(EXTRA_POSITION, mPosition);

                Intent intent = new Intent();
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

}
