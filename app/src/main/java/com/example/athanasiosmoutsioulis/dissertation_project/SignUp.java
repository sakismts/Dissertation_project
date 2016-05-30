package com.example.athanasiosmoutsioulis.dissertation_project;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class SignUp extends AppCompatActivity implements AttendanceModel.OnSignUpUpdateListener, AttendanceModel.OnCourseListUpdateListener {
    EditText id,pass,confpass;
    AutoCompleteTextView course;
    AttendanceModel model = AttendanceModel.getOurInstance();

    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Button submit = (Button)findViewById(R.id.btn_submit);
        id = (EditText)findViewById(R.id.edt_signupId);
        pass = (EditText)findViewById(R.id.edt_signupPass);
        confpass = (EditText)findViewById(R.id.edt_signupConfPass);
        course = (AutoCompleteTextView)findViewById(R.id.autoCompleteTextView);

        model.setSignupUpdateListener(this);
        model.setCourseListUpdateListener(this);
        model.updateCourseList();


        if (submit!= null){
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (id.getText().toString().isEmpty()){
                    Toast.makeText(SignUp.this,"You have to fill the id",Toast.LENGTH_SHORT).show();

                }else if(pass.getText().toString().isEmpty()){
                    Toast.makeText(SignUp.this,"You have to fill the password",Toast.LENGTH_SHORT).show();

                }else if(!confpass.getText().toString().equals(pass.getText().toString())){
                    Toast.makeText(SignUp.this,"The confirm password doesn't match",Toast.LENGTH_SHORT).show();

                }else if(course.getText().toString().isEmpty()){
                    Toast.makeText(SignUp.this,"You have to fill your course",Toast.LENGTH_SHORT).show();

                }else{
                    Log.i("Signup", "ready");
                     String uri = "http://greek-tour-guides.eu/ioannina/dissertation/insert_user.php?id="+id.getText().toString()+"&role=student&pass="+pass.getText().toString()+"&course="+course.getText().toString();
                    model.signup(uri);

                }


            }
        });

        }

    }

    @Override
    public void onSignUpUpdateListener(boolean signed) {
        if (signed==true){
            Log.i("Signup", "success");
            Toast.makeText(SignUp.this,"The registration was completed",Toast.LENGTH_SHORT).show();
            super.onBackPressed();
        }else{
            Toast.makeText(SignUp.this,"The registration doesn't completed",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void oncourseListUpdateListener(ArrayList courses) {
        //retrieve the array with the courses thar are registed in the database
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,courses);
        course.setAdapter(adapter);
    }
}
