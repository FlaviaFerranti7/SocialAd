package com.example.socialad

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_click_post.*

class ClickPostActivity : AppCompatActivity() {

    private lateinit var clickPostRef : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_click_post)

        var bundle :Bundle ?=intent.extras
        var postKey = bundle!!.getString("PostKey").toString();

        clickPostRef = FirebaseDatabase.getInstance("https://socialad-78b0e-default-rtdb.firebaseio.com/").reference.child("Posts").child(postKey);
        clickPostRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var description = snapshot.child("description").value.toString()
                click_post_description.text = description;
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}