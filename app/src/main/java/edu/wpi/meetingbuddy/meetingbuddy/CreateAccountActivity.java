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

/**
 * Created by Anh on 2/17/2018.
 */

public class CreateAccountActivity extends AppCompatActivity {

    private EditText username;
    private EditText password;
    private EditText first;
    private EditText last;
    private Button create;

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
                String user_name = s.toString();
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
                String user_password = s.toString();
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
                String first = s.toString();
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
                String last = s.toString();
                //Save last name
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

                //Open a new blank student meetings view
                Intent i = new Intent(getApplicationContext(), StudentMeetingActivity.class);
                startActivity(i);
            }
        });


    }

}
