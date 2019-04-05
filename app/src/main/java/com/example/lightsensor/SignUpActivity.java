package com.example.lightsensor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.hbb20.CountryCodePicker;
import com.example.lightsensor.model.User;



import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;



import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class SignUpActivity extends AppCompatActivity {

    EditText firstNameEditText, lastNameEditText, dobEditText, phoneNumberEditText, emailEditText, passwordEditText, confirmPasswordEditText, emNameEditText, emPhoneEditText, emEmailEditText;
    RadioGroup radioGroup;
    Button verifyButton, submitButton;
    CountryCodePicker countryCodePicker;
    RadioButton mRadioButton, fRadioButton, oRadioButton;

    TextView genderTextView;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    User user;


    private String mVerificationId;
    boolean isPhoneVerified = false;

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        firstNameEditText = findViewById(R.id.firstNameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        submitButton = findViewById(R.id.buttonSubmit);

        user = new User();

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String firstName = firstNameEditText.getText().toString().trim();
                String lastName = lastNameEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String confirmPassword = confirmPasswordEditText.getText().toString().trim();


                if(firstName.isEmpty())
                    firstNameEditText.setError("This Field Cannot be Empty");
                else if(lastName.isEmpty())
                    lastNameEditText.setError("This Field Cannot be Empty");
                else if(email.isEmpty())
                    emailEditText.setError("This Field Cannot be Empty");
                else if(password.isEmpty())
                    passwordEditText.setError("This Field Cannot be Empty");
                else if(password.length() < 8 || password.length() > 16)
                    passwordEditText.setError("Password must be 8-16 characters long");
                else if(confirmPassword.isEmpty())
                    confirmPasswordEditText.setError("This Field Cannot be Empty");
                else if(!password.equals(confirmPassword))
                    confirmPasswordEditText.setError("Password mismatch");

                else
                {

                    user.setFirstName(firstName);
                    user.setLastName(lastName);
                    user.setFullName();
                    user.setEmail(email);
                    //user.setActive(false);

                    Toast.makeText(SignUpActivity.this, "user details entered", Toast.LENGTH_SHORT).show();

                    Log.d("FULL NAME :::::::::::::", user.getFullName());
                    Log.d("EMAIL :::::::::::::::::", user.getEmail());


                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful())
                                    {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(":::::::::::::::::::::::", "createUserWithEmail:success");

                                        final FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                        assert firebaseUser != null;
                                        firebaseUser.sendEmailVerification()
                                                .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task task) {

                                                        if (task.isSuccessful()) {

                                                            //Add user to database

                                                            user.setUserId(firebaseUser.getUid());

                                                            mDatabase.child("user").child(user.getUserId()).setValue(user);

                                                            Toast.makeText(SignUpActivity.this, "Verification email sent to " + firebaseUser.getEmail(), Toast.LENGTH_SHORT).show();
                                                            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                                                            startActivity(intent);
                                                            finish();
                                                        } else {
                                                            Log.e(":::::::::::::::::::::::", "sendEmailVerification", task.getException());
                                                            Toast.makeText(SignUpActivity.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }
                                    else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(":::::::::::::::::::::::", "createUserWithEmail:failure", task.getException());
                                        Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
//                                        updateUI(null);
                                    }

                                    // ...
                                }
                            });

                }

            }
        });






    }
}
