package com.example.socialad

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val headerView: View = navigation_view.inflateHeaderView(R.layout.navigation_header);

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
                    Toast.makeText(this, "SETTINGS", Toast.LENGTH_SHORT).show();
                    true
                }
                R.id.nav_logout -> {
                    Toast.makeText(this, "LOGOUT", Toast.LENGTH_SHORT).show();
                    true
                }
                R.id.nav_post -> {
                    Toast.makeText(this, "ADD POST", Toast.LENGTH_SHORT).show();
                    true
                }
                else -> false
            }
        }


    }

}