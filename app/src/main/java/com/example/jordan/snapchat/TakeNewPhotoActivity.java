package com.example.jordan.snapchat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;

public class TakeNewPhotoActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private TakeNewPhotoActivity takeNewPhotoActivity;
    private boolean isPhotoTaken;
    private boolean isPhotoSaved;
    private String timer;
    private byte[] photoData;

    @BindView(R.id.input_caption)
    EditText inputCaption;

    @BindView(R.id.display_photo)
    ImageView displayPhoto;

    @BindView(R.id.timer_spinner)
    Spinner timerSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_new_photo);
        takeNewPhotoActivity = this;
        isPhotoTaken = false;
        isPhotoSaved = false;
        timer = null;
        photoData = null;
        ButterKnife.bind(takeNewPhotoActivity);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.timer_strings, R.layout.timer_spinner_base);
        adapter.setDropDownViewResource(R.layout.timer_spinner_dropdown);
        timerSpinner.setAdapter(adapter);
    }

    @OnClick(R.id.take_photo)
    public void clickTakePhoto() {
        Intent cameraIntent = new  Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
    }

    @OnClick(R.id.send_photo)
    public void clickSendPhoto() {
        if(isPhotoTaken && timer != null) {
            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> objects, ParseException e) {
                    if (e == null) {
                        if (objects.size() == 1) {
                            Toast.makeText(takeNewPhotoActivity, getString(R.string.toast_take_photo_err_no_users), Toast.LENGTH_SHORT).show();
                        }
                        for (ParseUser parseUser : objects) {
                            String currentUsername = ParseUser.getCurrentUser().getUsername();
                            if (parseUser.getUsername().equals(currentUsername)) {
                                continue;
                            }
                            ParseFile file = new ParseFile(ParseConstants.IMAGE_NAME, photoData);
                            ParseObject message = new ParseObject(ParseConstants.OBJECT_MESSAGE_NAME);

                            message.put(ParseConstants.CAPTION_KEY, inputCaption.getText().toString());
                            message.put(ParseConstants.IMAGE_KEY, file);
                            message.put(ParseConstants.USERNAME_KEY, currentUsername);
                            message.put(ParseConstants.DATE_KEY, Utils.getCurrentDateAsString());
                            message.put(ParseConstants.TIMER_KEY, timer);

                            message.setACL(new ParseACL(parseUser));
                            message.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        Toast.makeText(takeNewPhotoActivity, getString(R.string.toast_send_photo), Toast.LENGTH_SHORT).show();
                                    } else {
                                        Log.e("Snapchat", e.toString());
                                    }
                                }
                            });
                        }
                    } else {
                        Log.e("Snapchat", e.toString());
                    }
                }
            });
        } else {
            Toast.makeText(takeNewPhotoActivity, getString(R.string.toast_take_photo_err_no_photo), Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.save_photo)
    public void clickSavePhoto(){
        if(isPhotoTaken) {
            if (!isPhotoSaved) {
                ParseFile file = new ParseFile(ParseConstants.IMAGE_NAME, photoData);
                ParseObject memory = new ParseObject(ParseConstants.OBJECT_MEMORY_NAME);
                memory.put(ParseConstants.CAPTION_KEY, inputCaption.getText().toString());
                memory.put(ParseConstants.IMAGE_KEY, file);
                memory.put(ParseConstants.DATE_KEY, Utils.getCurrentDateAsString());
                memory.setACL(new ParseACL(ParseUser.getCurrentUser()));
                memory.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(takeNewPhotoActivity, getString(R.string.toast_saved_photo), Toast.LENGTH_SHORT).show();
                            isPhotoSaved = true;
                        } else {
                            Log.e("Snapchat", e.toString());
                        }
                    }
                });
            } else {
                Toast.makeText(takeNewPhotoActivity, getString(R.string.toast_take_photo_err_already_saved), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(takeNewPhotoActivity, getString(R.string.toast_take_photo_err_no_photo), Toast.LENGTH_SHORT).show();
        }
    }

    @OnItemSelected(R.id.timer_spinner)
    public void spinnerItemSelected(Spinner spinner, int position) {
        timer = spinner.getItemAtPosition(position).toString();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // Display Preview in ImageView
            Bitmap picture = (Bitmap) data.getExtras().get("data");
            displayPhoto.setImageBitmap(picture);
            isPhotoTaken = true;
            isPhotoSaved = false;

            // Save bitmap as byte array
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            if (picture != null) {
                picture.compress(Bitmap.CompressFormat.PNG, 100, stream);
            }
            photoData = stream.toByteArray();
        }
    }
}
