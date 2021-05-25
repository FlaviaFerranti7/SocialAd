package com.example.socialad

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_post.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class PostActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth;
    private lateinit var currentUserId: String;
    private lateinit var usersRef: DatabaseReference;
    private lateinit var postsRef: DatabaseReference;

    private lateinit var loadingBar : ProgressDialog;

    private lateinit var saveCurrentTime: String;
    private lateinit var saveCurrentDate: String;
    private lateinit var postname: String;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        val mToolbar : Toolbar = update_post_toolbar as Toolbar
        setSupportActionBar(mToolbar);
        supportActionBar?.title = "Add post";
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);

        loadingBar = ProgressDialog(this)

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.currentUser!!.uid;
        usersRef = FirebaseDatabase.getInstance("https://socialad-78b0e-default-rtdb.firebaseio.com/").reference.child("Users");
        postsRef = FirebaseDatabase.getInstance("https://socialad-78b0e-default-rtdb.firebaseio.com/").reference.child("Posts");

        saveCurrentTime = SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().time)
        saveCurrentDate = SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().time)
        postname = saveCurrentDate + saveCurrentTime

        add_post_btn.setOnClickListener {
            ValidatePost();
        }
    }

    private fun ValidatePost() {
        val description : String = post_text.text.toString();
        if(TextUtils.isEmpty(description)){
            Toast.makeText(this, "Please write something before posting ", Toast.LENGTH_SHORT).show();
        }
        else{
            loadingBar.setTitle("Saving post");
            loadingBar.setMessage("Please wait while we are saving your new post");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            StoringPostToFirebaseDatabase(description);
        }
    }

    private fun StoringPostToFirebaseDatabase(d: String) {
        usersRef.child(currentUserId).addValueEventListener(object : ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    val userFullName : String = p0.child("fullname").value.toString();
                    val userProfileImage : String = p0.child("profileImage").value.toString();

                    var postMap = HashMap<String, Any>();
                    postMap.put("uid", currentUserId);
                    postMap.put("date", saveCurrentDate);
                    postMap.put("time", saveCurrentTime);
                    postMap.put("fullname", userFullName);
                    postMap.put("profileImage", userProfileImage);
                    postMap.put("description", d);

                    postsRef.child(currentUserId + postname).updateChildren(postMap).addOnCompleteListener {
                      if(it.isSuccessful){
                          SendUserToMainActivity();
                          Toast.makeText(this@PostActivity, "New post created successfully  ", Toast.LENGTH_SHORT).show();
                          loadingBar.dismiss();
                      }
                      else{
                          Toast.makeText(this@PostActivity, "Error occurred while creating post", Toast.LENGTH_SHORT).show();
                          loadingBar.dismiss();
                      }
                    };
                }
            }
            override fun onCancelled(p0: DatabaseError) {
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