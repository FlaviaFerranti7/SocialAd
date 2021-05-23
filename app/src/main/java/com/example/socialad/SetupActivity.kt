package com.example.socialad

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_setup.*

class SetupActivity : AppCompatActivity() {

    private lateinit var mAuth : FirebaseAuth;
    private lateinit var usersRef : DatabaseReference;
    private lateinit var currentUserId : String;
    private lateinit var loadingBar : ProgressDialog;
    private lateinit var userProfileImageRef : StorageReference;
    private lateinit var filePath : StorageReference;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup)

        Utils.setupUI(setup_layout,this);

        loadingBar = ProgressDialog(this)

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.currentUser?.uid.toString();
        usersRef = FirebaseDatabase.getInstance("https://socialad-78b0e-default-rtdb.firebaseio.com/").reference.child("Users").child(currentUserId);

        val extras = intent.extras
        if (extras != null) {                               //avatar has been created

            val value = extras.getString("photo")

            loadingBar.setTitle("Saving information");
            loadingBar.setMessage("Please wait while we are saving your avatar");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            userProfileImageRef = FirebaseStorage.getInstance().reference.child("profile Images");
            filePath = userProfileImageRef.child(currentUserId + ".jpg");

            val path = Uri.parse("android.resource://" + BuildConfig.APPLICATION_ID + "/drawable/a"+value);
            filePath.putFile(path).addOnCompleteListener {
                if(it.isSuccessful){
                    Toast.makeText(this, "Profile Image correctly stored", Toast.LENGTH_SHORT).show()
                    val downloadUrl = it.result.toString();
                    usersRef.child("profileImage").setValue(downloadUrl).addOnCompleteListener {
                        if(it.isSuccessful){
                            Toast.makeText(this, "Profile Image correctly stored to firebase database", Toast.LENGTH_SHORT).show()
                            loadingBar.dismiss();
                        }
                        else{
                            val message = it.exception?.message;
                            Toast.makeText(this, "Error occured$message", Toast.LENGTH_SHORT).show()
                            loadingBar.dismiss();
                        }
                    };
                }
            }; //save image into firebase storage and database
        }

        setup_save_btn.setOnClickListener {
            SaveAccountSetUpInformation();
        }
        setup_profile_img.setOnClickListener {
            SendUserToChooseAvatarActivity();
        }
    }

    private fun SaveAccountSetUpInformation() {
        var username : String = setup_username.text.toString();
        var fullname : String = setup_name.text.toString();
        var city : String = setup_location.text.toString();

        if(TextUtils.isEmpty(username)){
            Toast.makeText(this, "Please write your username", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(fullname)){
            Toast.makeText(this, "Please write your full name", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(city)){
            Toast.makeText(this, "Please write your city", Toast.LENGTH_SHORT).show();
        }
        else{
            loadingBar.setTitle("Saving information");
            loadingBar.setMessage("Please wait while we are saving all your info");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            var userMap = HashMap<String, Any>();
            userMap.put("username", username);
            userMap.put("fullname", fullname);
            userMap.put("country", city);
            userMap.put("status", "alunno");

            usersRef.updateChildren(userMap).addOnCompleteListener {
                if(it.isSuccessful){
                    Toast.makeText(this, "Your account is created successfully", Toast.LENGTH_LONG).show();
                    SendUserToMainActivity();
                    loadingBar.dismiss();
                }
                else{
                    var message = it.exception?.message;
                    Toast.makeText(this, "Error occured: " + message, Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }
        }
    }

    private fun SendUserToMainActivity() {
        val mainIntent = Intent(this, MainActivity::class.java);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
    private fun SendUserToChooseAvatarActivity() {
        val avatarIntent = Intent(this, ChooseAvatarActivity::class.java);
        avatarIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(avatarIntent);
        finish();
    }
}