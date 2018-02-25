package com.dartmouth.kd.devents;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


public class UserProfile extends Activity {

    public static final int REQUEST_CODE_TAKE_FROM_CAMERA = 0;
    public static final int CODE_GALLERY = 1;

    private static final String URI_INSTANCE_STATE_KEY = "saved_uri";
    private static final String URI_INSTANCE_STATE_KEY_TEMP = "saved_uri_temp";
    private static final String CAMERA_CLICKED_KEY = "clicked";

    private ImageView mImageView;
    private Uri mImageCaptureUri, mTempUri;
    private Boolean stateChanged = false, cameraClicked = false,clickedbyCam=false;
    private Button Button_change, Button_cancel, Button_save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        if (savedInstanceState != null) {
            mImageCaptureUri = savedInstanceState
                    .getParcelable(URI_INSTANCE_STATE_KEY);
            mTempUri = savedInstanceState.getParcelable(URI_INSTANCE_STATE_KEY_TEMP);
            cameraClicked = savedInstanceState.getBoolean(CAMERA_CLICKED_KEY);
            stateChanged=true;
        }
        Button_change = (Button) findViewById(R.id.changeButton);
        Button_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.app.DialogFragment fragment = DialogFragment.newInstance(DialogFragment.DIALOG_PHOTO);
                fragment.show(getFragmentManager(), "Photo Picker");            }
        });
        Button_save = (Button) findViewById(R.id.bSave);
        Button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfile();
                Toast.makeText(getApplicationContext(),
                        getString(R.string.ui_profile_toast_save_text),
                        Toast.LENGTH_SHORT).show();
                // Close the activity
                finish();
            }
        });
        Button_cancel = (Button) findViewById(R.id.bCancel);
        Button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCancelClick();
            }
        });
        mImageView = (ImageView)findViewById(R.id.imageView);
        loadProfile();

    }

    public void onCancelClick(){
        finish();
    }

    public void changeImage(){
        Intent intent;


        // Take photo from camera，
        // Construct an intent with action
        // MediaStore.ACTION_IMAGE_CAPTURE
        intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Construct temporary image path and name to save the taken
        // photo
        ContentValues values = new ContentValues(1);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
        mImageCaptureUri = getContentResolver()
                .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

/*
    This was the previous code to generate a URI. This was throwing an exception -
    "android.os.StrictMode.onFileUriExposed" in Android N.
    This was because StrictMode prevents passing URIs with a file:// scheme. Once you
    set the target SDK to 24, then the file:// URI scheme is no longer supported because the
    security is exposed. You can change the  targetSDK version to be <24, to use the following code.
    The new code as written above works nevertheless.


        mImageCaptureUri = Uri.fromFile(new File(Environment
                .getExternalStorageDirectory(), "tmp_"
                + String.valueOf(System.currentTimeMillis()) + ".jpg"));
*/
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                mImageCaptureUri);
        intent.putExtra("return-data", true);
        try {

            startActivityForResult(intent, REQUEST_CODE_TAKE_FROM_CAMERA);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }

    }
    public void loadProfile() {
        String key, str;
        int val;

        key = getString(R.string.preference_name);
        SharedPreferences prefs = getSharedPreferences(key, MODE_PRIVATE);

        key = getString(R.string.ui_profile_name);
        str = prefs.getString(key, "");
        ((EditText) findViewById(R.id.userNameEnter)).setText(str);

        key = getString(R.string.ui_profile_email);
        str = prefs.getString(key, "");
        ((EditText) findViewById(R.id.userEmail)).setText(str);



        key = getString(R.string.ui_profile_gender);
        val = prefs.getInt(key, -1);

        if (val >= 0) {
            RadioButton radioBtn = (RadioButton) ((RadioGroup) findViewById(R.id.radiogroup_Gender))
                    .getChildAt(val);
            radioBtn.setChecked(true);
        }

        key = getString(R.string.ui_profile_class);
        str = prefs.getString(key, "");
        ((EditText) findViewById(R.id.classYear)).setText(str);

        key = getString(R.string.ui_profile_major);
        str = prefs.getString(key, "");
        ((EditText) findViewById(R.id.Major)).setText(str);

        loadSnap();
    }

    public void saveProfile() {
        String key, str2;
        int val2;

        key = getString(R.string.preference_name);
        SharedPreferences prefs = getSharedPreferences(key, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // Write screen contents into corresponding editor fields.
        key = getString(R.string.ui_profile_name);
        str2 = ((EditText) findViewById(R.id.userNameEnter)).getText().toString();
        editor.putString(key, str2);

        key = getString(R.string.ui_profile_email);
        str2 = ((EditText) findViewById(R.id.userEmail)).getText()
                .toString();
        editor.putString(key, str2);


        key = getString(R.string.ui_profile_gender);
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radiogroup_Gender);
        val2 = radioGroup.indexOfChild(findViewById(radioGroup
                .getCheckedRadioButtonId()));
        editor.putInt(key, val2);

        key = getString(R.string.ui_profile_class);
        str2 = ((EditText) findViewById(R.id.classYear)).getText()
                .toString();
        editor.putString(key, str2);

        key = getString(R.string.ui_profile_major);
        str2 = ((EditText) findViewById(R.id.Major)).getText()
                .toString();
        editor.putString(key, str2);

        editor.apply();

        saveSnap();


    }
    private void loadSnap() {



        try {
            FileInputStream fis;

            if(stateChanged && cameraClicked){
                if(!Uri.EMPTY.equals(mTempUri)) {
                    mImageView.setImageURI(mTempUri);
                    stateChanged = false;
                }
            } else {
                fis = openFileInput(getString(R.string.profile_photo_file_name));
                Bitmap bmap = BitmapFactory.decodeStream(fis);
                mImageView.setImageBitmap(bmap);

                fis.close();
            }

        } catch (IOException e) {
            // Default profile photo if no photo saved before.
            mImageView.setImageResource(R.mipmap.garfield);
        }
    }
    private void saveSnap() {

        // Commit all the changes into preference file
        // Save profile image into internal storage.
        mImageView.buildDrawingCache();
        Bitmap bmap = mImageView.getDrawingCache();
        try {
            FileOutputStream fos = openFileOutput(
                    getString(R.string.profile_photo_file_name), MODE_PRIVATE);
            bmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_logout) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;

        switch (requestCode) {
            case REQUEST_CODE_TAKE_FROM_CAMERA:
                // Send image taken from camera for cropping
                beginCrop(mImageCaptureUri);
                clickedbyCam=true;
                break;

            case CODE_GALLERY:
                Uri srcUri = data.getData();
                beginCrop(srcUri);
                break;

            case Crop.REQUEST_CROP:
                // Update image view after image crop
                // Set the picture image in UI
                handleCrop(resultCode, data);

                // Delete temporary image taken by camera after crop.
                if(clickedbyCam) {
                    File f = new File(mImageCaptureUri.getPath());
                    if (f.exists())
                        f.delete();
                    clickedbyCam=false;
                }

                break;
        }
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the image capture uri before the activity goes into background
        outState.putParcelable(URI_INSTANCE_STATE_KEY, mImageCaptureUri);
        outState.putParcelable(URI_INSTANCE_STATE_KEY_TEMP,mTempUri);
        outState.putBoolean(CAMERA_CLICKED_KEY,cameraClicked);
    }
    /** Method to start Crop activity using the library
     *	Earlier the code used to start a new intent to crop the image,
     *	but here the library is handling the creation of an Intent, so you don't
     * have to.
     *  **/
    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            mTempUri = Crop.getOutput(result);
            mImageView.setImageResource(0);
            mImageView.setImageURI(mTempUri);
            Log.d("TAG", "came here");
            cameraClicked=true;

        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    public void onPhotoPickerItemSelected(int item) {
        Intent intent;

        switch (item) {

            case 0:
                changeImage();
                break;

            case 1:
                intent = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(intent, CODE_GALLERY);
                break;

            default:
                return;
        }    }
}

