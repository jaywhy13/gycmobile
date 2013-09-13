package jaywhy13.gycweb.net;

import android.util.Log;
import android.widget.SimpleCursorAdapter;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import jaywhy13.gycweb.GYCMainActivity;

/**
 * Created by jay on 9/12/13.
 */
public class Internet {

    public static String get(String url){
        // Get the client
        HttpClient client = new DefaultHttpClient();
        // Prepare a http request and response
        HttpGet request = new HttpGet(url);
        HttpResponse response;

        // Make the request
        try {
            response = client.execute(request);
            HttpEntity entity = response.getEntity();
            if(entity != null){
                InputStream is = entity.getContent();
                String result = convertInputStreamToString(is);
                Log.d(GYCMainActivity.TAG, "Got result: \n" + result);
                is.close();
                return result;
            }
            return null;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String post(String url){
        return post(url, null);
    }

    public static String post(String url, List<NameValuePair> params){
        // Get the client
        HttpClient client = new DefaultHttpClient();
        // Prepare a http request and response
        HttpPost request = new HttpPost(url);
        if(params != null){
            try {
                request.setEntity(new UrlEncodedFormEntity(params));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        HttpResponse response;

        // Make the request
        try {
            response = client.execute(request);
            HttpEntity entity = response.getEntity();
            if(entity != null){
                InputStream is = entity.getContent();
                String result = convertInputStreamToString(is);
                Log.d(GYCMainActivity.TAG, "Got result: \n" + result);
                is.close();
                return result;
            }
            return null;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String convertInputStreamToString(InputStream is){
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuilder stringBuilder = new StringBuilder();
        String line = null;
        try {
            while((line = br.readLine()) != null){
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

}
