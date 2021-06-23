package com.example.socialad

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.LocationServices
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class PostsAdapter(private val dataSet: MutableList<Posts>) : RecyclerView.Adapter<PostsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val fullname: TextView;
        val date: TextView;
        val time: TextView;
        val description: TextView;
        val place: TextView;
        val tag: TextView;
        val profileImage: CircleImageView;

        init {
            // Define click listener for the ViewHolder's View.
            fullname = view.findViewById(R.id.post_user_name)
            date = view.findViewById(R.id.post_date)
            time = view.findViewById(R.id.post_time)
            description = view.findViewById(R.id.post_description)
            place = view.findViewById(R.id.post_location)
            tag = view.findViewById(R.id.post_tag)
            profileImage = view.findViewById(R.id.post_profile_image)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostsAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.all_post_layout, parent, false)

        return ViewHolder(view)

    }

    override fun getItemCount() = dataSet.size


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        
        holder.description.text = dataSet[position].description;
        holder.tag.text = dataSet[position].type;
        holder.date.text = "   " + dataSet[position].date
        holder.time.text = "  -  " + dataSet[position].time
        holder.fullname.text = dataSet[position].fullname
        Picasso.get().load(dataSet[position].profileImage).placeholder(R.drawable.profile_img).into(holder.profileImage);

        if(dataSet[position].place!=""){
            holder.place.text = dataSet[position].place;
            holder.place.setTextColor(ContextCompat.getColor(holder.place.context, R.color.holo_blue_light));
            holder.place.setOnClickListener {
                val intent = Intent(it.context, MapsActivity::class.java)
                intent.putExtra("latitude", dataSet[position].latitude)
                intent.putExtra("longitude", dataSet[position].longitude)
                intent.putExtra("place", dataSet[position].place)
                it.context.startActivity(intent)
            }
        }
        else{
            holder.place.text = "No location available";
            holder.place.setTextColor(ContextCompat.getColor(holder.place.context, R.color.gray));
        }

        holder.itemView.setOnClickListener { v ->
            val intent = Intent(v.context, ClickPostActivity::class.java)
            intent.putExtra("PostKey", dataSet[position].date+dataSet[position].time+dataSet[position].uid)
            v.context.startActivity(intent)

        }

    }
}