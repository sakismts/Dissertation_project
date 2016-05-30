package com.example.athanasiosmoutsioulis.dissertation_project;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by AthanasiosMoutsioulis on 20/05/16.
 */
public class AttendanceModel {

    //variables
    private static AttendanceModel ourInstance;



    //user info
    String id;
    String fbName;
    Bitmap photoProfile;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFbName() {
        return fbName;
    }

    public void setFbName(String fbName) {
        this.fbName = fbName;
    }

    public Bitmap getPhotoProfile() {
        return photoProfile;
    }

    public void setPhotoProfile(Bitmap photoProfile) {
        this.photoProfile = photoProfile;
    }
    ///////


    private OnSignUpUpdateListener signupUpdateListener; //define var of the interface

    private OnSignInUpdateListener signinUpdateListener; //define var of the interface



    private OnCourseListUpdateListener courseListUpdateListener; //define var of the interface

    //constructor
    public AttendanceModel(Context context) {
        ourInstance=this;
    }



    public static AttendanceModel getOurInstance() {
        return ourInstance;
    }

    public static void setOurInstance(AttendanceModel ourInstance) {
        AttendanceModel.ourInstance = ourInstance;
    }

    /////// Sign Up Section/////////////

    public void signup(String uri){

        Log.i("Signup","Sending request");
      //  String uri = "http://greek-tour-guides.eu/ioannina/dissertation/insert_user.php?id=2&role=student&pass=1&course=a";
        JsonObjectRequest request = new JsonObjectRequest(uri, signupListener,signupErrorListener);

        MyApplication.getInstance().getRequestQueue().add(request);

    }

    Response.Listener<JSONObject> signupListener = new Response.Listener<JSONObject>(){


        @Override
        public void onResponse(JSONObject response) {
            System.out.println(response);
            int result=-1;
            try {
                result= response.getInt("success");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (result==1)
                notifyListenerSignUp(true);
            else
                notifyListenerSignUp(false);
        }
    };

    Response.ErrorListener signupErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {

        }
    };

    //INTERFACE for signup
    public interface  OnSignUpUpdateListener{
        void onSignUpUpdateListener(boolean signed);

    }

    public void setSignupUpdateListener(OnSignUpUpdateListener signupUpdateListener) {
        this.signupUpdateListener = signupUpdateListener;
    }

    private void notifyListenerSignUp(boolean signed){


        if (signupListener != null)

            signupUpdateListener.onSignUpUpdateListener(signed);

    }
    ///////////////////////////////////////////////////////

    /////Sing in Section/////////////

    public void signin(String uri){

        Log.i("Signin","Sending request");
        //  String uri = "http://greek-tour-guides.eu/ioannina/dissertation/insert_user.php?id=2&role=student&pass=1&course=a";
        JsonObjectRequest request = new JsonObjectRequest(uri, signinListener,signinErrorListener);

        MyApplication.getInstance().getRequestQueue().add(request);

    }

    Response.Listener<JSONObject> signinListener = new Response.Listener<JSONObject>(){


        @Override
        public void onResponse(JSONObject response) {
            System.out.println(response);
            int result=-1;
            try {
                result= response.getInt("success");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (result==1)
                notifyListenerSignIn(true);
            else
                notifyListenerSignIn(false);
        }
    };

    Response.ErrorListener signinErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {

        }
    };

    //INTERFACE for signin
    public interface  OnSignInUpdateListener{
        void onSignInUpdateListener(boolean signed);

    }

    public void setSigninUpdateListener(OnSignInUpdateListener signinUpdateListener) {
        this.signinUpdateListener = signinUpdateListener;
    }

    private void notifyListenerSignIn(boolean login){


        if (signinUpdateListener != null)

            signinUpdateListener.onSignInUpdateListener(login);

    }

    ///////////////////////////////////////////////////////////////
    /////////////update course list////////////////////////////////

    public void updateCourseList(){

        Log.i("CourseList","Sending request");
        //  String uri = "http://greek-tour-guides.eu/ioannina/dissertation/insert_user.php?id=2&role=student&pass=1&course=a";
       String uri = "http://greek-tour-guides.eu/ioannina/dissertation/courseList.php?";
        JsonObjectRequest request = new JsonObjectRequest(uri, courseUpdateListener,courseUpdateErrorListener);

        MyApplication.getInstance().getRequestQueue().add(request);

    }

    Response.Listener<JSONObject> courseUpdateListener = new Response.Listener<JSONObject>(){


        @Override
        public void onResponse(JSONObject response) {
            System.out.println(response);
            ArrayList<String> courses = new ArrayList<String>();
            int result=-1;
            try {
                result= response.getInt("success");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (result==1){
                try {
                    JSONArray tmp_course = response.getJSONArray("courses");

                    for (int i=0; i<tmp_course.length();i++){
                        JSONObject objjson = tmp_course.getJSONObject(i);
                        courses.add(objjson.getString("course"));

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
           notifyListenerCourseList(courses);

        }
    };

    Response.ErrorListener courseUpdateErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {

        }
    };

    //interface for course list
    public interface  OnCourseListUpdateListener{
        void oncourseListUpdateListener(ArrayList courses);

    }

    public void setCourseListUpdateListener(OnCourseListUpdateListener courseListUpdateListener) {
        this.courseListUpdateListener = courseListUpdateListener;
    }

    private void notifyListenerCourseList(ArrayList courses){


        if (courseListUpdateListener != null)

            courseListUpdateListener.oncourseListUpdateListener(courses);

    }

}
