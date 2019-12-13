package com.smokinmonkey.bewitched;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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

public class LoginActivity extends AppCompatActivity {

    // debug tag
    private final String TAG = LoginActivity.class.getName();
    private final int REQUEST_SIGNUP = 1;
    // user class to store user data

    private EditText etEmail;
    private EditText etPassword;
    private Button btnLogin;
    private TextView tvSignup;

    // firebase auth objects
    FirebaseAuth mFirebaseAuth;
    FirebaseUser mFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // bind views without butterknife
        etEmail = (EditText) findViewById(R.id.etEmailUsername);
        etPassword = (EditText) findViewById(R.id.etUserPasswordLogin);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        tvSignup = (TextView) findViewById(R.id.tvSignupForAccount);

        // set on click listeners
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        tvSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signupActivity();
            }
        });

        // initialize firebase objects
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
    }

    // on click for login button
    public void login() {
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        // validate both email and password
        if(!validate(email, password)) {
            // login failed
            onLoginFailed();
            return;
        }

        // call function to login with email and password
        loginWithEmailPassword(email, password);

    }

    // function to validate email and password
    private boolean validate(String email, String password) {
        // checks if email is not empty or is valid email address
        if(email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Please enter a valid email address.");
            return false;
        } else if(password.isEmpty() || password.length() < 6 || password.length() > 16 ) {
            etPassword.setError("Please enter account password between 6 and 16 alphanumeric characters.");
            return false;
        } else {
            etPassword.setError(null);
            etEmail.setError(null);
        }

        return true;
    }

    // function to call when login is failed
    private void onLoginFailed() {
        Toast.makeText(LoginActivity.this, "Email and password do not match...",
                Toast.LENGTH_LONG).show();
    }

    // login with email and password
    private void loginWithEmailPassword(String email, String password) {
        mFirebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(
                this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            mFirebaseUser = mFirebaseAuth.getInstance().getCurrentUser();

                            if(mFirebaseUser != null) {
                                Toast.makeText(LoginActivity.this, "Log in successful... username: "
                                        + mFirebaseUser.getDisplayName() + " user ID: " + mFirebaseUser.getUid(),
                                        Toast.LENGTH_LONG).show();
                            }

                            // start user into home activity
                            Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
                            startActivity(i);

                        } else {
                            // firebase sign in with email and password failed
                            Toast.makeText(LoginActivity.this, "Firebase authentication FAILED...",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );
    }

    // on click for sign up for an account
    public void signupActivity() {
        Intent i = new Intent(getApplicationContext(), SignupActivity.class);
        startActivity(i);
    }
}
