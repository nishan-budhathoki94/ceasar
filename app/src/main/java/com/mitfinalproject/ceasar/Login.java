package com.mitfinalproject.ceasar;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;

import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private TextInputLayout textInputEmail;
    private TextInputLayout textInputPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textInputEmail = findViewById(R.id.textInputLayoutEmail);
        textInputPassword = findViewById(R.id.textInputLayoutPassword);

    }

    //validate email
    public boolean validateEmail() {
        String emailInput = textInputEmail.getEditText().toString().trim();

        if(emailInput.isEmpty()) {
            textInputEmail.setError("Email cannot be empty");
            return false;
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            textInputEmail.setError("Invalid Email Address");
            return false;
        }
        else {
            textInputEmail.setError(null);
            return true;
        }
    }

    //validate password
    public boolean validatePassword() {
        String passwordInput = textInputEmail.getEditText().toString().trim();

        if(passwordInput.isEmpty()) {
            textInputPassword.setError("Password cannot be empty");
            return false;
        }
        else {
            textInputPassword.setError(null);
            return true;
        }
    }

    //method calls activity_signUp
    public void newUserClick(View v) {
        Intent signUp = new Intent(this,Signup.class);
            startActivity(signUp);

    }
}
