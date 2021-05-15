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
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setSupportActionBar(toolbar_login)
        supportActionBar?.title = "Login"    //to set title null else app name will show
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar_login?.setNavigationOnClickListener {
            val i = Intent(this@LoginActivity, WelcomeActivity::class.java)
            startActivity(i)
            finish()
        }

        mAuth = FirebaseAuth.getInstance()
        onClick()
    }

    private fun onClick() {
        btn_login.setOnClickListener(this)
        //txt_new_user_reg.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_login -> {
                loginUser()
            }
            R.id.txt_new_user_reg -> {
                val i = Intent(this@LoginActivity,RegisterActivity::class.java)
                startActivity(i)
                finish()
            }
        }
    }

    private fun loginUser() {
        val email = edt_email_login.text.toString()
        val password = edt_password_login.text.toString()
        when {
            email == "" -> {
                Toast.makeText(this@LoginActivity, "Please enter email", Toast.LENGTH_SHORT)
                    .show()
            }
            password == "" -> {
                Toast.makeText(this@LoginActivity, "Please enter password", Toast.LENGTH_SHORT)
                    .show()
            }
            else -> {
                window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                progress_bar_login.visibility = View.VISIBLE
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        progress_bar_login.visibility = View.GONE
                        val i = Intent(this@LoginActivity, MainActivity::class.java)
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(i)
                        finish()
                    } else {
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        progress_bar_register.visibility = View.GONE
                        Toast.makeText(
                            this@LoginActivity,
                            "Error Message: " + task.exception?.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}