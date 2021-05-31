package com.example.messengerapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.messengerapp.fragments.ChatsFragment
import com.example.messengerapp.fragments.SearchFragment
import com.example.messengerapp.fragments.SettingsFragment
import com.example.messengerapp.model_classes.Chat
import com.example.messengerapp.model_classes.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var refUsers: DatabaseReference? = null
    var firebaseUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firebaseUser = FirebaseAuth.getInstance().currentUser
        refUsers = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)

        setSupportActionBar(toolbar_main)
        supportActionBar?.title = ""    //to set title null else app name will show

        /*val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
        viewPagerAdapter.addFragment(ChatsFragment(), "Chats")
        viewPagerAdapter.addFragment(SearchFragment(), "Search")
        viewPagerAdapter.addFragment(SettingsFragment(), "Settings")

        view_pager_main.adapter = viewPagerAdapter
        tab_layout_main.setupWithViewPager(view_pager_main)*/

        val ref =
            FirebaseDatabase.getInstance().reference.child("Chats")
        ref.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
                var countUnreadMessage = 0
                for (snap in snapshot.children){
                    val chat = snap.getValue(Chat::class.java)
                    if(chat!!.getReceiver().equals(firebaseUser!!.uid) && !chat.isIsSeen()){
                        countUnreadMessage += 1
                    }
                }

                if (countUnreadMessage == 0){
                    viewPagerAdapter.addFragment(ChatsFragment(), "Chats")
                }else{
                    viewPagerAdapter.addFragment(ChatsFragment(), "($countUnreadMessage) Chats")
                }
                viewPagerAdapter.addFragment(SearchFragment(), "Search")
                viewPagerAdapter.addFragment(SettingsFragment(), "Settings")
                view_pager_main.adapter = viewPagerAdapter
                tab_layout_main.setupWithViewPager(view_pager_main)
            }

        })


        refUsers?.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val user: Users? = snapshot.getValue(Users::class.java)
                    user_name.text = user?.getUsername()
                    Picasso.get().load(user?.getProfile()).placeholder(R.drawable.profile_place).into(profile_image)
                }
            }

        })

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_logout -> {
                FirebaseAuth.getInstance().signOut()
                val i = Intent(this@MainActivity, WelcomeActivity::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(i)
                finish()

                return true
            }
        }
        return false
    }

    internal class ViewPagerAdapter(fragmentManager: FragmentManager) :
        FragmentPagerAdapter(fragmentManager) {

        private val fragments = ArrayList<Fragment>()
        private val titles = ArrayList<String>()

        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        override fun getCount(): Int {
            return fragments.size
        }

        fun addFragment(fragment: Fragment, title: String) {
            fragments.add(fragment)
            titles.add(title)
        }

        override fun getPageTitle(i: Int): CharSequence? {
            return titles[i]
        }
    }
}