package com.example.socialad


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.all_post_layout.view.*

class SettingsActivity : AppCompatActivity() {

    private lateinit var settingsUserRef: DatabaseReference;
    private lateinit var mAuth: FirebaseAuth;
    private lateinit var currentUserId : String;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val mToolbar : Toolbar = settings_toolbar as Toolbar
        setSupportActionBar(mToolbar);
        supportActionBar?.title = " AccountSettings";
        supportActionBar?.setDisplayHomeAsUpEnabled(true);

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
    }

}