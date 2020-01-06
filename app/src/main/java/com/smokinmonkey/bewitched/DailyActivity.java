package com.smokinmonkey.bewitched;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.smokinmonkey.bewitched.classes.Post;
import com.smokinmonkey.bewitched.classes.User;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DailyActivity extends AppCompatActivity {

    private final String TAG = DailyActivity.class.getName();
    private final int GALLERY_REQUEST_CODE = 2001;
    // views
    private ImageButton imgbtnDailyAddImage;
    private EditText etDailyActivityTitle;
    private EditText etDailyActivityDescription;
    private Button btnDailyActivityPost;

    // firebase objects
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mStorageReference;
    private DatabaseReference mUserIdRef;

    private User mUser;
    private Post mPost;

    private Uri mPostImageUri;
    private Bitmap mBitmap;

    private Date d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily);

        // bind views without butterknife
        imgbtnDailyAddImage = (ImageButton) findViewById(R.id.imgbtnDailyActivity);
        etDailyActivityTitle = (EditText) findViewById(R.id.etDailyActivityTitle);
        etDailyActivityDescription = (EditText) findViewById(R.id.etDailyActivityDescription);
        btnDailyActivityPost = (Button) findViewById(R.id.btnDailyActivityPost);

        // initialize firebase objects
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        // storage for multi media file
        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorage.getReference();

        mUser = new User();
        mPost = new Post();

        d = new Date();

        imgbtnDailyAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addImageDailyActivity();
            }
        });

        btnDailyActivityPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postDailyActivity();
            }
        });

        // get user information from database
        mUserIdRef = (DatabaseReference) FirebaseDatabase.getInstance().getReference()
                .child("users").child(mFirebaseUser.getUid());
        ValueEventListener vel = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUser = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        };

        mUserIdRef.addListenerForSingleValueEvent(vel);
    }

    // start image picker intent
    private void addImageDailyActivity(){
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
    }

    // intent return result, validate everything
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mPostImageUri = data.getData();
            try {
                mBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mPostImageUri);
                imgbtnDailyAddImage.setImageBitmap(mBitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // method to post blog into database
    private void postDailyActivity() {
        Toast.makeText(DailyActivity.this, "Post button clicked...", Toast.LENGTH_LONG).show();
        final String postTitle = etDailyActivityTitle.getText().toString().trim();
        final String postDesc = etDailyActivityDescription.getText().toString().trim();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String date = sdf.format(d.getTime());

        if(!postTitle.isEmpty() && !postDesc.isEmpty()) {
            // method to upload image
            uploadImage();

            // set post data to post class
            mPost.setUserID(mUser.getUserID());
            mPost.setUserName(mUser.getUserName());
            mPost.setPostTitle(postTitle);
            mPost.setPostDescription(postDesc);
            mPost.setPostDate(date);

        }

        DatabaseReference mDailyPostRef = mFirebaseDatabase.getReference().child("posts").child(date);
        mDailyPostRef.child(mUser.getUserID()).setValue(mPost);
    }

    // method to upload image
    private void uploadImage() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String date = sdf.format(d.getTime());

        StorageReference imgRef = mStorageReference.child("images/" + date + "/"
                + mFirebaseUser.getUid() + "/" + mPostImageUri.getLastPathSegment());

        mPost.setPostImageURI(imgRef.getDownloadUrl().toString());

        UploadTask ut = imgRef.putFile(mPostImageUri);
        ut.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(DailyActivity.this, "Image uploaded..."
                        + mPostImageUri, Toast.LENGTH_LONG).show();
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DailyActivity.this, "Image upload FAILED..."
                        + mPostImageUri, Toast.LENGTH_LONG).show();
            }
        })
        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {

            }
        });
    }

}
