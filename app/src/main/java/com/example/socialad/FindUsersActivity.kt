package com.example.socialad

import android.os.Bundle
import android.text.TextUtils
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_find_users.*
import kotlinx.android.synthetic.main.activity_main.*

class FindUsersActivity : AppCompatActivity() {

    private lateinit var allUserDatabaseRef: DatabaseReference;

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

        allUserDatabaseRef = FirebaseDatabase.getInstance().reference.child("Users")

        find_btn.setOnClickListener {
            var name = find_name.text.toString()
            if(!TextUtils.isEmpty(name)) {
                name = name[0].toUpperCase() + name.substring(1)
            }
            val city = find_city.text.toString()
            var status = ""
            if (find_user_radiogroup.checkedRadioButtonId != -1){
                val radioButton : RadioButton = find_user_radiogroup.findViewById(find_user_radiogroup.checkedRadioButtonId)
                status = radioButton.text.toString()
            }
            SearchUsers(name, city, status);
        }
    }

    private fun SearchUsers(name: String, city: String, status: String) {
        var usersQuery = allUserDatabaseRef.orderByChild("fullname").startAt(name).endAt(name + "\uf8ff").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val temp: MutableList<Users> = ArrayList()
                if(!snapshot.hasChildren()){
                    Toast.makeText(this@FindUsersActivity, "No result found", Toast.LENGTH_SHORT).show();
                }
                for( ds in snapshot.children) {
                    val user: Users? = ds.getValue(Users::class.java);
                    if (user != null) {
                        if((TextUtils.isEmpty(city) && TextUtils.isEmpty(status)) || (TextUtils.isEmpty(city) && !TextUtils.isEmpty(status) && user.status!!.contains(status, ignoreCase = true))
                                || ( !TextUtils.isEmpty(city) && TextUtils.isEmpty(status) && user.country!!.contains(city, ignoreCase = true))
                                || ( !TextUtils.isEmpty(city) && !TextUtils.isEmpty(status) && user.status!!.contains(status, ignoreCase = true) && user.country!!.contains(city, ignoreCase = true)) ) {
                            user.key = ds.key;
                            temp.add(user);
                        }
                    }
                }
                if(temp.isEmpty()){
                    Toast.makeText(this@FindUsersActivity, "No result found", Toast.LENGTH_SHORT).show();
                }
                val resultUsersAdapter = ResultUsersAdapter(temp)
                find_result_list.adapter = resultUsersAdapter;
            }

        })

    }

}