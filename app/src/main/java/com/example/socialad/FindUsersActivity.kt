package com.example.socialad

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_find_users.*

class FindUsersActivity : AppCompatActivity() {

    private lateinit var allUserDatabaseRef: DatabaseReference;
    private var result = FindUsers("","","");

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_users)

        Utils.setupUI(find_user_layout, this)

        val mToolbar: Toolbar = find_app_bar as Toolbar
        setSupportActionBar(mToolbar);
        supportActionBar?.title = "Search Users";
        supportActionBar?.setDisplayHomeAsUpEnabled(true);

        find_result_list.setHasFixedSize(true);
        val linearLayoutManager: LinearLayoutManager = LinearLayoutManager(this);
        find_result_list.layoutManager = linearLayoutManager;

        allUserDatabaseRef = FirebaseDatabase.getInstance("https://socialad-78b0e-default-rtdb.firebaseio.com/").reference.child("Users")

        find_btn.setOnClickListener {
            val value = find_box_input.text.toString()
            SearchUsers(value);
        }
    }

    private fun SearchUsers(value: String) {
        allUserDatabaseRef.keepSynced(true);
        var usersQuery = allUserDatabaseRef.orderByChild("fullname").startAt(value).endAt(value + "\uf8ff").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val temp: MutableList<FindUsers> = ArrayList()
                for( ds in snapshot.children) {
                    val user: Users? = ds.getValue(Users::class.java);
                    if (user != null) {
                        result.fullname = user.fullname
                        result.profileImage = user.profileImage
                        result.status = user.status
                        temp.add(result);
                        result = FindUsers("","","");
                    }
                }
                for(r in temp){
                    println(r.fullname)
                }
                val resultUsersAdapter = ResultUsersAdapter(temp)
                find_result_list.adapter = resultUsersAdapter;
            }

        })

    }

}