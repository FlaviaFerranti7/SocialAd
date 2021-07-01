package com.example.socialad

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_post.*
import kotlinx.android.synthetic.main.activity_setup.*
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

    private lateinit var listener : ValueEventListener;

    private val AUTOCOMPLETE_REQUEST_CODE = 1
    private var latitude : String = "";
    private var longitude : String = "";
    private var placename : String = "";



    @SuppressLint("SimpleDateFormat")
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
        usersRef = FirebaseDatabase.getInstance().reference.child("Users");
        postsRef = FirebaseDatabase.getInstance().reference.child("Posts");

        saveCurrentTime = SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().time)
        saveCurrentDate = SimpleDateFormat("MM-dd-yyyy").format(Calendar.getInstance().time)
        postname = saveCurrentDate + saveCurrentTime

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, "YOUR_KEY");
        }

        add_place_btn.setOnClickListener {
            // Set the fields to specify which types of place data to
            // return after the user has made a selection.
            val fields = listOf(Place.Field.LAT_LNG, Place.Field.NAME)

            // Start the autocomplete intent.
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                .build(this)
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)

        }

        add_post_btn.setOnClickListener {
            ValidatePost();
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    data?.let {
                        val place = Autocomplete.getPlaceFromIntent(data)
                        Log.i(".Post", "Place: ${place.name}, ${place.latLng}")
                        location_text.text = place.name
                        latitude = place.latLng?.latitude.toString()!!
                        longitude = place.latLng?.longitude.toString()!!
                        placename = place.name.toString()
                    }
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    // TODO: Handle the error.
                    data?.let {
                        val status = Autocomplete.getStatusFromIntent(data)
                        Log.i(".Post", status.statusMessage!!)
                    }
                }
                Activity.RESULT_CANCELED -> {
                    // The user canceled the operation.
                }
            }
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun ValidatePost() {
        val description : String = post_text.text.toString();
        if(TextUtils.isEmpty(description)){
            Toast.makeText(this, "Please write something before posting ", Toast.LENGTH_SHORT).show();
        }
        if (post_radiogroup.checkedRadioButtonId == -1){
            Toast.makeText(this, "Please select the post typology", Toast.LENGTH_SHORT).show();
        }
        else{
            loadingBar.setTitle("Saving post");
            loadingBar.setMessage("Please wait while we are saving your new post");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            val radioButton : RadioButton = post_radiogroup.findViewById(post_radiogroup.checkedRadioButtonId)
            val typology = radioButton.text.toString()
            StoringPostToFirebaseDatabase(description, typology);
        }
    }

    private fun StoringPostToFirebaseDatabase(d: String, t:String) {
        listener = usersRef.child(currentUserId).addValueEventListener(object : ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    val userFullName : String = p0.child("fullname").value.toString();
                    val userProfileImage : String = p0.child("profileImage").value.toString();
                    val userCity : String = p0.child("country").value.toString();

                    var postMap = HashMap<String, Any>();
                    postMap.put("uid", currentUserId);
                    postMap.put("date", saveCurrentDate);
                    postMap.put("time", saveCurrentTime);
                    postMap.put("fullname", userFullName);
                    postMap.put("profileImage", userProfileImage);
                    postMap.put("city", userCity);
                    postMap.put("description", d);
                    postMap.put("type", t);
                    if(placename!= "") {
                        postMap.put("latitude", latitude);
                        postMap.put("longitude", longitude);
                        postMap.put("place", placename);
                    }
                    else{
                        postMap.put("place", "");
                        postMap.put("latitude", "");
                        postMap.put("longitude", "");
                    }

                    var connected = false
                    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                    connected = if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)!!.state == NetworkInfo.State.CONNECTED ||
                            connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)!!.state == NetworkInfo.State.CONNECTED) {
                        //we are connected to a network
                        true
                    } else false
                    if(!connected){
                        SendUserToMainActivity();
                        Toast.makeText(this@PostActivity, "You have lost connection, the post will be created once your reconnect", Toast.LENGTH_LONG).show();
                        loadingBar.dismiss();
                    }

                    postsRef.child(postname + currentUserId).updateChildren(postMap).addOnCompleteListener {
                      if(it.isSuccessful){
                          SendUserToMainActivity();
                          Toast.makeText(this@PostActivity, "New post created successfully ", Toast.LENGTH_SHORT).show();
                          loadingBar.dismiss();
                      }
                      else{
                          Toast.makeText(this@PostActivity, "Error occurred while creating post, please try again", Toast.LENGTH_SHORT).show();
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