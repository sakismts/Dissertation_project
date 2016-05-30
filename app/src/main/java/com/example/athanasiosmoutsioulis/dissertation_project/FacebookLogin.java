package com.example.athanasiosmoutsioulis.dissertation_project;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class FacebookLogin extends AppCompatActivity {
    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;
    AttendanceModel model = AttendanceModel.getOurInstance();
    private TextView info;
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;
    private Activity myactivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.myactivity=this;

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {
            }
        };

        //in case the profile is changed
        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                if(newProfile != null){
                    info.setText(newProfile.getName());
                    Uri imageUrl= newProfile.getProfilePictureUri(300,300);
                    new DownloadImage((ImageView)findViewById(R.id.imageView)).execute(imageUrl.toString());
                    model.setFbName(newProfile.getName());
                }else{
                    ImageView img = (ImageView)findViewById(R.id.imageView);
                    img.setImageResource(R.drawable.photo_profile);
                    model.setFbName("");
                    info.setText("You are not logged in to Facebook");
                }
            }
        };
        accessTokenTracker.startTracking();
        profileTracker.startTracking();
        setContentView(R.layout.activity_facebook_login);
        setFinishOnTouchOutside(false);
        callbackManager = CallbackManager.Factory.create();
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);


        loginButton = (LoginButton)findViewById(R.id.login_button);
        info = (TextView)findViewById(R.id.info);
        Button cancel = (Button)findViewById(R.id.btn_cancel);
        //Load the profile photo from shared preferences
        Profile profile = Profile.getCurrentProfile();
        if (profile != null) {
            String previouslyEncodedImage = sharedpreferences.getString("photo_profile", "");
            if( !previouslyEncodedImage.equalsIgnoreCase("") ){
                byte[] b = Base64.decode(previouslyEncodedImage, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
                ImageView img = (ImageView)findViewById(R.id.imageView);
                img.setImageBitmap(bitmap);
            }
            info.setText(profile.getName().toString());
        }

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", true);
                setResult(Activity.RESULT_OK, returnIntent);
                FacebookLogin.super.onBackPressed();
            }
        });

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken accessToken = loginResult.getAccessToken();
                Profile profile = Profile.getCurrentProfile();
                if (profile != null) {
                    System.out.println(profile.getName());
                    info.setText(profile.getName());
                    model.setFbName(profile.getName());
                    Uri imageUrl = profile.getProfilePictureUri(300, 300);
                    new DownloadImage((ImageView) findViewById(R.id.imageView)).execute(imageUrl.toString());
                }

            }

            @Override
            public void onCancel() {
                info.setText("Login attempt canceled.");

            }

            @Override
            public void onError(FacebookException e) {

                info.setText("Login attempt failed.");
            }
        });

        loginButton.setReadPermissions("user_friends");

}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }

public class DownloadImage extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;
    private ProgressDialog dialog;

    public DownloadImage(ImageView bmImage) {
        this.bmImage = bmImage;
       dialog = new ProgressDialog(myactivity); // this = YourActivity
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Loading. Please wait...");
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
    }



    @Override
    protected void onPreExecute() {
        dialog.show();
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
        //saving image to shared preferences
        Bitmap tmp=result;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        tmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        SharedPreferences.Editor edit=sharedpreferences.edit();
        edit.putString("photo_profile",encodedImage);
        edit.commit();
        ///
        bmImage.setImageBitmap(result);
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", true);
        model.setPhotoProfile(result);
        setResult(Activity.RESULT_OK, returnIntent);
        dialog.dismiss();
        FacebookLogin.this.finish();

    }
}

}

