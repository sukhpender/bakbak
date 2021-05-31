package com.example.messengerapp.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.messengerapp.MainActivity
import com.example.messengerapp.R
import com.example.messengerapp.WelcomeActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var refUsers: DatabaseReference
    private var firebaseUserId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        setSupportActionBar(toolbar_register)
        supportActionBar?.title = "Register"    //to set title null else app name will show
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar_register?.setNavigationOnClickListener {
            val i = Intent(this@RegisterActivity, WelcomeActivity::class.java)
            startActivity(i)
            finish()
        }

        mAuth = FirebaseAuth.getInstance()

        onClick()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_register -> {
                registerUser()
            }
        }
    }

    private fun registerUser() {

        val username = edt_username_register.text.toString()
        val email = edt_email_register.text.toString()
        val password = edt_password_register.text.toString()

        when {
            username == "" -> {
                Toast.makeText(this@RegisterActivity, "Please enter username", Toast.LENGTH_SHORT)
                    .show()
            }
            email == "" -> {
                Toast.makeText(this@RegisterActivity, "Please enter email", Toast.LENGTH_SHORT)
                    .show()
            }
            password == "" -> {
                Toast.makeText(this@RegisterActivity, "Please enter password", Toast.LENGTH_SHORT)
                    .show()
            }
            else -> {
                window.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )
                progress_bar_register.visibility = View.VISIBLE
                mAuth.createUserWithEmailAndPassword(
                    email,
                    password
                ).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            this@RegisterActivity,
                            "Registration Successful",
                            Toast.LENGTH_SHORT
                        ).show()
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        firebaseUserId = mAuth.currentUser!!.uid
                        refUsers = FirebaseDatabase.getInstance().reference.child("Users")
                            .child(firebaseUserId)

                        val userHashMap = HashMap<String, Any>()
                        userHashMap["uid"] = firebaseUserId
                        userHashMap["username"] = username
                        userHashMap["profile"] =
                            "https://firebasestorage.googleapis.com/v0/b/bakbak-e520c.appspot.com/o/profile_place.png?alt=media&token=c8bc853d-3c6a-417f-a1eb-0a39295a8337"
                        /*userHashMap["cover"] =
                            "https://firebasestorage.googleapis.com/v0/b/bakbak-e520c.appspot.com/o/facebook-svgrepo-com.svg?alt=media&token=1dda1e90-4862-45f7-999d-fc460c92bcb5"*/
                        userHashMap["status"] = "offline"
                        userHashMap["search"] = username.toLowerCase()
                        userHashMap["facebook"] = "https://m.facebook.com"
                        userHashMap["instagram"] = "https://m.instagram.com"
                        userHashMap["website"] = "https://www.google.com"

                        refUsers.updateChildren(userHashMap).addOnCompleteListener { t ->
                            if (task.isSuccessful) {
                                val i = Intent(this@RegisterActivity, MainActivity::class.java)
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(i)
                                progress_bar_register.visibility = View.GONE
                                finish()
                            }
                        }
                    } else {
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        progress_bar_register.visibility = View.GONE
                        Toast.makeText(
                            this@RegisterActivity,
                            "Error Message: " + task.exception?.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }

                }
            }
        }
    }

    private fun onClick() {
        btn_register.setOnClickListener(this)
    }
}