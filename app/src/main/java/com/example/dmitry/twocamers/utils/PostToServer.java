package com.example.dmitry.twocamers.utils;

import android.content.Context;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dmitry on 28.04.2016.
 */
public class PostToServer {
    private static final String SERVER = "http://dobro.in.net/";
    private static final String DO ="mobile/upload/uploadPhoto.do";

    private static String convert(File file) {
        RandomAccessFile f = null;
        byte[] b = null;
        try {
            f = new RandomAccessFile(file, "r");
            b = new byte[(int) f.length()];
            f.read(b);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (f != null) {
                try {
                    f.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    public static void publicPhoto(final Context context, File file, final String token){
        //convert photo to base64
        final String stringBase64 = convert(file);
        Log.d("log",stringBase64);
        //create queue
        RequestQueue queue = Volley.newRequestQueue(context);
        //create request
        StringRequest sr = new StringRequest(Request.Method.POST,SERVER+DO, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(context,response,Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context,error.toString(),Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> headers = new HashMap<>();
                headers.put("Charset", "UTF-8");
                headers.put("Content-Type", "application/x-javascript");
                return headers;
            }
        };

        queue.add(sr);
    }
}
