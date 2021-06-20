package com.example.socialad

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class ListChatAdapter (private val dataSet: MutableList<Users>) : RecyclerView.Adapter<ListChatAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView;
        val image: CircleImageView;

        init {
            name = view.findViewById(R.id.list_chat_name)
            image = view.findViewById(R.id.list_chat_image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.display_list_chat_layout, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount() = dataSet.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.name.text = dataSet[position].fullname
        Picasso.get().load( dataSet[position].profileImage).placeholder(R.drawable.profile_img).into(holder.image);

        holder.itemView.setOnClickListener {v ->
            val intent = Intent(v.context, ChatActivity::class.java)
            intent.putExtra("user", dataSet[position].key)
            intent.putExtra("username", dataSet[position].fullname)
            intent.putExtra("userimage", dataSet[position].profileImage)
            v.context.startActivity(intent)
        }

    }

}