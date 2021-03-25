package com.example.socialad

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.INotificationSideChannel
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_setup.*

class SetupActivity : AppCompatActivity() {

    private lateinit var mAuth : FirebaseAuth;
    private lateinit var usersRef : DatabaseReference;
    private lateinit var currentUserId : String;
    private lateinit var loadingBar : ProgressDialog;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup)

        Utils.setupUI(setup_layout,this);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.currentUser?.uid.toString();
        usersRef = FirebaseDatabase.getInstance("https://socialad-78b0e-default-rtdb.firebaseio.com/").reference.child("Users").child(currentUserId);

        loadingBar = ProgressDialog(this)

        setup_save_btn.setOnClickListener {
            SaveAccountSetUpInformation();
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
}