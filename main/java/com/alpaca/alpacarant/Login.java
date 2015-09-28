package com.alpaca.alpacarant;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class Login extends ActionBarActivity {

    EditText editUsername, editPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void onLoginButtonClick(View v) {
        JSONObject jsonObject;

        editUsername = (EditText) findViewById(R.id.editUsername);
        editPassword = (EditText) findViewById(R.id.editPassword);

        String username = editUsername.getText().toString();
        String password = editPassword.getText().toString();
        Log.i("MyTag", "Beginning");

        if (username.length() == 0 || password.length() == 0) {
            Toast.makeText(getApplicationContext(), "Username or password field cannot be empty", Toast.LENGTH_SHORT).show();
        } else {
            jsonObject = createJSONFile(username, password);
            try {
                Log.i("Before: ", "Getting http response");
                //I DO NOT KNOW HOW TO VERIFY WITH SERVER DATABASE
                Networking networking = new Networking();
                networking.execute("http://nturant.me/signin", jsonObject);
                Log.i("After: ", "Logged in");

                startActivity(new Intent(Login.this, MainPage.class));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class Networking extends AsyncTask{

        @Override
        protected Object doInBackground(Object[] params) {
            try {
                makeRequest((String) params[0], (JSONObject) params[1]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private JSONObject createJSONFile(String username, String password) {
        try {
            JSONObject object = new JSONObject();

            object.put("email", username);
            object.put("password", password);
            Log.i("JSON String: ", object.toString());

            return object;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private void makeRequest(String url, JSONObject object) throws Exception {
        Log.i("Inside makeRequest: ", "Beginning");
        //instantiates httpclient to make request
        HttpClient httpClient = new DefaultHttpClient();

        Log.i("Inside makeRequest: ", "httpclient");
        //url with the post data
        HttpPost httpPost = new HttpPost(url);

        Log.i("Inside makeRequest: ", "httppost");
        //passes JSONObject to string entity
        StringEntity stringEntity = new StringEntity(object.toString());
        stringEntity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

        Log.i("Inside makeRequest: ", "stringentity");
        //sets the post request as the resulting string
        httpPost.setEntity(stringEntity);

        Log.i("Inside makeRequest: ", "setentity");
        //handles what is returned from the page
        ResponseHandler responseHandler = new BasicResponseHandler();
        Log.i("Inside makeRequest: ", "return");
        HttpResponse httpResponse = (HttpResponse) httpClient.execute(httpPost, responseHandler);

        if (httpResponse != null) {
            StringBuilder sb = new StringBuilder();
            BufferedReader rd = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));

            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line + "\n");
            }

            String result;
            result = sb.toString();
            Log.i("DataHandler", "Append String " + result);
        }
        else{
            Log.i("DataHandler", "Httpresponse empty");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
