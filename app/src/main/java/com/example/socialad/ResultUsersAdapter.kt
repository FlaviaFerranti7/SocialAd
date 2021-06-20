package com.example.socialad

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class ResultUsersAdapter(private val dataSet: MutableList<Users>) : RecyclerView.Adapter<ResultUsersAdapter.ViewHolder>() {

    private lateinit var currentUserId: String;

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val fullname: TextView;
        val status: TextView;
        val image: CircleImageView;

        init {
            // Define click listener for the ViewHolder's View.
            fullname = view.findViewById(R.id.search_full_name)
            status = view.findViewById(R.id.search_status)
            image = view.findViewById(R.id.users_profile_image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.all_users_display_layout, parent, false)

        return ViewHolder(view)
    }


    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.fullname.text = dataSet[position].fullname
        viewHolder.status.text = dataSet[position].status
        Picasso.get().load(dataSet[position].profileImage).placeholder(R.drawable.profile_img).into(viewHolder.image);

        viewHolder.itemView.setOnClickListener { v ->
            if(dataSet[position].key != FirebaseAuth.getInstance().currentUser!!.uid) {
                val option = arrayOf<CharSequence>(dataSet[position].fullname + "'s Profile", "Send Message");
                val builder = AlertDialog.Builder(viewHolder.itemView.context);
                builder.setTitle("Select Options");
                builder.setItems(option) { dialog, which ->
                    if (which == 0) {
                        val intent = Intent(v.context, ProfileActivity::class.java)
                        intent.putExtra("user", dataSet[position].key)
                        v.context.startActivity(intent)
                    }
                    if (which == 1) {
                        val intent = Intent(v.context, ChatActivity::class.java)
                        intent.putExtra("user", dataSet[position].key)
                        intent.putExtra("username", dataSet[position].fullname)
                        intent.putExtra("userimage", dataSet[position].profileImage)
                        v.context.startActivity(intent)}
                }
                builder.create().show();
            }
            else{
                val intent = Intent(v.context, ProfileActivity::class.java)
                intent.putExtra("user", dataSet[position].key)
                v.context.startActivity(intent)
            }
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size


}