package com.example.messengerapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.messengerapp.auth.LoginActivity
import com.example.messengerapp.auth.RegisterActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_splash.*

class WelcomeActivity : AppCompatActivity(),View.OnClickListener {

    var firebaseUSer: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        onClick()
    }

    override fun onStart() {
        super.onStart()

        firebaseUSer = FirebaseAuth.getInstance().currentUser
        if (firebaseUSer != null){
            val i = Intent(this@WelcomeActivity,MainActivity::class.java)
            startActivity(i)
            finish()
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_register_welcome -> {
                val i = Intent(this@WelcomeActivity,RegisterActivity::class.java)
                startActivity(i)
                finish()
            }

            R.id.btn_login_welcome -> {
                val i = Intent(this@WelcomeActivity,LoginActivity::class.java)
                startActivity(i)
                finish()
            }
        }
    }

    private fun onClick(){
        btn_register_welcome.setOnClickListener(this)
        btn_login_welcome.setOnClickListener(this)
    }
}