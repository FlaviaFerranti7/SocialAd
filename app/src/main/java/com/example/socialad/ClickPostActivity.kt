package com.example.socialad

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_click_post.*
import kotlinx.android.synthetic.main.activity_post.*
import kotlinx.android.synthetic.main.all_post_layout.view.*

class ClickPostActivity : AppCompatActivity() {

    private lateinit var clickPostRef : DatabaseReference;
    private lateinit var mAuth: FirebaseAuth;
    private lateinit var currentUserId: String;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_click_post)

        var bundle :Bundle ?=intent.extras
        var postKey = bundle!!.getString("PostKey").toString();

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.currentUser!!.uid;

        val mToolbar : Toolbar = click_post_toolbar as Toolbar
        setSupportActionBar(mToolbar);
        supportActionBar?.title = "Post";
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);

        click_delete.visibility = View.INVISIBLE
        click_edit.visibility = View.INVISIBLE

        clickPostRef = FirebaseDatabase.getInstance("https://socialad-78b0e-default-rtdb.firebaseio.com/").reference.child("Posts").child(postKey);
        clickPostRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var profileImage = snapshot.child("profileImage").value.toString()
                var username = snapshot.child("fullname").value.toString()
                var date = snapshot.child("date").value.toString()
                var time = snapshot.child("time").value.toString()
                var description = snapshot.child("description").value.toString()
                var databaseUserId = snapshot.child("uid").value.toString()

                Picasso.get().load(profileImage).placeholder(R.drawable.profile_img).into(click_post_profile_image);
                click_post_user_name.text = username;
                click_post_date.text = "   $date";
                click_post_time.text = "  -  $time";
                click_post_description.text = description;

                if(currentUserId.equals(databaseUserId)){
                    click_delete.visibility = View.VISIBLE
                    click_edit.visibility = View.VISIBLE
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId;
        if (id == android.R.id.home){
            SendUserToMainActivity();
        }
        return super.onOptionsItemSelected(item)
    }

    private fun SendUserToMainActivity() {
        val mainIntent = Intent(this, MainActivity::class.java);
        startActivity(mainIntent);
    }
}