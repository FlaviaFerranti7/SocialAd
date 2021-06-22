package com.example.socialad

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_setup.*


class SetupActivity : AppCompatActivity() {

    private lateinit var mAuth : FirebaseAuth;
    private lateinit var usersRef : DatabaseReference;
    private lateinit var currentUserId : String;
    private lateinit var loadingBar : ProgressDialog;
    private lateinit var userProfileImageRef : StorageReference;
    private lateinit var filePath : StorageReference;

    private lateinit var listener : ValueEventListener;

    private lateinit var username : String;
    private lateinit var fullname : String;
    private lateinit var city : String;

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
            filePath.putFile(path).addOnCompleteListener { task ->
                if(task.isSuccessful){
                    Toast.makeText(this, "Profile Image correctly stored", Toast.LENGTH_SHORT).show()
                    val downloadUrl = filePath.downloadUrl.addOnSuccessListener {uri ->
                        val u = uri.toString()
                        usersRef.child("profileImage").setValue(u).addOnCompleteListener {
                            if(it.isSuccessful){
                                loadingBar.dismiss();
                            }
                            else{
                                val message = it.exception?.message;
                                Toast.makeText(this, "Error occured$message", Toast.LENGTH_SHORT).show()
                                loadingBar.dismiss();
                            }
                        };
                    }
                }
            }; //save image into firebase storage and database
        }

        setup_save_btn.setOnClickListener {
            SaveAccountSetUpInformation();
        }
        setup_profile_img.setOnClickListener {
            SendUserToChooseAvatarActivity();
        }

        listener = usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot){
                if(dataSnapshot.exists()){
                    if(dataSnapshot.hasChild("profileImage")) {
                        val image = dataSnapshot.child("profileImage").value.toString();
                        Picasso.get().load(image).placeholder(R.drawable.profile_img)
                            .into(setup_profile_img);
                    }
                }
            }
            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }

    private fun SaveAccountSetUpInformation() {
        username = setup_username.text.toString();
        fullname = setup_name.text.toString();
        city = setup_location.text.toString();

        if(TextUtils.isEmpty(username)){
            Toast.makeText(this, "Please write your username", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(fullname)){
            Toast.makeText(this, "Please write your full name", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(city)){
            Toast.makeText(this, "Please write your city", Toast.LENGTH_SHORT).show();
        }
        if (radiogroup.checkedRadioButtonId == -1){
            Toast.makeText(this, "Please select your status", Toast.LENGTH_SHORT).show();
        }
        else{
            loadingBar.setTitle("Saving information");
            loadingBar.setMessage("Please wait while we are saving all your info");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            val radioButton : RadioButton = radiogroup.findViewById(radiogroup.checkedRadioButtonId)
            var status = radioButton.text.toString()

            var userMap = HashMap<String, Any>();
            userMap.put("username", username);
            userMap.put("fullname", fullname);
            userMap.put("country", city);
            userMap.put("status", status);

            usersRef.updateChildren(userMap).addOnCompleteListener {
                if(it.isSuccessful){
                    Toast.makeText(this, "Your account is created successfully", Toast.LENGTH_LONG).show();
                    SendUserToMainActivity();
                    loadingBar.dismiss();
                }
                else{
                    var message = it.exception?.message;
                    Toast.makeText(this, "Error occured: $message", Toast.LENGTH_SHORT).show();
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

    override fun onDestroy() {
        super.onDestroy()
        if(usersRef!=null){
            usersRef.removeEventListener(listener);
        }
    }

}