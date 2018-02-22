package edu.wpi.meetingbuddy.meetingbuddy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Anh on 2/17/2018.
 */

public class CreateAccountActivity extends AppCompatActivity {

    private EditText username;
    private EditText password;
    private EditText first;
    private EditText last;
    private EditText phonenum;
    private Button create;
    String user_name;
    String user_password;
    String firstname;
    String lastname;
    String phone;
    String schedule = "";
    private NetworkManager networkManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_account);

        //Username
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

        //Password
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
        //first and last name
        first = findViewById(R.id.firstname);
        first.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                firstname = s.toString();
                //Save first name
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        last = findViewById(R.id.lastname);
        last.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                lastname = s.toString();
                //Save last name
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //phonenum
        phonenum = findViewById(R.id.phonenum);
        phonenum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                phone = s.toString();
                //Save phone
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

       //Create account
        create = findViewById(R.id.create_button);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Create account
                //...
                JSONObject creds = new JSONObject();
                try {
                    creds.put("username", user_name);
                    creds.put("password", user_password);
                    creds.put("phone", phone);
                    creds.put("first_name", firstname);
                    creds.put("last_name", lastname);
                    creds.put("schedule", schedule);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                networkManager.post(NetworkManager.url+"/newAccount", creds.toString(), new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        System.out.println("Failed to connect");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Failed to connect", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                    @Override
                    public void onResponse(Response response) throws IOException {
                        final String responseStr = response.body().string();
                        final int statusCode = response.code();
                        try {
                            final JSONObject jsonRes = new JSONObject(responseStr);
                            final String status = jsonRes.getString("status");

//                        final String messageText = "Status code : " + response.code() +
//                                "n" +
//                                "Response body : " + responseStr;
//                        System.out.println("Received response");

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), status, Toast.LENGTH_LONG).show();
                                }
                            });


                            if(status.equals("success")) {
                                //Get my account information
                                Account myAccount = new Account();
                                myAccount.fromJSON(jsonRes);

                                //Go to My meetings, send it my account information
                                Intent i = new Intent(getApplicationContext(), MyMeetingActivity.class);
                                i.putExtra("Account", myAccount);
                                startActivity(i);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

                //Open a new blank student meetings view
                //Intent i = new Intent(getApplicationContext(), UserLoginActivity.class);
                //startActivity(i);
            }


}
