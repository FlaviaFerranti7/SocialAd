package com.example.socialad


import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.activity_setup.*
import kotlinx.android.synthetic.main.all_post_layout.view.*

class SettingsActivity : AppCompatActivity() {

    private lateinit var settingsUserRef: DatabaseReference;
    private lateinit var mAuth: FirebaseAuth;
    private lateinit var currentUserId : String;

    private lateinit var loadingBar : ProgressDialog;

    private lateinit var userProfileImageRef : StorageReference;
    private lateinit var filePath : StorageReference;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val mToolbar : Toolbar = settings_toolbar as Toolbar
        setSupportActionBar(mToolbar);
        supportActionBar?.title = " Account Settings";
        supportActionBar?.setDisplayHomeAsUpEnabled(true);

        loadingBar = ProgressDialog(this)

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.uid!!;
        settingsUserRef = FirebaseDatabase.getInstance("https://socialad-78b0e-default-rtdb.firebaseio.com/").reference.child("Users").child(currentUserId)

        settingsUserRef.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val myProfileImage = snapshot.child("profileImage").value.toString();
                val myFullName= snapshot.child("fullname").value.toString();
                val myUsername= snapshot.child("username").value.toString();
                val myCity = snapshot.child("country").value.toString();
                val myStatus = snapshot.child("status").value.toString();

                Picasso.get().load(myProfileImage).placeholder(R.drawable.profile_img).into(settings_pic);
                settings_username.setText(myUsername);
                settings_fullname.setText(myFullName);
                settings_country.setText(myCity);
                settings_status.setText(myStatus);
            }
        })

        val extras = intent.extras
        if (extras != null) {                               //avatar has been changed

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
                        settingsUserRef.child("profileImage").setValue(u).addOnCompleteListener {
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
                }
            }; //save image into firebase storage and database
        }

        settings_update.setOnClickListener {
            ValidateAccountInfo();
        }

        settings_pic.setOnClickListener {
            SendUserToChooseAvatarActivity();
        }

        settingsUserRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot){
                if(dataSnapshot.exists()){
                    if(dataSnapshot.hasChild("profileImage")) {
                        val image = dataSnapshot.child("profileImage").value.toString();
                        Picasso.get().load(image).placeholder(R.drawable.profile_img)
                            .into(settings_pic);
                    }
                }
            }
            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }

    private fun ValidateAccountInfo() {

        val fullName= settings_fullname.text.toString();
        val username= settings_username.text.toString();
        val city = settings_country.text.toString();
        val status = settings_status.text.toString();

        if(TextUtils.isEmpty(fullName)){
            Toast.makeText(this, "Please write your profile name", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(username)){
            Toast.makeText(this, "Please write your username", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(city)){
            Toast.makeText(this, "Please write your city", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(status)){
            Toast.makeText(this, "Please write your status", Toast.LENGTH_SHORT).show();
        }
        else{
            UpdateAccountInfo(fullName, username, city, status)
        }


    }

    private fun UpdateAccountInfo(fullName: String, username: String, city: String, status: String) {

        var userMap = HashMap<String, Any>();
        userMap.put("fullname", fullName);
        userMap.put("username", username);
        userMap.put("country", city);
        userMap.put("status", status);

        settingsUserRef.updateChildren(userMap).addOnCompleteListener {
            if(it.isSuccessful){
                SendUserToMainActivity();
                Toast.makeText(this, "Account information updated successfully", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this, "An error occurred, please try again", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private fun SendUserToMainActivity() {
        val mainIntent = Intent(this, MainActivity::class.java);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish()
    }

    private fun SendUserToChooseAvatarActivity() {
        val avatarIntent = Intent(this, ChooseAvatarActivity::class.java);
        avatarIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK);
        avatarIntent.putExtra("Activity_Name","A");
        startActivity(avatarIntent);
        finish();
    }

}