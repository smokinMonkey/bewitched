package com.smokinmonkey.bewitched;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smokinmonkey.bewitched.classes.User;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;

public class ProfileActivity extends AppCompatActivity {

    private final String TAG = ProfileActivity.class.getName();
    // views
    private EditText etUserName;
    private EditText etUserEmail;
    private EditText etUserPassword;
    private EditText etUserBirthday;
    private TextView tvUserAge;
    private Button btnSaveProfile;

    // firebase objects
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDbRef;
    private DatabaseReference userIdRef;

    private User mUser;

    final Calendar userCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // bind views without butterknife
        etUserName      = (EditText) findViewById(R.id.etUserNameProfile);
        etUserEmail     = (EditText) findViewById(R.id.etUserEmailProfile);
        etUserPassword  = (EditText) findViewById(R.id.etUserPasswordProfile);
        etUserBirthday  = (EditText) findViewById(R.id.etUserBirthdayProfile);
        tvUserAge       = (TextView) findViewById(R.id.tvUserAgeProfile);
        btnSaveProfile  = (Button) findViewById(R.id.btnSaveProfile);

        // initialize firebase objects
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDbRef = mFirebaseDatabase.getReference();

        mUser = new User();

        etUserName.setInputType(0);
        etUserEmail.setInputType(0);
        etUserPassword.setInputType(0);
        etUserBirthday.setInputType(0);

        // get user information from database
//        DatabaseReference usersRef = mDbRef.child("users");
        userIdRef = (DatabaseReference) FirebaseDatabase.getInstance()
                .getReference().child("users").child(mFirebaseUser.getUid());
        ValueEventListener vel = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUser = dataSnapshot.getValue(User.class);
                Log.d(TAG, "mUser id: " + mUser.getUserID());

                etUserName.setText(mUser.getUserName());
                etUserEmail.setText(mUser.getUserEmail());
                etUserPassword.setText(mUser.getUserPassword());

                if(mUser.getUserBirthday() != null  && mUser.getUserAge() != null) {
                    etUserBirthday.setText(mUser.getUserBirthday());
                    tvUserAge.setText(mUser.getUserAge());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        };

        userIdRef.addListenerForSingleValueEvent(vel);

        // user birthday input setup
        final DatePickerDialog.OnDateSetListener userBirthday = new DatePickerDialog.OnDateSetListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                userCalendar.set(Calendar.YEAR, year);
                userCalendar.set(Calendar.MONTH, month);
                userCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };
        // user birthday set on click listener
        etUserBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(ProfileActivity.this, userBirthday, userCalendar.get(Calendar.YEAR),
                userCalendar.get(Calendar.MONTH), userCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        // save user profile button set on click listener
        btnSaveProfile.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                updateUserProfile();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void updateLabel() {
        String dateFormat = "MM/dd/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        String birthdate = sdf.format(userCalendar.getTime());
        etUserBirthday.setText(birthdate);
        mUser.setUserBirthday(birthdate);
        calculateUserAge();
    }

    // method to calculate user age
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void calculateUserAge() {
        LocalDate today = LocalDate.now();
        LocalDate birthdate = LocalDate.of(userCalendar.get(Calendar.YEAR),
                userCalendar.get(Calendar.MONTH), userCalendar.get(Calendar.DAY_OF_MONTH));

        Period p = Period.between(birthdate, today);
        mUser.setUserAge(Integer.toString(p.getYears()));

        tvUserAge.setText(mUser.getUserAge());
    }

    // method to update users data into database
    public void updateUserProfile() {
        userIdRef.setValue(mUser);
    }
}
