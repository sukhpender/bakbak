package com.example.messengerapp.simpleBase

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.messengerapp.R
import com.example.messengerapp.simpleBase.listener.KotlinBaseListener

class KotlinBaseActivity : AppCompatActivity(), KotlinBaseListener {

    private var dialogShow: Boolean? = false
    private var progress: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

        initDialog() //for the circular progress loader
    }

    //to show the toast message call this method
    fun showToast(string: String) {
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show()
    }

    //progress loader initialization
    private fun initDialog() {
        progress = Dialog(this)
        progress?.setContentView(R.layout.progress_layout)
        progress?.setCancelable(false)
        progress?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    //to show the progress loader call this method
    override fun showProgress() {
        try {
            hideProgress()
            dialogShow = true
            progress?.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //to hide the progress loader call this method
    override fun hideProgress() {
        try {
            if (this.dialogShow == true) {
                progress?.dismiss()
                dialogShow = false
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}