package com.example.athanasiosmoutsioulis.dissertation_project;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Created by AthanasiosMoutsioulis on 20/05/16.
 */
public class MyApplication extends Application {
    private ImageLoader imageLoader;
    static private MyApplication instance;
    private RequestQueue requestQueue;

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }

    public static MyApplication getInstance() {
        return instance;
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance=this;
        requestQueue = Volley.newRequestQueue(this);
        int cacheSize = 4 * 1024 *1024;
        imageLoader = new ImageLoader(requestQueue, new LruBitmapCache(cacheSize));




    }
}
