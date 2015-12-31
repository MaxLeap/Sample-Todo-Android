package com.maxleap.todo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.maxleap.LogInCallback;
import com.maxleap.MLLog;
import com.maxleap.MLUser;
import com.maxleap.MLUserManager;
import com.maxleap.SignUpCallback;
import com.maxleap.exception.MLException;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = LoginActivity.class.getName();

    private EditText mUsernameEditText;
    private EditText mPasswordEditText;
    private TextInputLayout mUsernameTextInputLayout;
    private TextInputLayout mPasswordTextInputLayout;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        MLUser user = MLUser.getCurrentUser();
        if (null != user) {
            startActivity(new Intent(LoginActivity.this, TodoListsActivity.class));
            finish();
        }

        mUsernameEditText = (EditText) findViewById(R.id.username_edit_text);
        mUsernameTextInputLayout = (TextInputLayout) findViewById(R.id.username_input_layout);
        mPasswordEditText = (EditText) findViewById(R.id.password_edit_text);
        mPasswordTextInputLayout = (TextInputLayout) findViewById(R.id.password_input_layout);
        mUsernameTextInputLayout.setErrorEnabled(true);
        mPasswordTextInputLayout.setErrorEnabled(true);

        mUsernameEditText.setText("foobar");
        mPasswordEditText.setText("123456");

        findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (validate()) {
                    MLUserManager.logInInBackground(mUsernameEditText.getText().toString(),
                            mPasswordEditText.getText().toString(),
                            new LogInCallback() {
                                @Override
                                public void done(final MLUser mlUser, final MLException e) {
                                    if (e != null) {
                                        MLLog.e(TAG, e);
                                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    startActivity(new Intent(LoginActivity.this, TodoListsActivity.class));
                                    finish();
                                }
                            });
                }
            }
        });

        findViewById(R.id.signup_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (validate()) {
                    MLUser user = new MLUser();
                    user.setUserName(mUsernameEditText.getText().toString());
                    user.setPassword(mPasswordEditText.getText().toString());
                    MLUserManager.signUpInBackground(user, new SignUpCallback() {
                        @Override
                        public void done(final MLException e) {
                            if (e != null) {
                                MLLog.e(TAG, e);
                                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                return;
                            }
                            startActivity(new Intent(LoginActivity.this, TodoListsActivity.class));
                            finish();
                        }
                    });
                }
            }
        });
    }

    private boolean validate() {
        mUsernameTextInputLayout.setError("");
        mPasswordTextInputLayout.setError("");
        if (TextUtils.isEmpty(mUsernameEditText.getText().toString())) {
            mUsernameTextInputLayout.setError(getString(R.string.err_require_username));
            return false;
        }
        if (TextUtils.isEmpty(mPasswordEditText.getText().toString())) {
            mPasswordTextInputLayout.setError(getString(R.string.err_require_password));
            return false;
        }
        return true;
    }
}
