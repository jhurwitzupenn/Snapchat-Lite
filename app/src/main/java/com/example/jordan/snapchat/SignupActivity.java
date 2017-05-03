package com.example.jordan.snapchat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignupActivity extends AppCompatActivity {

    private SignupActivity signupActivity;

    @BindView(R.id.input_email)
    EditText inputEmail;

    @BindView(R.id.input_password)
    EditText inputPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        signupActivity = this;
        ButterKnife.bind(signupActivity);

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(signupActivity, BaseActivity.class));
            finish();
        }

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            String lastLoggedInUsername = extras.getString(ApplicationConstants.EXTRA_USERNAME_KEY);
            inputEmail.setText(lastLoggedInUsername);
        }
    }

    @OnClick(R.id.login)
    public void login() {
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();
        if ("".equals(email) || "".equals(password)) {
            Toast.makeText(signupActivity, getString(R.string.toast_signin_err_no_params), Toast.LENGTH_SHORT).show();
        } else {
            try {
                ParseUser.logIn(email, password);
                startActivity(new Intent(signupActivity, BaseActivity.class));
                finish();
            } catch (ParseException e) {
                Toast.makeText(signupActivity, getString(R.string.toast_login_err_parse), Toast.LENGTH_SHORT).show();
                Log.e("Snapchat", e.toString());
            }
        }
    }

    @OnClick(R.id.signup)
    public void signup() {
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();
        if ("".equals(email) || "".equals(password)) {
            Toast.makeText(signupActivity, getString(R.string.toast_signin_err_no_params), Toast.LENGTH_SHORT).show();
        } else {
            ParseUser user = new ParseUser();
            user.setUsername(email);
            user.setPassword(password);
            user.setEmail(email);
            user.signUpInBackground(new SignUpCallback() {
                public void done(ParseException e) {
                    if (e == null) {
                        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(signupActivity);
                        sharedPref.edit().putBoolean(ApplicationConstants.SHARED_FIRST_TIME_USER_KEY, true).apply();
                        startActivity(new Intent(signupActivity, BaseActivity.class));
                        finish();
                    } else {
                        Toast.makeText(signupActivity, getString(R.string.toast_signup_err_parse), Toast.LENGTH_SHORT).show();
                        Log.e("Snapchat", e.toString());
                    }
                }
            });
        }
    }
}
