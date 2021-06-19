package com.example.socialad

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_find_users.*
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.all_post_layout.view.*


class ProfileActivity : AppCompatActivity() {

    private lateinit var profileUserRef: DatabaseReference;
    private lateinit var mAuth: FirebaseAuth;
    private lateinit var currentUserId : String;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val mToolbar : Toolbar = profile_page_toolbar as Toolbar
        setSupportActionBar(mToolbar);
        supportActionBar?.title = "Profile";
        supportActionBar?.setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.uid!!;
        profileUserRef = FirebaseDatabase.getInstance("https://socialad-78b0e-default-rtdb.firebaseio.com/").reference.child("Users").child(currentUserId)

        profileUserRef.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            @SuppressLint("SetTextI18n")
            override fun onDataChange(snapshot: DataSnapshot) {
                val myProfileImage = snapshot.child("profileImage").value.toString();
                val myFullName= snapshot.child("fullname").value.toString();
                val myUsername= snapshot.child("username").value.toString();
                val myCity = snapshot.child("country").value.toString();
                val myStatus = snapshot.child("status").value.toString();

                Picasso.get().load(myProfileImage).placeholder(R.drawable.profile_img).into(profile_profile_image);
                profile_username.text = "Nickname: $myUsername";
                profile_full_name.text = myFullName;
                profile_city.text = "City: $myCity";
                profile_status.text = "Status: $myStatus";
            }
        })

        profile_post_list.setHasFixedSize(true);
        val linearLayoutManager : LinearLayoutManager = LinearLayoutManager(this);
        linearLayoutManager.reverseLayout = true;
        linearLayoutManager.stackFromEnd = true;
        profile_post_list.layoutManager = linearLayoutManager;

        DisplayAllUserPost();


    }

    private fun DisplayAllUserPost() {

        FirebaseDatabase.getInstance("https://socialad-78b0e-default-rtdb.firebaseio.com/").reference.child("Posts")
                .orderByChild("uid").equalTo(currentUserId).addValueEventListener(object : ValueEventListener{
                    override fun onCancelled(error: DatabaseError) {
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        val temp: MutableList<Posts> = ArrayList()
                        for( ds in snapshot.children) {
                            val post: Posts? = ds.getValue(Posts::class.java);
                            if (post != null) {
                                temp.add(post);
                            }
                        }
                        val postsAdapter = PostsAdapter(temp)
                        profile_post_list.adapter = postsAdapter;
                    }
                })

    }

}