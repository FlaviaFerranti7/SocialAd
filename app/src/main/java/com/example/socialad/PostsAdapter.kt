package com.example.socialad

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class PostsAdapter(private val dataSet: MutableList<Posts>) : RecyclerView.Adapter<PostsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val fullname: TextView;
        val date: TextView;
        val time: TextView;
        val description: TextView;
        val profileImage: CircleImageView;

        init {
            // Define click listener for the ViewHolder's View.
            fullname = view.findViewById(R.id.post_user_name)
            date = view.findViewById(R.id.post_date)
            time = view.findViewById(R.id.post_time)
            description = view.findViewById(R.id.post_description)
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
        holder.date.text = "   " + dataSet[position].date
        holder.time.text = "  -  " + dataSet[position].time
        holder.fullname.text = dataSet[position].fullname
        Picasso.get().load(dataSet[position].profileImage).placeholder(R.drawable.profile_img).into(holder.profileImage);

        holder.itemView.setOnClickListener { v ->
            val intent = Intent(v.context, ClickPostActivity::class.java)
            intent.putExtra("PostKey", dataSet[position].date+dataSet[position].time+dataSet[position].uid)
            v.context.startActivity(intent)

        }

    }
}