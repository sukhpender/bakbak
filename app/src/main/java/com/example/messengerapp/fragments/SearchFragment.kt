package com.example.messengerapp.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.messengerapp.R
import com.example.messengerapp.adapters.UsersAdapter
import com.example.messengerapp.model_classes.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_search.*

class SearchFragment : Fragment() {

    private var userAdapter: UsersAdapter? = null
    private var mUsers: List<Users>? = null
    private var edt_txt_search: EditText? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_search, container, false)

        edt_txt_search = view.findViewById(R.id.edt_search_user)
        mUsers = ArrayList()
        retrieveAllUsers()
        rv_searchList?.setHasFixedSize(true)
        rv_searchList?.layoutManager = LinearLayoutManager(context)


        edt_txt_search?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchForUsers(s.toString().toLowerCase())
            }

        })
        return view
    }

    private fun retrieveAllUsers() {
        val firebaseUserId = FirebaseAuth.getInstance().currentUser?.uid
        val refUsers = FirebaseDatabase.getInstance().reference.child("Users")

        refUsers.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {

                (mUsers as ArrayList<Users>).clear()
                if (edt_txt_search?.text.toString() == "") {
                    for (snap in snapshot.children) {

                        val users: Users? = snap.getValue(Users::class.java)
                        if ((users!!.getUID()) != firebaseUserId) {
                            (mUsers as ArrayList<Users>).add(users)
                        }
                    }
                    userAdapter = UsersAdapter(context!!, mUsers!!, false)
                    rv_searchList?.adapter = userAdapter
                    userAdapter?.notifyDataSetChanged()
                }
            }

        })

    }

    private fun searchForUsers(str: String) {

        val firebaseUserId = FirebaseAuth.getInstance().currentUser?.uid
        val queryUsers = FirebaseDatabase.getInstance().reference.child("Users")
            .orderByChild("search")
            .startAt(str)
            .endAt(str + "\uf8ff")

        queryUsers.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                (mUsers as ArrayList<Users>).clear()

                for (snap in snapshot.children) {

                    val users: Users? = snap.getValue(Users::class.java)
                    if ((users!!.getUID()) != firebaseUserId) {
                        (mUsers as ArrayList<Users>).add(users)
                    }
                }
                userAdapter = UsersAdapter(context!!, mUsers!!, false)
                rv_searchList?.adapter = userAdapter
                userAdapter?.notifyDataSetChanged()
            }

        })
    }

}