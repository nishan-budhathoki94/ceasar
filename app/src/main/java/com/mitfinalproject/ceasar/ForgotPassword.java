package com.mitfinalproject.ceasar;

import android.content.Intent;
import androidx.annotation.NonNull;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {
    Button btnResetPswd;
    ProgressBar progressBar;
    private TextInputLayout textInputEmail;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        btnResetPswd = findViewById(R.id.buttonResetPassword);
        progressBar = findViewById(R.id.progressBarResetPassword);
        textInputEmail = findViewById(R.id.textInputLayoutForgotPassword);
        mAuth = FirebaseAuth.getInstance();
        btnResetPswd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateEmail()) {
                    final String emailInput = textInputEmail.getEditText().getText().toString().trim();
                    progressBar.setVisibility(View.VISIBLE);
                    mAuth.sendPasswordResetEmail(emailInput).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(ForgotPassword.this, "Password reset link has been sent to" + emailInput + ". Please check your email",
                                        Toast.LENGTH_LONG).show();
                                Intent loginActivity = new Intent(ForgotPassword.this, Login.class);
                                finish();
                                startActivity(loginActivity);
                            }

                            else {
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(ForgotPassword.this, "Something went wrong, please check your email address again.",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });{

                    }
                }

            }
        });
    }

    //validate email
    public boolean validateEmail() {
        String emailInput = textInputEmail.getEditText().getText().toString().trim();

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

}
