package com.smokinmonkey.bewitched.classes;

import android.net.Uri;

public class User {
    private String mUserID;
    private String mUserName;
    private String mUserEmail;
    private String mUserPassword;
    private String mUserBirthday;
    private String mUserAge;
    private Uri mUserProfilePhoto;
//    private Uri[] mUserPhotos;

    // getters
    public String getUserID() { return this.mUserID; }
    public String getUserName() { return this.mUserName; }
    public String getUserEmail() { return this.mUserEmail; }
    public String getUserPassword() { return this.mUserPassword; }
    public String getUserBirthday() { return this.mUserBirthday; }
    public String getUserAge() { return this.mUserAge; }
    public Uri getUserProfilePhoto() { return this.mUserProfilePhoto; }
//    public Uri[] getUserPhotos() { return this.mUserPhotos; }

    // setters
    public void setUserID(String userID) { this.mUserID = userID; }
    public void setUserName(String userName) { this.mUserName = userName; }
    public void setUserEmail(String userEmail) { this.mUserEmail = userEmail; }
    public void setUserPassword(String userPassword) { this.mUserPassword = userPassword; }
    public void setUserBirthday(String userBirthday) { this.mUserBirthday = userBirthday; }
    public void setUserAge(String age) { this.mUserAge = age; }
    public void setUserProfilePhoto(Uri userProfilePhoto) { this.mUserProfilePhoto = userProfilePhoto; }
//    public void setUserPhotos(Uri[] userPhotos) {
//        for(int i = 0; i <= userPhotos.length; i++) {
//            this.mUserPhotos[i] = userPhotos[i];
//        }
//    }
}
