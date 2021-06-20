package com.example.socialad

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_click_post.*
import java.text.SimpleDateFormat
import java.util.*

class ChatActivity : AppCompatActivity() {

    private lateinit var receiverId : String;
    private lateinit var receivername : String;
    private lateinit var receiverimage : String;

    private lateinit var senderId : String;

    private lateinit var receiverNameText : TextView;
    private lateinit var receiverProfileImage : CircleImageView;

    private lateinit var mAuth : FirebaseAuth;
    private lateinit var rootRef: DatabaseReference;

    private lateinit var saveCurrentTime: String;
    private lateinit var saveCurrentDate: String;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        //Utils.setupUI(chat_layout,this);

        val mToolbar : Toolbar = chat_toolbar as Toolbar
        setSupportActionBar(mToolbar);
        supportActionBar?.title = "Messages";
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowCustomEnabled(true);
        val layoutInflater : LayoutInflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater;
        val action_bar_view : View = layoutInflater.inflate(R.layout.chat_custom_bar, null);
        supportActionBar?.setCustomView(action_bar_view);

        receiverNameText = findViewById(R.id.chat_custom_profile_name)
        receiverProfileImage = findViewById(R.id.chat_custom_profile_image)

        val extras = intent.extras
        if(extras!=null){
            receiverId = extras.getString("user").toString();
            receivername = extras.getString("username").toString();
            receiverimage = extras.getString("userimage").toString();
        }
        receiverNameText.text = receivername
        Picasso.get().load(receiverimage).placeholder(R.drawable.profile_img).into(receiverProfileImage);

        mAuth = FirebaseAuth.getInstance();
        senderId = mAuth.currentUser!!.uid
        rootRef = FirebaseDatabase.getInstance("https://socialad-78b0e-default-rtdb.firebaseio.com/").reference

        chat_send_message.setOnClickListener {
            SendMessage();
        }

    }

    @SuppressLint("SimpleDateFormat")
    private fun SendMessage() {
        val messageText = chat_input_message.text.toString()
        if(TextUtils.isEmpty(messageText)){
            Toast.makeText(this, "Please type a message first", Toast.LENGTH_SHORT).show();
        }
        else {
            val message_sender_ref : String = "Messages/$senderId/$receiverId";
            val message_receiver_ref : String = "Messages/$receiverId/$senderId";

            val user_message_key = rootRef.child("Messages").child(senderId).child(receiverId).push(); //unique random key
            val message_push_id = user_message_key.key

            saveCurrentTime = SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().time)
            saveCurrentDate = SimpleDateFormat("MM-dd-yyyy").format(Calendar.getInstance().time)

            var messageBodyTextMap = HashMap<String, Any>();
            messageBodyTextMap.put("message", messageText);
            messageBodyTextMap.put("time", saveCurrentTime);
            messageBodyTextMap.put("date", saveCurrentDate);
            messageBodyTextMap.put("from", senderId);

            var messageBodyDetailsMap = HashMap<String, Any>();
            messageBodyDetailsMap.put(message_sender_ref+"/"+message_push_id , messageBodyTextMap);
            messageBodyDetailsMap.put(message_receiver_ref+"/"+message_push_id , messageBodyTextMap);

            rootRef.updateChildren(messageBodyDetailsMap).addOnCompleteListener {
                if(it.isSuccessful){
                    Toast.makeText(this, "Message send successfully", Toast.LENGTH_SHORT).show();
                }
                else{
                    val error = it.exception?.message
                    Toast.makeText(this, "Error occured: $error", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}