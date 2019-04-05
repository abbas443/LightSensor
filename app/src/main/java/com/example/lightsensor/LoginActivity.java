package com.example.lightsensor;


import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {


    private Button signUpButton;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button signInButton;
    private static final String TAG = "Sensor";

    private FirebaseAuth firebaseAuth;
    private ConnectivityManager connectivityManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

        editTextEmail = findViewById(R.id.emailField);
        editTextPassword = findViewById(R.id.passwordField);
        signInButton = findViewById(R.id.buttonSignin);
        signUpButton = findViewById(R.id.buttonRegister);
        signInButton.setOnClickListener(this);
        signUpButton.setOnClickListener(this);

        if (firebaseAuth.getCurrentUser() != null) {

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();

        }



        }

    @Override
    public void onClick(View view) {
        if(view == signInButton){
            userLogin();
        }
        if(view == signUpButton){
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
            finish();
        }

    }

    private boolean isValidEmailAndPassword(String email, String password) {
        if(TextUtils.isEmpty(email)) {

            //email is empty
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TextUtils.isEmpty(password)) {
            //password is empty
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void userLogin() {

        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        boolean validDetails = isValidEmailAndPassword(email, password);

        if(validDetails)
        {
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        //start profile activity
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        Log.d(TAG, "onCreate: Initializing Sensor Services" +user);
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();

                       // if (user.isEmailVerified()) {
                       //     Toast.makeText(LoginActivity.this, "Email Verified", Toast.LENGTH_SHORT).show();
                        //    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        //    finish();
                        //}
                        //else {
                        //    Toast.makeText(LoginActivity.this, "Email Not Verified", Toast.LENGTH_SHORT).show();
                        //}
                    }
                    else
                    {
                        Toast.makeText(LoginActivity.this, "Invalid Email/Password", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

    }
}
