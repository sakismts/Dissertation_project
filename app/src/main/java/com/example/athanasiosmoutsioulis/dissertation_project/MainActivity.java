package com.example.athanasiosmoutsioulis.dissertation_project;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;

import java.io.InputStream;


public class MainActivity extends AppCompatActivity {
    AttendanceModel model = new AttendanceModel(this);
    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;
    static final int LOGIN_REQUEST = 1;  // The request code
    static final int FBLOGIN_REQUEST = 2;  // The request code
    TextView id;
    TextView fbName;
    ImageView photo_profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //initialize the facebook sdk
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        setContentView(R.layout.activity_main_two);
        // Shared preferences for user id pass and if he is logged
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        photo_profile = (ImageView)findViewById(R.id.img_fbPhoto);
        id = (TextView)findViewById(R.id.tv_id);
        fbName = (TextView)findViewById(R.id.tv_fbName);

        Profile profile = Profile.getCurrentProfile();
        if (profile!=null){
           //load the photo profile from preferences
            String previouslyEncodedImage = sharedpreferences.getString("photo_profile", "");
            if( !previouslyEncodedImage.equalsIgnoreCase("") ){
                byte[] b = Base64.decode(previouslyEncodedImage, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
                RoundedBitmapDrawable roundedBitmapDrawable= RoundedBitmapDrawableFactory.create(getResources(), bitmap);
                roundedBitmapDrawable.setCornerRadius(25);
                photo_profile.setImageDrawable(roundedBitmapDrawable);
            }

            fbName.setVisibility(View.VISIBLE);
            fbName.setText(profile.getName());

        }else{
            Bitmap imageBitmap=BitmapFactory.decodeResource(getResources(),  R.drawable.photo_profile);
            RoundedBitmapDrawable roundedBitmapDrawable= RoundedBitmapDrawableFactory.create(getResources(), imageBitmap);
            roundedBitmapDrawable.setCornerRadius(25);
            photo_profile.setImageDrawable(roundedBitmapDrawable);
            fbName.setVisibility(View.GONE);

        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);//Menu Resource, Menu
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage(R.string.logout_message)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //clear the shared preferences
                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                editor.putString("id", "");
                                editor.putString("pass", "");
                                editor.putBoolean("logged", false);
                                editor.putString("photo_profile", "");
                                editor.commit();
                                LoginManager.getInstance().logOut();
                                Bitmap imageBitmap=BitmapFactory.decodeResource(getResources(),  R.drawable.photo_profile);
                                RoundedBitmapDrawable roundedBitmapDrawable= RoundedBitmapDrawableFactory.create(getResources(), imageBitmap);
                                roundedBitmapDrawable.setCornerRadius(25);
                                photo_profile.setImageDrawable(roundedBitmapDrawable);
                                fbName.setVisibility(View.GONE);
                                Intent loginActivity = new Intent(MainActivity.this, LoginActivity.class);
                                startActivityForResult(loginActivity, LOGIN_REQUEST);

                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });
                // Create the AlertDialog object and return it
                 builder.create().show();
                return true;
            case R.id.fbLogin:

                Intent facebookLogin = new Intent(MainActivity.this, FacebookLogin.class);
                startActivityForResult(facebookLogin, FBLOGIN_REQUEST);
               // startActivity(facebookLogin);
                return true;
            case R.id.calendar:

                AlertDialog.Builder builder_calendar = new AlertDialog.Builder(MainActivity.this);
                LayoutInflater inflater = this.getLayoutInflater();
                View dialog_view=inflater.inflate(R.layout.import_calendar_dialog, null);
                builder_calendar.setView(dialog_view);
                final EditText feed_url= (EditText)dialog_view.findViewById(R.id.edt_calendar_url);

                builder_calendar.setMessage("Paste the url for the caledanr feed")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // FIRE ZE MISSILES!
                                String calendar_url= feed_url.getText().toString();
                                calendar_url=calendar_url.replace("webcal","http");
                                Log.i("calendar",calendar_url);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });

                builder_calendar.create().show();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // if the user is not logged than to the the Login screen
        if (sharedpreferences.getBoolean("logged", false)== false){
            //the activity login will return a result if the user logged in or no
            Intent loginActivity = new Intent(MainActivity.this, LoginActivity.class);
            startActivityForResult(loginActivity, LOGIN_REQUEST);
        }else{
            //update the gui
            id.setText(sharedpreferences.getString("id","User"));

        }

    }
    private void test(){

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == LOGIN_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Log.i("Request","ok");
                Boolean result=data.getBooleanExtra("result", false);
                if (result == true){
                    //if the user logged in the update the ui with his info
                    id.setText(sharedpreferences.getString("id","User"));
                    Profile profile = Profile.getCurrentProfile();
                    if (profile==null){
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Facebook Login")
                                .setMessage("Do you also want to login with Facebook")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent facebookLogin = new Intent(MainActivity.this, FacebookLogin.class);
                                        startActivityForResult(facebookLogin, FBLOGIN_REQUEST);
                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // do nothing
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();

                    }

                }

            }
            // if the user wasn't logged in the close the application
            if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("Request","cancel");
                finish();
            }
        }else  if (requestCode == FBLOGIN_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Log.i("FBRequest","ok");
                Boolean result=data.getBooleanExtra("result", false);
                if (result == true){
                    //if the user logged in the update the ui with his info
                    Profile profile = Profile.getCurrentProfile();
                    if (profile!=null){
                        //load photo profile from shared preferences
                        String previouslyEncodedImage = sharedpreferences.getString("photo_profile", "");
                        if( !previouslyEncodedImage.equalsIgnoreCase("") ){
                            byte[] b = Base64.decode(previouslyEncodedImage, Base64.DEFAULT);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
                            RoundedBitmapDrawable roundedBitmapDrawable= RoundedBitmapDrawableFactory.create(getResources(), bitmap);
                            roundedBitmapDrawable.setCornerRadius(25);
                            photo_profile.setImageDrawable(roundedBitmapDrawable);
                        }
                        fbName.setVisibility(View.VISIBLE);
                        fbName.setText(profile.getName());

                    }else{
                        //set default images
                        Bitmap imageBitmap=BitmapFactory.decodeResource(getResources(),  R.drawable.photo_profile);
                        RoundedBitmapDrawable roundedBitmapDrawable= RoundedBitmapDrawableFactory.create(getResources(), imageBitmap);
                        roundedBitmapDrawable.setCornerRadius(25);
                        photo_profile.setImageDrawable(roundedBitmapDrawable);
                        fbName.setVisibility(View.GONE);

                    }

                }

            }

        }
    }


}
