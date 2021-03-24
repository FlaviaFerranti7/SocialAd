package com.example.socialad

import android.annotation.SuppressLint
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle;

    @SuppressLint("RestrictedApi")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mToolbar : Toolbar = main_page_toolbar as Toolbar
        setSupportActionBar(mToolbar);
        supportActionBar?.title = "Home";

        val headerView: View = navigation_view.inflateHeaderView(R.layout.navigation_header);
        actionBarDrawerToggle = ActionBarDrawerToggle(this, drawer_layout, R.string.drawer_open, R.string.drawer_close);
        drawer_layout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        supportActionBar?.setDisplayHomeAsUpEnabled(true);

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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(actionBarDrawerToggle?.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item)
    }

}