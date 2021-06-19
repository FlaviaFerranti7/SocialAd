package com.example.socialad

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile.*


class ProfileActivity : AppCompatActivity() {

    private lateinit var profileUserRef: DatabaseReference;
    private lateinit var mAuth: FirebaseAuth;
    private lateinit var user : String;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val mToolbar : Toolbar = profile_page_toolbar as Toolbar
        setSupportActionBar(mToolbar);
        supportActionBar?.title = "Profile";
        supportActionBar?.setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();

        val extras = intent.extras
        if(extras!=null){
            user = extras.getString("user").toString();
        }
        else{
            user = mAuth.uid!!
        }

        profileUserRef = FirebaseDatabase.getInstance("https://socialad-78b0e-default-rtdb.firebaseio.com/").reference.child("Users").child(user)

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
                .orderByChild("uid").equalTo(user).addValueEventListener(object : ValueEventListener{
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