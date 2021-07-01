package com.example.socialad

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_list_chat.*
import java.util.*
import java.util.concurrent.CountDownLatch

class ListChatActivity : AppCompatActivity() {

    private lateinit var mAuth : FirebaseAuth;
    private lateinit var currentId : String;
    private lateinit var rootRef: DatabaseReference;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_chat)

        val mToolbar : Toolbar = list_chat_toolbar as Toolbar
        setSupportActionBar(mToolbar);
        supportActionBar?.title = "Chat";
        supportActionBar?.setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        currentId = mAuth.currentUser!!.uid
        rootRef = FirebaseDatabase.getInstance().reference

        list_chat.setHasFixedSize(true);
        val linearLayoutManager: LinearLayoutManager = LinearLayoutManager(this);
        list_chat.layoutManager = linearLayoutManager;
        val users: MutableList<Users> = ArrayList()

        rootRef.child("Messages").child(currentId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val temp: MutableList<String> = ArrayList()
                if(!snapshot.hasChildren()){
                    Toast.makeText(this@ListChatActivity, "No Messages yet", Toast.LENGTH_SHORT).show();
                }
                for( ds in snapshot.children) {
                    //val message: Messages? = ds.getValue(Messages::class.java);
                    temp.add(ds.key!!)
                }
                for(r in temp){
                    rootRef.child("Users").child(r).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(error: DatabaseError) {
                        }

                        override fun onDataChange(snapshot: DataSnapshot) {

                            if(snapshot.exists()){
                                val user: Users? = snapshot.getValue(Users::class.java)
                                if (user != null) {
                                    user.key = snapshot.key;
                                    users.add(user)
                                }
                            }
                            val listChatAdapter = ListChatAdapter(users)
                            listChatAdapter.notifyDataSetChanged();
                            list_chat.adapter = listChatAdapter
                        }
                    })
                }
            }
        })

    }
}