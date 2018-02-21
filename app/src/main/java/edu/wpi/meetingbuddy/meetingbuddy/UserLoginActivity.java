package edu.wpi.meetingbuddy.meetingbuddy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;


/**
 * Created by Anh on 2/15/2018.
 */

public class UserLoginActivity extends Activity {


    private EditText username;
    private EditText password;
    private Button login;
    private Button create_account;
    private String user_name;
    private String user_password;

    private NetworkManager networkManager = new NetworkManager();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_log_in);

        username = findViewById(R.id.username);
        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                user_name = s.toString();
                //Save the username for authentication
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        password = findViewById(R.id.password);
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                user_password = s.toString();
                //Save the password for authentication
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        login = findViewById(R.id.log_in);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log in

                String json = "{name: " + user_name + ", password: " + user_password + "}";
                networkManager.post(networkManager.url+"/Login", json, new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                    }
                    @Override
                    public void onResponse(Response response) throws IOException {
                        String responseStr = response.body().string();
                        final String messageText = "Status code : " + response.code() +
                                "n" +
                                "Response body : " + responseStr;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), messageText, Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });


                //Go to My meetings
                Intent i = new Intent(getApplicationContext(),MyMeetingActivity.class);
                startActivity(i);
            }
        });

        create_account = findViewById(R.id.create_account);
        create_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),CreateAccountActivity.class);
                startActivity(i);
            }
        });


    }
}