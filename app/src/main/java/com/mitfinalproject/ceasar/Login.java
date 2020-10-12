package com.mitfinalproject.ceasar;

import android.content.Intent;
import androidx.annotation.NonNull;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
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
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mitfinalproject.ceasar.Admin.ItemListAdmin;
import com.mitfinalproject.ceasar.Customer.ItemListCustomer;
import com.mitfinalproject.ceasar.Data.Constants;
import com.mitfinalproject.ceasar.Employee.ActiveOrdersEmployee;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {
    private TextInputLayout textInputEmail;
    private TextInputLayout textInputPassword;
    private ProgressBar progressbar;
    private FirebaseAuth mAuth;
    private String name,phone,type= "customer",password,email;

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
            progressbar.setVisibility(View.INVISIBLE);
            return false;
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            textInputEmail.setError("Invalid Email Address");
            progressbar.setVisibility(View.INVISIBLE);
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
            progressbar.setVisibility(View.INVISIBLE);
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
            password = textInputPassword.getEditText().getText().toString().trim();
            email = textInputEmail.getEditText().getText().toString().trim();
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
                                    StringRequest request = new StringRequest(Request.Method.POST, server_url,
                                            new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
                                                    try {
                                                        Log.d("jsonres", "onResponse: "+response);
                                                        JSONObject jo = new JSONObject(response);
                                                        JSONArray jsonArray = jo.getJSONArray("data");
                                                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                                                        name = jsonObject.getString("name").trim();
                                                        phone = jsonObject.getString("phone").trim();
                                                        type = jsonObject.getString("type").trim();
                                                        Log.d("jsonres", "onResponse: "+jsonObject+name+phone+type);

                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }

                                                    if (type.equalsIgnoreCase(Constants.TYPE_CUSTOMER)) {
                                                        progressbar.setVisibility(View.GONE);
                                                        Intent customerPanel = new Intent(Login.this, ItemListCustomer.class);
                                                        customerPanel.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                        finish();
                                                        startActivity(customerPanel);
                                                    }
                                                    else if (type.equalsIgnoreCase(Constants.TYPE_ADMIN)){
                                                        progressbar.setVisibility(View.GONE);
                                                        Intent adminPanel = new Intent(Login.this, ItemListAdmin.class);
                                                        adminPanel.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                        finish();
                                                        startActivity(adminPanel);
                                                    }
                                                    else {
                                                        progressbar.setVisibility(View.GONE);
                                                        Intent employee = new Intent(Login.this, ActiveOrdersEmployee.class);
                                                        employee.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                        finish();
                                                        startActivity(employee);
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
                            }
                        }
                    });
        }
    }
}
