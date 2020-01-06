package com.smokinmonkey.bewitched.classes;

import android.net.Uri;

import com.google.android.gms.tasks.Task;

public class Post {

    private String mUserID;
    private String mUserName;
    private String mPostDate;
    private String mPostImageURI;
    private String mPostTitle;
    private String mPostDescription;
    private int mNumberOfLikes;
    private int mNumberOfComments;

    // getters
    public String getUserID() { return this.mUserID; }
    public String getUserName() { return this.mUserName; }
    public String getPostDate() { return this.mPostDate; }
    public String getPostImageURI() { return this.mPostImageURI; }
    public String getPostTitle() { return this.mPostTitle; }
    public String getPostDescription() { return this.mPostDescription; }
    public int getNumberOfLikes() { return this.mNumberOfLikes; }
    public int getNumberOfComments() { return this.mNumberOfComments; }

    // setters
    public void setUserID(String userID) { this.mUserID = userID; }
    public void setUserName(String userName) { this.mUserName = userName; }
    public void setPostDate(String postDate) { this.mPostDate = postDate; }

    public void setPostImageURI(String postImageURI) { this.mPostImageURI = postImageURI; }

    public void setPostTitle(String postTitle) { this.mPostTitle = postTitle; }
    public void setPostDescription(String postDescription) { this.mPostDescription = postDescription; }
    public void setNumberOfLikes(int numOfLikes) { this.mNumberOfLikes = numOfLikes; }
    public void setmNumberOfComments(int numOfComments) { this.mNumberOfComments = numOfComments; }

}
