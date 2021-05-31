package com.example.messengerapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.messengerapp.R
import com.example.messengerapp.model_classes.Chat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView


class ChatsAdapter(
    var mContext: Context,
    var mChatList: List<Chat>,
    var imageUrl: String
) : RecyclerView.Adapter<ChatsAdapter.ViewHolder>() {

    var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        return if (position == 1) {
            val view =
                LayoutInflater.from(mContext).inflate(R.layout.message_item_right, parent, false)
            ViewHolder(view)
        } else {
            val view =
                LayoutInflater.from(mContext).inflate(R.layout.message_item_left, parent, false)
            ViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return mChatList.size
    }

    // image message - right side
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val chat: Chat = mChatList[position]

        Picasso.get().load(imageUrl).into(holder.profile_image)

        //images messages
        if (chat.getMessage().equals("sent you an image") && !chat.getUrl().equals("")) {
            if (chat.getSender().equals(firebaseUser!!.uid)) {

                holder.show_text_message?.visibility = View.GONE
                holder.right_image_view?.visibility = View.VISIBLE
                Picasso.get().load(chat.getUrl()).into(holder.right_image_view)

            } else if (!chat.getSender().equals(firebaseUser!!.uid)) {

                holder.show_text_message?.visibility = View.GONE
                holder.left_img_view?.visibility = View.VISIBLE
                Picasso.get().load(chat.getUrl()).into(holder.left_img_view)
            }

        }
        //text messages
        else {
            holder.show_text_message?.visibility = View.VISIBLE
            holder.show_text_message?.text = chat.getMessage()
        }

        //sent and seen message
        if (position == mChatList.size - 1) {
            if (chat.isIsSeen()) {
                holder.text_seen?.text = "Seen"
                if (chat.getMessage().equals("sent you an image") && chat.getUrl().equals("")) {
                    val rlp: RelativeLayout.LayoutParams? =
                        holder.text_seen?.layoutParams as RelativeLayout.LayoutParams?
                    rlp!!.setMargins(0, 245, 10, 0)
                    holder.text_seen?.layoutParams = rlp
                }
            } else {
                holder.text_seen?.text = "Sent"
                if (chat.getMessage().equals("sent you an image") && chat.getUrl().equals("")) {
                    val rlp: RelativeLayout.LayoutParams? =
                        holder.text_seen?.layoutParams as RelativeLayout.LayoutParams?
                    rlp!!.setMargins(0, 245, 10, 0)
                    holder.text_seen?.layoutParams = rlp
                }
            }
        } else {
            holder.text_seen?.visibility = View.GONE
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (mChatList[position].getSender().equals(firebaseUser!!.uid)) {
            1
        } else {
            0
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profile_image: CircleImageView? = itemView.findViewById(R.id.profile_image_left_chat)
        val left_img_view: ImageView? = itemView.findViewById(R.id.left_image_view)
        val text_seen: TextView? = itemView.findViewById(R.id.text_seen)
        val show_text_message: TextView? = itemView.findViewById(R.id.show_text_message)
        val right_image_view: ImageView? = itemView.findViewById(R.id.right_image_view)
    }
}