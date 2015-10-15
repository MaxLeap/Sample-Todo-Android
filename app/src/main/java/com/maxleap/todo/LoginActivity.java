package com.maxleap.todo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.maxleap.*;
import com.maxleap.exception.MLException;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = LoginActivity.class.getName();

    private EditText usernameEditText;
    private EditText passwordEditText;
    private TextInputLayout usernameTextInputLayout;
    private TextInputLayout passwordTextInputLayout;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        MLUser user = MLUser.getCurrentUser();
        if (null != user) {
            startActivity(new Intent(LoginActivity.this, TodoListsActivity.class));
            finish();
        }

        usernameEditText = (EditText) findViewById(R.id.username_edit_text);
        usernameTextInputLayout = (TextInputLayout) findViewById(R.id.username_input_layout);
        passwordEditText = (EditText) findViewById(R.id.password_edit_text);
        passwordTextInputLayout = (TextInputLayout) findViewById(R.id.password_input_layout);
        usernameTextInputLayout.setErrorEnabled(true);
        passwordTextInputLayout.setErrorEnabled(true);

        findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (validate()) {
                    MLUserManager.logInInBackground(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString(),
                            new LogInCallback<MLUser>() {
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
                    user.setUserName(usernameEditText.getText().toString());
                    user.setPassword(passwordEditText.getText().toString());
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
        usernameTextInputLayout.setError("");
        passwordTextInputLayout.setError("");
        if (TextUtils.isEmpty(usernameEditText.getText().toString())) {
            usernameTextInputLayout.setError(getString(R.string.err_require_username));
            return false;
        }
        if (TextUtils.isEmpty(passwordEditText.getText().toString())) {
            passwordTextInputLayout.setError(getString(R.string.err_require_password));
            return false;
        }
        return true;
    }
}
