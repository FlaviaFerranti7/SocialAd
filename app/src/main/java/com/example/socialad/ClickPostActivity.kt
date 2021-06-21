package com.example.socialad

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
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

    private val AUTOCOMPLETE_REQUEST_CODE = 1
    private var latitude : String = "";
    private var longitude : String = "";
    private var placename : String = "";

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
        change_place_btn.visibility = View.INVISIBLE

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, "AIzaSyBjopnsGwv8GcZBAvVoRv5Dqtu4yw4Q5R4");
        }

        clickPostRef = FirebaseDatabase.getInstance("https://socialad-78b0e-default-rtdb.firebaseio.com/").reference.child("Posts").child(postKey);
        clickPostRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){

                    var profileImage = snapshot.child("profileImage").value.toString()
                    var username = snapshot.child("fullname").value.toString()
                    var date = snapshot.child("date").value.toString()
                    var time = snapshot.child("time").value.toString()
                    var description = snapshot.child("description").value.toString()
                    var tag = snapshot.child("type").value.toString()
                    placename = snapshot.child("place").value.toString()
                    latitude = snapshot.child("latitude").value.toString()
                    longitude = snapshot.child("longitude").value.toString()
                    var databaseUserId = snapshot.child("uid").value.toString()

                    if(placename != ""){
                        click_post_location.text = placename;
                        click_post_location.setTextColor(ContextCompat.getColor(this@ClickPostActivity, R.color.holo_blue_light));
                        click_post_location.setOnClickListener {
                            val intent = Intent(it.context, MapsActivity::class.java)
                            intent.putExtra("latitude", latitude)
                            intent.putExtra("longitude", longitude)
                            intent.putExtra("place", placename)
                            it.context.startActivity(intent)
                        }
                    }
                    else{
                        click_post_location.text = "No location available";
                        click_post_location.setTextColor(ContextCompat.getColor(this@ClickPostActivity, R.color.gray));
                    }

                    Picasso.get().load(profileImage).placeholder(R.drawable.profile_img).into(click_post_profile_image);
                    click_post_user_name.text = username;
                    click_post_date.text = "   $date";
                    click_post_time.text = "  -  $time";
                    click_post_description.text = description;
                    click_tag.text = tag;

                    if(currentUserId.equals(databaseUserId)){
                        change_place_btn.visibility = View.VISIBLE
                        click_delete.visibility = View.VISIBLE
                        click_edit.visibility = View.VISIBLE
                    }

                    change_place_btn.setOnClickListener{
                        ChangeLocation(placename);
                    }
                    click_edit.setOnClickListener {
                        EditCurrentPost(description);
                    }
                    click_post_user_name.setOnClickListener {
                        if(databaseUserId != currentUserId){
                            val option = arrayOf<CharSequence>(username + "'s Profile", "Send Message");
                            val builder = android.app.AlertDialog.Builder(this@ClickPostActivity);
                            builder.setTitle("Select Options");

                            builder.setItems(option) { dialog, which ->
                                if (which == 0) {
                                    val intent = Intent(this@ClickPostActivity, ProfileActivity::class.java)
                                    intent.putExtra("user", databaseUserId)
                                    startActivity(intent)
                                }
                                if (which == 1) {
                                    val intent = Intent(this@ClickPostActivity, ChatActivity::class.java)
                                    intent.putExtra("user", databaseUserId)
                                    intent.putExtra("username", username)
                                    intent.putExtra("userimage", profileImage)
                                    startActivity(intent)}
                            }
                            builder.create().show();
                        }
                        else {
                            val intent = Intent(this@ClickPostActivity, ProfileActivity::class.java)
                            intent.putExtra("user", databaseUserId)
                            startActivity(intent)
                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })

        click_delete.setOnClickListener {
            DeleteCurrentPost();
        }
    }

    private fun ChangeLocation(place: String) {
        // Set the fields to specify which types of place data to
        // return after the user has made a selection.
        val fields = listOf(Place.Field.LAT_LNG, Place.Field.NAME)

        // Start the autocomplete intent.
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
            .build(this)
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    data?.let {
                        val place = Autocomplete.getPlaceFromIntent(data)
                        Log.i(".Post", "Place: ${place.name}, ${place.latLng}")
                        latitude = place.latLng?.latitude.toString()!!
                        longitude = place.latLng?.longitude.toString()!!
                        placename = place.name.toString()

                        clickPostRef.child("place").setValue(placename);
                        clickPostRef.child("latitude").setValue(latitude);
                        clickPostRef.child("longitude").setValue(longitude)
                        click_post_location.text = placename;
                        click_post_location.setTextColor(ContextCompat.getColor(this@ClickPostActivity, R.color.holo_blue_light));
                        Toast.makeText(this, "The location has been updated successfully", Toast.LENGTH_SHORT).show();
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

    private fun EditCurrentPost(description: String) {
        val builder = AlertDialog.Builder(this);
        builder.setTitle("Edit Post");

        val inputField = EditText(this);
        inputField.setText(description);
        builder.setView(inputField);

        builder.setPositiveButton("Update", DialogInterface.OnClickListener { dialog, which ->
            clickPostRef.child("description").setValue(inputField.text.toString());
            Toast.makeText(this, "The post has been updated successfully", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->
            dialog.cancel();
        });

        val dialog = builder.create();
        dialog.show();
        dialog.window!!.setBackgroundDrawableResource(android.R.color.background_light)

    }

    private fun DeleteCurrentPost() {
        clickPostRef.removeValue();
        SendUserToMainActivity()
        Toast.makeText(this, "The post has been deleted successfully", Toast.LENGTH_SHORT).show();
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