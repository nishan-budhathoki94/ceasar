package com.mitfinalproject.ceasar.Admin;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import com.mitfinalproject.ceasar.Data.Constants;
import com.mitfinalproject.ceasar.Login;
import com.mitfinalproject.ceasar.R;
import com.mitfinalproject.ceasar.VolleySingleton;

import java.util.HashMap;
import java.util.Map;

public class SignUpEmployee extends AppCompatActivity {

    private TextInputLayout textInputEmail,textInputPassword,textInputConfirmEmail,textInputPhone,textInputName;
    private String name,phone,email,password;
    ProgressBar progressbar;
    private FirebaseAuth mAuth;
    private long backPressed;
    private Toast backToast;
    private String server_url = "http://everestelectricals.com.au/ceasar/signup.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        textInputEmail = findViewById(R.id.textInputLayoutSignUpEmail);
        textInputPassword = findViewById(R.id.textInputLayoutSignUpPassword);
        textInputConfirmEmail = findViewById(R.id.textInputLayoutConfirmEmail);
        textInputPhone = findViewById(R.id.textInputLayoutPhone);
        textInputName = findViewById(R.id.textInputLayoutName);
        progressbar = findViewById(R.id.progressBarSignUp);
        TextView textViewTitle = findViewById(R.id.textViewSignUpTitle);
        textViewTitle.setText("Please enter your employee details to create account");

    }

    //call php file from the server to create new user
    public void signUpBtnClick(View v) {
        if(validatePhone()  && validatePassword() && validateConfirmEmail() && validateEmail()  &&  validateName()  )  {
            progressbar.setVisibility(View.VISIBLE);
            name = textInputName.getEditText().getText().toString().trim();
            email = textInputEmail.getEditText().getText().toString().trim();
            password = textInputPassword.getEditText().getText().toString().trim();
            phone = textInputPhone.getEditText().getText().toString().trim();
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("Firebase", "createUserWithEmail:success");
                                mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(SignUpEmployee.this, "Registration Successful, Please check your email for verification", Toast.LENGTH_LONG).show();
                                            //Inject Sql with the data using post method
                                            StringRequest request = new StringRequest(Request.Method.POST, server_url,
                                                    new Response.Listener<String>() {
                                                        @Override
                                                        public void onResponse(String response) {
                                                            progressbar.setVisibility(View.GONE);
                                                            Toast.makeText(getApplicationContext(), "Employee Account Created", Toast.LENGTH_SHORT).show();
                                                            textInputName.getEditText().setText("");
                                                            textInputEmail.getEditText().setText("");
                                                            textInputConfirmEmail.getEditText().setText("");
                                                            textInputPassword.getEditText().setText("");
                                                            textInputPhone.getEditText().setText("");
                                                        }
                                                    },

                                                    new Response.ErrorListener() {
                                                        @Override
                                                        public void onErrorResponse(VolleyError error) {
                                                            progressbar.setVisibility(View.INVISIBLE);
                                                            Toast.makeText(getApplicationContext(), "Error..."+error.toString(), Toast.LENGTH_SHORT).show();
                                                            error.printStackTrace();

                                                        }
                                                    }) {

                                                @Override
                                                protected Map<String, String> getParams() throws AuthFailureError {
                                                    Map<String, String> params = new HashMap<String, String>();
                                                    params.put("email", email);
                                                    params.put("type", Constants.TYPE_EMPLOYEE.toLowerCase());
                                                    params.put("phone",phone);
                                                    params.put("name",name);
                                                    return params;
                                                }
                                            };

                                            VolleySingleton.getInstance(SignUpEmployee.this).addToRequestQueue(request);
                                        }
                                        else {
                                            progressbar.setVisibility(View.INVISIBLE);
                                            Toast.makeText(SignUpEmployee.this, "Something went wrong please check your email address again", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });

                            } else {
                                progressbar.setVisibility(View.INVISIBLE);
                                // If sign in fails, display a message to the user.
                                Log.w("Firebase", "createUserWithEmail:failure", task.getException());
                                Toast.makeText(SignUpEmployee.this, "Email "+email+" is already registered.",
                                        Toast.LENGTH_LONG).show();
                                textInputName.getEditText().setText("");
                                textInputEmail.getEditText().setText("");
                                textInputConfirmEmail.getEditText().setText("");
                                textInputPassword.getEditText().setText("");
                                textInputPhone.getEditText().setText("");
                            }
                        }
                    });


        }
    }

    @Override
    public void onBackPressed() {
        if (backPressed + 2000 > System.currentTimeMillis()){
            backToast.cancel();
            super.onBackPressed();
            return;
        }
        else {
            backToast= Toast.makeText(getBaseContext(),"Press back again to exit",Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPressed = System.currentTimeMillis();
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

    //validate email
    public boolean validateConfirmEmail() {
        String confirmEmailInput = textInputConfirmEmail.getEditText().getText().toString().trim();
        String emailInput = textInputEmail.getEditText().getText().toString().trim();

        if(confirmEmailInput.isEmpty()) {
            textInputConfirmEmail.setError("Email cannot be empty");
            progressbar.setVisibility(View.INVISIBLE);
            return false;
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(confirmEmailInput).matches()) {
            textInputConfirmEmail.setError("Invalid Email Address");
            progressbar.setVisibility(View.INVISIBLE);
            return false;
        }
        else if (!emailInput.equals(confirmEmailInput)) {
            textInputConfirmEmail.setError("Email mismatched");
            progressbar.setVisibility(View.INVISIBLE);
            return false;
        }
        else {
            textInputConfirmEmail.setError(null);
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
//        else if (!PASSWORD_PATTERN.matcher(passwordInput).matches()) {
//            textInputPassword.setError("Password too weak");
//            return false;
//        }
        else {
            textInputPassword.setError(null);
            return true;
        }
    }

    //validate phone
    public boolean validatePhone() {
        String phoneInput = textInputPhone.getEditText().getText().toString().trim();

        if(phoneInput.isEmpty()) {
            textInputPhone.setError("Phone Field cannot be empty");
            progressbar.setVisibility(View.INVISIBLE);
            return false;
        }
        else {
            textInputPhone.setError(null);
            return true;
        }
    }

    //validate phone
    public boolean validateName() {
        String nameInput = textInputName.getEditText().getText().toString().trim();

        if(nameInput.isEmpty()) {
            textInputPhone.setError("Name cannot be empty");
            progressbar.setVisibility(View.INVISIBLE);
            return false;
        }
        else {
            textInputPhone.setError(null);
            return true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_admin_panel,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_logout:
                FirebaseAuth.getInstance().signOut();
                Intent intentLogout = new Intent(this, Login.class);
                intentLogout.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intentLogout);
                finish();
                break;

            case R.id.set_delivery_zone:
                Intent intentDeliverZone = new Intent(this, DeliveryZone.class);
                intentDeliverZone.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intentDeliverZone);
                finish();
                break;

            case R.id.viewMenuItems:
                Intent intentViewItems = new Intent(this, ItemListAdmin.class);
                intentViewItems.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intentViewItems);
                finish();
                break;
            case R.id.action_addMenuItem:
                Intent intent = new Intent(this, AddMenuItemAdmin.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                break;
            case R.id.createEmployeeAccount:
                Toast.makeText(getApplicationContext(), "You are already in the same menu", Toast.LENGTH_SHORT).show();
                break;

            case R.id.view_orders:
                Intent viewOrders = new Intent(this, OrderHistoryAdmin.class);
                viewOrders.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(viewOrders);
                finish();
                break;
        }
        return true;
    }
}
