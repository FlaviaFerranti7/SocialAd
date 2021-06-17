package com.example.socialad

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.all_post_layout.view.*
import kotlinx.android.synthetic.main.navigation_header.*
import java.security.AccessController.getContext
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {

    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle;
    private lateinit var mAuth: FirebaseAuth;
    private lateinit var usersRef: DatabaseReference;
    private lateinit var currentUserId: String;
    private lateinit var listener1 : ValueEventListener;
    private lateinit var listener2 : ValueEventListener;

    @SuppressLint("RestrictedApi")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.currentUser!!.uid;
        usersRef = FirebaseDatabase.getInstance("https://socialad-78b0e-default-rtdb.firebaseio.com/").reference.child("Users");

        val mToolbar : Toolbar = main_page_toolbar as Toolbar
        setSupportActionBar(mToolbar);
        supportActionBar?.title = "Home";

        val headerView: View = navigation_view.inflateHeaderView(R.layout.navigation_header);
        actionBarDrawerToggle = ActionBarDrawerToggle(this, main_layout, R.string.drawer_open, R.string.drawer_close);
        main_layout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        supportActionBar?.setDisplayHomeAsUpEnabled(true);

        all_users_post_list.setHasFixedSize(true);
        val linearLayoutManager : LinearLayoutManager = LinearLayoutManager(this);
        linearLayoutManager.reverseLayout = true;
        linearLayoutManager.stackFromEnd = true;
        all_users_post_list.layoutManager = linearLayoutManager;

        navigation_view.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.nav_profile -> {
                    Toast.makeText(this, "PROFILE", Toast.LENGTH_SHORT).show();
                    true
                }
                R.id.nav_home -> {
                    Toast.makeText(this, "HOME", Toast.LENGTH_SHORT).show();
                    true
                }
                R.id.nav_search -> {
                    Toast.makeText(this, "SEARCH", Toast.LENGTH_SHORT).show();
                    true
                }
                R.id.nav_messages -> {
                    Toast.makeText(this, "MESSAGE", Toast.LENGTH_SHORT).show();
                    true
                }
                R.id.nav_settings -> {
                    SendUserToSettingsActivity()
                    true
                }
                R.id.nav_logout -> {
                    usersRef.child(currentUserId).removeEventListener(listener2);
                    usersRef.removeEventListener(listener1);
                    mAuth.signOut();
                    SendUserToLoginActivity();
                    true
                }
                R.id.nav_post -> {
                    SendUserToPostActivity();
                    true
                }
                else -> false
            }
        }

        main_add_post.setOnClickListener {
                SendUserToPostActivity();
        }

        DisplayAllUsersPost();

    }

    private fun DisplayAllUsersPost() {
        var postsQuery = FirebaseDatabase.getInstance("https://socialad-78b0e-default-rtdb.firebaseio.com/")
                .reference
                .child("Posts")
                .limitToLast(50)
        val options = FirebaseRecyclerOptions.Builder<Posts>()
                .setQuery(postsQuery,Posts::class.java)
                .setLifecycleOwner(this)
                .build()

        val firebaseRecyclerAdapter = object : FirebaseRecyclerAdapter<Posts, PostViewHolder>(options){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
                return PostViewHolder(LayoutInflater.from(parent.context)
                        .inflate(R.layout.all_post_layout, parent, false))
            }

            override fun onBindViewHolder(holder: PostViewHolder, position: Int, model: Posts) {
                    holder.bind(model)
                    holder.itemView.setOnClickListener { v ->
                        val intent = Intent(v.context, ClickPostActivity::class.java)
                        intent.putExtra("PostKey", getRef(position).key)
                        v.context.startActivity(intent)

                    }
            }

        }

        all_users_post_list.adapter = firebaseRecyclerAdapter;

    }

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        @SuppressLint("SetTextI18n")
        fun bind(post: Posts) {
            with(post) {
                itemView.post_description?.text = description;
                itemView.post_date?.text = "   $date"
                itemView.post_time?.text = "  -  $time"
                itemView.post_user_name?.text = fullname
                Picasso.get().load(profileImage).placeholder(R.drawable.profile_img)
                        .into(itemView.post_profile_image);
            }
        }

    }

    override fun onStart() {
        super.onStart()
        var currentUser : FirebaseUser? = mAuth.currentUser;
        if (currentUser == null){
            SendUserToLoginActivity();
        }
        else{
            CheckUserExistance();
        }
    }

    private fun CheckUserExistance() {
        val current_user_id : String = mAuth.currentUser!!.uid;

        listener1 = usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!dataSnapshot.hasChild(current_user_id)) {        //user authenticated but not present in database
                    SendUserToSetupActivity();
                } else {
                    listener2 = usersRef.child(currentUserId).addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (dataSnapshot.exists()) {
                                if (dataSnapshot.hasChild("fullname")) {
                                    val fullname = dataSnapshot.child("fullname").value.toString()
                                    nav_username.text = fullname;
                                }
                                if (dataSnapshot.hasChild("profileImage")) {
                                    val image = dataSnapshot.child("profileImage").value.toString();
                                    Picasso.get().load(image).placeholder(R.drawable.profile_img)
                                            .into(profile_img);
                                }
                            }
                        }

                        override fun onCancelled(p0: DatabaseError) {
                        }
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        });
    }

    private fun SendUserToSetupActivity() {
        val setupIntent = Intent(this, SetupActivity::class.java);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();
    }

    private fun SendUserToLoginActivity() {
        val loginIntent = Intent(this, LoginActivity::class.java);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }
    private fun SendUserToPostActivity() {
        val addNewPostIntent = Intent(this, PostActivity::class.java);
        startActivity(addNewPostIntent);
    }

    private fun SendUserToSettingsActivity() {
        val settingsIntent = Intent(this, SettingsActivity::class.java);
        startActivity(settingsIntent);
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(actionBarDrawerToggle?.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item)
    }

}