package com.example.messengerapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.messengerapp.R
import com.example.messengerapp.adapters.UsersAdapter
import com.example.messengerapp.model_classes.ChatList
import com.example.messengerapp.model_classes.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_chats.*

class ChatsFragment : Fragment() {

    private var mUser: List<Users>? = null
    private var userAdapter: UsersAdapter? = null
    private var usersChatList: List<ChatList>? = null
    lateinit var recycler_chat_list: RecyclerView
    private var firebaseUser: FirebaseUser? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_chats, container, false)

        recycler_chat_list = view.findViewById(R.id.rv_chat_list)
        recycler_chat_list.setHasFixedSize(true)
        recycler_chat_list.layoutManager = LinearLayoutManager(context)

        firebaseUser = FirebaseAuth.getInstance().currentUser

        usersChatList = ArrayList()
        val ref =
            FirebaseDatabase.getInstance().reference.child("ChatList").child(firebaseUser!!.uid)

        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                (usersChatList as ArrayList).clear()

                for (snap in snapshot.children) {
                    val chatList = snap.getValue(ChatList::class.java)

                    (usersChatList as ArrayList).add(chatList!!)
                }
                retrieveChatList()
            }
        })
        return view
    }

    private fun retrieveChatList() {
        mUser = ArrayList()

        val ref = FirebaseDatabase.getInstance().reference.child("Users")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                (mUser as ArrayList).clear()

                for (snap in snapshot.children) {
                    val user = snap.getValue(Users::class.java)
                    for (eachChatList in usersChatList!!) {

                        if (user?.getUID().equals(eachChatList.getId())) {
                            (mUser as ArrayList).add(user!!)
                        }
                    }
                }
                userAdapter = UsersAdapter(context!!, (mUser as ArrayList), true)
                recycler_chat_list.adapter = userAdapter

            }

        })
    }

}