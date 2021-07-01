package com.example.socialad

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.RadioButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_find_users.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_post.*
import kotlinx.android.synthetic.main.navigation_header.*
import okhttp3.internal.Util


class MainActivity : AppCompatActivity() {

    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle;
    private lateinit var googleSignInClient: GoogleSignInClient;
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

        Utils.setupUI(main_layout,this);
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.currentUser!!.uid;
        usersRef = FirebaseDatabase.getInstance().reference.child("Users");

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

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
                    SendUserToProfileActivity()
                    true
                }
                R.id.nav_search -> {
                    SendUserToSearchActivity()
                    true
                }
                R.id.nav_messages -> {
                    SendUserToListChatActivity();
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
                    googleSignInClient.signOut()
                    SendUserToLoginActivity();
                    true
                }
                else -> false
            }
        }

        var connected = false
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connected = if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)!!.state == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)!!.state == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            true
        } else false

        if(!connected){
            Toast.makeText(this, "You are not connect to Internet!", Toast.LENGTH_LONG).show()
        }

        main_add_post.setOnClickListener {
                SendUserToPostActivity();
        }

        DisplayAllUsersPost();

        find_post_btn.setOnClickListener{
            var tag = ""
            if (find_post_radiogroup.checkedRadioButtonId != -1){
                val radioButton : RadioButton = find_post_radiogroup.findViewById(find_post_radiogroup.checkedRadioButtonId)
                tag = radioButton.text.toString()
            }

            val city = find_post_city.text.toString()
            val content = find_post_content.text.toString()
            if(!TextUtils.isEmpty(tag) || !TextUtils.isEmpty(city) || !TextUtils.isEmpty(content)) {
                SearchPost(tag, city, content);
            }
        }

    }

    private fun SearchPost(t:String, city :String, c : String) {
        FirebaseDatabase.getInstance().reference
                .child("Posts").orderByChild("type").startAt(t).endAt(t + "\uf8ff").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {

                val temp: MutableList<Posts> = ArrayList()
                if(!snapshot.hasChildren()){
                    Toast.makeText(this@MainActivity, "No result found", Toast.LENGTH_SHORT).show();
                }
                for (ds in snapshot.children) {
                    val post: Posts? = ds.getValue(Posts::class.java);
                    if (post != null) {
                        if((TextUtils.isEmpty(city) && TextUtils.isEmpty(c)) || (TextUtils.isEmpty(city) && !TextUtils.isEmpty(c) && post.description!!.contains(c, ignoreCase = true))
                                || ( !TextUtils.isEmpty(city) && TextUtils.isEmpty(c) && post.city!!.contains(city, ignoreCase = true))
                                || ( !TextUtils.isEmpty(city) && !TextUtils.isEmpty(c) && post.description!!.contains(c, ignoreCase = true) && post.city!!.contains(city, ignoreCase = true)) ) {
                            temp.add(post);
                        }
                    }
                }
                if(temp.isEmpty()){
                    Toast.makeText(this@MainActivity, "No result found", Toast.LENGTH_SHORT).show();
                }
                val postsAdapter = PostsAdapter(temp)
                all_users_post_list.adapter = postsAdapter;

            }

        })

    }

    private fun DisplayAllUsersPost() {
        FirebaseDatabase.getInstance().reference
                .child("Posts").addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        val temp: MutableList<Posts> = ArrayList()
                        for (ds in snapshot.children) {
                            val post: Posts? = ds.getValue(Posts::class.java);
                            if (post != null) {
                                temp.add(post);
                            }
                        }
                        val postsAdapter = PostsAdapter(temp)
                        all_users_post_list.adapter = postsAdapter;
                    }
                })
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
                if (!dataSnapshot.hasChild(current_user_id) || !dataSnapshot.child(current_user_id).child("fullname").exists()) {        //user authenticated but not present in database or only image saved
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
    private fun SendUserToProfileActivity() {
        val profileIntent = Intent(this, ProfileActivity::class.java);
        startActivity(profileIntent);
    }

    private fun SendUserToSearchActivity() {
        val searchIntent = Intent(this, FindUsersActivity::class.java);
        startActivity(searchIntent);
    }

    private fun SendUserToListChatActivity() {
        val searchIntent = Intent(this, ListChatActivity::class.java);
        startActivity(searchIntent);
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(actionBarDrawerToggle?.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item)
    }

}