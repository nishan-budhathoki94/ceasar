package com.mitfinalproject.ceasar;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Login extends AppCompatActivity {
    private TextInputLayout textInputEmail;
    private TextInputLayout textInputPassword;
    private ProgressBar progressbar;
    private FirebaseAuth mAuth;
    private String name,phone,type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        textInputEmail = findViewById(R.id.textInputLayoutEmail);
        textInputPassword = findViewById(R.id.textInputLayoutPassword);
        progressbar = findViewById(R.id.progressBarLogin);
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

    //validate password
    public boolean validatePassword() {
        String passwordInput = textInputPassword.getEditText().getText().toString().trim();

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
        Intent signUp = new Intent(this, SignUp.class);
            startActivity(signUp);
    }

    //forgot password activity
    public void forgotPasswordLink(View v) {
        Intent forgotPassword = new Intent(this, ForgotPassword.class);
        startActivity(forgotPassword);
    }

    public void loginBtnClick(View v) {
        if(validateEmail() && validatePassword()) {
            progressbar.setVisibility(View.VISIBLE);
            final String password = textInputPassword.getEditText().getText().toString().trim();
            final String email = textInputEmail.getEditText().getText().toString().trim();
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("login", "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                //check if user email is verified
                                if(user.isEmailVerified()) {
                                    String server_url = "http://everestelectricals.com.au/ceasar/getuser.php";
                                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, server_url,null,
                                            new Response.Listener<JSONObject>() {
                                                @Override
                                                public void onResponse(JSONObject response) {
                                                    try {
                                                        JSONArray jsonArray = response.getJSONArray("data");
                                                        Log.d("jsonres", "onResponse: "+response);
                                                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                                                        name = jsonObject.getString("name").trim();
                                                        phone = jsonObject.getString("phone").trim();
                                                        type = jsonObject.getString("type").trim();
                                                        Log.d("jsonres", "onResponse: "+jsonObject+name+phone+type);

                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }

                                                    if (type.equals("customer")) {
                                                        progressbar.setVisibility(View.GONE);
                                                        Intent customerPanel = new Intent(Login.this,CustomerPanel.class);
                                                        customerPanel.putExtra("name",name);
                                                        customerPanel.putExtra("type",type);
                                                        finish();
                                                        startActivity(customerPanel);
                                                    }
                                                    else {
                                                        progressbar.setVisibility(View.GONE);
                                                        Intent adminPanel = new Intent(Login.this,ItemList.class);
                                                        adminPanel.putExtra("name",name);
                                                        adminPanel.putExtra("type",type);
                                                        finish();
                                                        startActivity(adminPanel);
                                                    }

                                                }
                                            },

                                            new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                    Log.d("Volley", "Cannot connect to database");
                                                }
                                            }) {

                                        @Override
                                        protected Map<String, String> getParams() throws AuthFailureError {
                                            Map<String, String> params = new HashMap<String, String>();
                                            params.put("email", email);
                                            return params;
                                        }


                                    };

                                    VolleySingleton.getInstance(Login.this).addToRequestQueue(request);



                                }
                                else{
                                    progressbar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(Login.this, "Email "+email+" not verified. Please verify your email first",
                                            Toast.LENGTH_LONG).show();
                                }
                                //updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                progressbar.setVisibility(View.INVISIBLE);
                                Log.w("login", "signInWithEmail:failure", task.getException());
                                Toast.makeText(Login.this, "Incorrect Username or Password",
                                        Toast.LENGTH_LONG).show();
                                //updateUI(null);
                            }

                            // ...
                        }
                    });

        }


    }
}
