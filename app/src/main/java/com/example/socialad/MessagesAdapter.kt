package com.example.socialad

import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.collection.LLRBNode
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class MessagesAdapter (private val dataSet: MutableList<Messages>) : RecyclerView.Adapter<MessagesAdapter.ViewHolder>(){

    private lateinit var mAuth : FirebaseAuth;
    private lateinit var usersRef: DatabaseReference;

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val sender_message_text: TextView;
        val receiver_message_text: TextView;
        val image: CircleImageView;

        init {
            sender_message_text = view.findViewById(R.id.sender_message_text)
            receiver_message_text = view.findViewById(R.id.receiver_message_text)
            image = view.findViewById(R.id.message_profile_image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.message_layout_of_users, parent, false)

        mAuth = FirebaseAuth.getInstance();

        return ViewHolder(view)
    }

    override fun getItemCount() = dataSet.size

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        val messageSenderId = mAuth.currentUser!!.uid
        val fromUserId = dataSet[position].from

        usersRef = FirebaseDatabase.getInstance().reference.child("Users").child(fromUserId!!);
        usersRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val imageD = snapshot.child("profileImage").value.toString()
                    Picasso.get().load(imageD).placeholder(R.drawable.profile_img).into(viewHolder.image);

                }
            }

        })

        viewHolder.image.visibility = View.INVISIBLE
        viewHolder.receiver_message_text.visibility = View.INVISIBLE

        if(fromUserId == messageSenderId) {         //i am the sender
            viewHolder.sender_message_text.setBackgroundResource(R.drawable.sender_message_text_background);
            viewHolder.sender_message_text.setTextColor(Color.WHITE);
            viewHolder.sender_message_text.gravity = Gravity.LEFT
            viewHolder.sender_message_text.text = dataSet[position].message
        }
        else{

            viewHolder.image.visibility = View.VISIBLE
            viewHolder.sender_message_text.visibility = View.INVISIBLE
            viewHolder.receiver_message_text.visibility = View.VISIBLE

            viewHolder.receiver_message_text.setBackgroundResource(R.drawable.receiver_message_text_background);
            viewHolder.receiver_message_text.setTextColor(Color.WHITE);
            viewHolder.receiver_message_text.gravity = Gravity.LEFT
            viewHolder.receiver_message_text.text = dataSet[position].message

        }

    }
}