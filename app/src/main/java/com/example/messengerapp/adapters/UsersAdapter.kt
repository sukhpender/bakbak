package com.example.messengerapp.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.messengerapp.MainActivity
import com.example.messengerapp.MessageChatActivity
import com.example.messengerapp.R
import com.example.messengerapp.model_classes.Users
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.user_search_item_layout.view.*

class UsersAdapter(
    mContext: Context,
    mUserList: List<Users>,
    isChatChecked: Boolean
) : RecyclerView.Adapter<UsersAdapter.ViewHolder?>() {

    private val mContext: Context
    private val mUserList: List<Users>
    private val isChatChecked: Boolean

    init {
        this.mContext = mContext
        this.mUserList = mUserList
        this.isChatChecked = isChatChecked
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(mContext).inflate(R.layout.user_search_item_layout, parent, false)
        return UsersAdapter.ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mUserList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val user: Users = mUserList[position]
        holder.userName.text = user?.getUsername()
        Picasso.get().load(user?.getProfile()).placeholder(R.drawable.profile_place).into(holder.profileImage)

        holder.itemView.setOnClickListener{
            val option = arrayOf<CharSequence>(
                "Send Message",
                "Visit Profile"
            )
            val builder: AlertDialog.Builder = AlertDialog.Builder(mContext)
            builder.setTitle("What do you want")
            builder.setItems(option,DialogInterface.OnClickListener{
                dialog, position ->
                if (position == 0){
                    val intent = Intent(mContext,MessageChatActivity::class.java)
                    intent.putExtra("visit_id",user.getUID())
                    mContext.startActivity(intent)
                }
                if (position == 1){

                }
            })
            builder.show()
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var userName: TextView = itemView.findViewById(R.id.username)
        var profileImage: CircleImageView = itemView.findViewById(R.id.profile_image)
        var onLineImageView: ImageView = itemView.findViewById(R.id.image_online)
        var offLineImageView: ImageView = itemView.findViewById(R.id.image_offline)
        var lastMessage: TextView = itemView.findViewById(R.id.message_last)

    }
}