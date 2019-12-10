package com.smokinmonkey.bewitched;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
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
import com.google.firebase.firestore.FirebaseFirestore;

public class SignupActivity extends AppCompatActivity {

    private final String TAG = SignupActivity.class.getName();

    // views
    private EditText etName;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private Button btnRegister;
    private TextView tvAlreadyAccount;

    // firebase objects
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private FirebaseFirestore mFirebaseFirestore;

    // string for edit text
    private String mName;
    private String mEmail;
    private String mPassword;
    private String mConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // bind views without butterknife
        etName = (EditText) findViewById(R.id.etUserNameSignup);
        etEmail = (EditText) findViewById(R.id.etUserEmailSignup);
        etPassword = (EditText) findViewById(R.id.etPasswordSignup);
        etConfirmPassword = (EditText) findViewById(R.id.etConfirmPasswordSignup);
        btnRegister = (Button) findViewById(R.id.btnSignup);
        tvAlreadyAccount = (TextView) findViewById(R.id.tvAlreadyAccountSignup);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseFirestore = FirebaseFirestore.getInstance();

        // add on click listeners for buttons
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

        tvAlreadyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alreadyAccount();
            }
        });

    }

    // method for checking and validating input text and register
    public void register() {
        mName = etName.getText().toString().trim();
        mEmail = etEmail.getText().toString().trim();
        mPassword = etPassword.getText().toString().trim();
        mConfirmPassword = etConfirmPassword.getText().toString().trim();
//        String strBirthday = etBirthday.getText().toString().trim();

        // create and show loading bar to let user know it is running

        // checks and validate all input text
        if(!validate(mName, mEmail, mPassword, mConfirmPassword)) {
            onSignupFailed();
            return;
        }

        // disable sign up button
        btnRegister.setEnabled(false);

        // firebase sign up new users with email and password
        mFirebaseAuth.createUserWithEmailAndPassword(mEmail, mPassword).addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Log.d(TAG, "Firebase create user with email and password: SUCCESSFUL!!!");
                    mFirebaseUser = mFirebaseAuth.getInstance().getCurrentUser();
                } else {
                    Log.d(TAG, "Firebase create user with email and password: FAILED!!!", task.getException());
                    Toast.makeText(SignupActivity.this, "Sign up FAILED!", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnRegister.setEnabled(true);
        setResult(RESULT_OK, null);

    }

    // method to validate all edit text fields
    private boolean validate(String name, String email, String password, String confirmPassword) {
        if(name.isEmpty() || name.length() < 3) {
            etName.setError("Please enter your name");
            return false;
        } else if(email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Please enter a valid email address.");
            return false;
        } else if(password.isEmpty() || password.length() < 6 || password.length() > 16) {
            etPassword.setError("Please enter a password between 6 and 16 alphanumeric characters.");
            return false;
        } else if(confirmPassword.isEmpty() || !confirmPassword.matches(password)) {
            etConfirmPassword.setError("Confirm password and password do not match.");
            return false;
        } else {
            etName.setError(null);
            etEmail.setError(null);
            etPassword.setError(null);
            etConfirmPassword.setError(null);
        }
        return true;
    }

    // method to call when sign up failed
    private void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Sign up have failed, fix dispalyed errors and try again...",
                Toast.LENGTH_LONG).show();
        btnRegister.setEnabled(true);
    }

    // on click for going back to login screen
    public void alreadyAccount() {
        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(i);
    }
}
