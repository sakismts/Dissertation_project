package com.example.athanasiosmoutsioulis.dissertation_project;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity implements AttendanceModel.OnSignInUpdateListener{
    EditText id,pass;
    AttendanceModel model = AttendanceModel.getOurInstance();
    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        model.setSigninUpdateListener(this);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        // views
        Button login = (Button)findViewById(R.id.btn_login);
        Button signup = (Button)findViewById(R.id.btn_signup);
        id = (EditText)findViewById(R.id.edt_id);
        pass = (EditText)findViewById(R.id.edt_pass);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (id.getText().toString().isEmpty()){
                    Toast.makeText(LoginActivity.this, "You have to fill the id", Toast.LENGTH_SHORT).show();

                }else if(pass.getText().toString().isEmpty()){
                    Toast.makeText(LoginActivity.this,"You have to fill the password",Toast.LENGTH_SHORT).show();

                }else{
                    Log.i("Signup", "ready");
                    String uri = "http://greek-tour-guides.eu/ioannina/dissertation/check_user.php?id="+id.getText().toString()+"&pass="+pass.getText().toString();
                    model.signin(uri);

                }
            }
        });


        //views actions
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signup = new Intent(getApplicationContext(), SignUp.class);
                startActivity(signup);

            }
        });
    }

    @Override
    public void onSignInUpdateListener(boolean login) {
        if (login==true){
            Log.i("Signin", "success");
            SharedPreferences.Editor editor = sharedpreferences.edit();
            //put the user info to the shared preferences
            editor.putString("id", id.getText().toString());
            editor.putString("pass", pass.getText().toString());
            editor.putBoolean("logged", true);
            editor.commit();
            Toast.makeText(LoginActivity.this,"The Login was successful",Toast.LENGTH_SHORT).show();
            Intent returnIntent = new Intent();
            returnIntent.putExtra("result", true);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();

        }else{
            Toast.makeText(LoginActivity.this,"The login wasn't successful",Toast.LENGTH_SHORT).show();
            Intent returnIntent = new Intent();
            returnIntent.putExtra("result", true);
            setResult(Activity.RESULT_CANCELED, returnIntent);
        }
    }
}
