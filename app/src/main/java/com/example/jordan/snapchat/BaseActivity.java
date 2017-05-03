package com.example.jordan.snapchat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;

import com.parse.ParseUser;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class BaseActivity extends AppCompatActivity {

    private BaseActivity baseActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        baseActivity = this;
        ButterKnife.bind(baseActivity);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(baseActivity);
        if(sharedPref.getBoolean(ApplicationConstants.SHARED_FIRST_TIME_USER_KEY, false)) {
            sharedPref.edit().putBoolean(ApplicationConstants.SHARED_FIRST_TIME_USER_KEY, false).apply();
            loadFirstTimeUserWelcome();
        }
    }

    @OnClick(R.id.take_new_photo)
    public void takeNewPhoto(){
        startActivity(new Intent(baseActivity, TakeNewPhotoActivity.class));
    }

    @OnClick(R.id.view_memories)
    public void viewMemories(){
        SharedPreferences sharedPref = baseActivity.getPreferences(Context.MODE_PRIVATE);
        startActivity(new Intent(baseActivity, MemoryActivity.class));
    }

    @OnClick(R.id.view_messages)
    public void viewMessages(){
        startActivity(new Intent(baseActivity, MessageActivity.class));
    }

    @OnClick(R.id.logout)
    public void logout(){
        ParseUser currentUser = ParseUser.getCurrentUser();
        Intent signupActivityIntent = new Intent(baseActivity, SignupActivity.class);
        if (currentUser != null) {
            ParseUser.logOut();
            signupActivityIntent.putExtra(ApplicationConstants.EXTRA_USERNAME_KEY, currentUser.getUsername());
        }
        startActivity(signupActivityIntent);
    }

    private void loadFirstTimeUserWelcome() {
        final AlertDialog.Builder welcome = new AlertDialog.Builder(baseActivity);
        LayoutInflater factory = LayoutInflater.from(baseActivity);
        final View view = factory.inflate(R.layout.first_time_welcome_dialog, null);
        welcome.setView(view);
        final AlertDialog alertDialog = welcome.create();
        alertDialog.show();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                alertDialog.dismiss();
            }
        }, ApplicationConstants.WELCOME_DURATION);
    }
}
