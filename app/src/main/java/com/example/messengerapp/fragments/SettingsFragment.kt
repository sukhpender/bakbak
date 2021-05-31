package com.example.messengerapp.fragments

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.messengerapp.R
import com.example.messengerapp.model_classes.Users
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_settings.view.*


class SettingsFragment : Fragment() {

    private var usersReference: DatabaseReference? = null
    private var firebaseUser: FirebaseUser? = null
    private val RequestCode = 438
    private var imageUri: Uri? = null
    private var storageRef: StorageReference? = null
    private var socialChecker: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        firebaseUser = FirebaseAuth.getInstance().currentUser
        usersReference = FirebaseDatabase.getInstance().reference.child("Users")
            .child(firebaseUser?.uid.toString())
        storageRef = FirebaseStorage.getInstance().reference.child("User Images")

        usersReference?.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user: Users? = snapshot.getValue(Users::class.java)
                    Log.e("username", user?.getUsername().toString())

                    if (context != null) {
                        /*if (user?.getCover()!!.isEmpty()) {
                            view.cover_image_setting.setImageResource(R.drawable.register)
                        } else{
                            Picasso.get().load(user?.getCover()).into(view.cover_image_setting)
                        }*/
                        view?.username_settings?.text = user?.getUsername()

                        if (user?.getProfile()!!.isEmpty()) {
                            view.profile_image_setting.setImageResource(R.drawable.profile_place)
                        } else {
                            Picasso.get().load(user.getProfile()).into(view.profile_image_setting)
                        }

                    }
                }
            }
        })

        view.profile_image_setting.setOnClickListener {
            pickImage()
        }
        view.image_set_facebook.setOnClickListener {
            socialChecker = "facebook"
            setSocialLink()
        }
        view.image_set_instagram.setOnClickListener {
            socialChecker = "instagram"
            setSocialLink()
        }
        view.image_set_website.setOnClickListener {
            socialChecker = "website"
            setSocialLink()
        }
        return view
    }

    private fun setSocialLink() {
        val builder: AlertDialog.Builder =
            AlertDialog.Builder(context, R.style.Theme_AppCompat_DayNight_Dialog_Alert)

        if (socialChecker == "website") {
            builder.setTitle("Write URL")
        } else {
            builder.setTitle("Write Username")
        }

        val editText = EditText(context)

        if (socialChecker == "website") {
            editText.hint = "e.g www.google.com"
        } else {
            editText.hint = "e.g naveen123"
        }
        builder.setView(editText)
        builder.setPositiveButton("Create", DialogInterface.OnClickListener { dialog, which ->
            var str = editText.text.toString()
            if (str == "") {
                Toast.makeText(context, "Please Write Something...", Toast.LENGTH_SHORT).show()
            } else {
                saveSocialLink(str)
            }
        })
        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->
            dialog.cancel()
        })
        val dialog: AlertDialog = builder.create()
        dialog.show()

        val b: Button = dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
        val c: Button = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
        b.setTextColor(context!!.resources.getColor(R.color.colorPrimaryDark))
        c.setTextColor(context!!.resources.getColor(R.color.colorPrimaryDark))
    }

    private fun saveSocialLink(str: String) {
        val mapSocial = HashMap<String, Any>()

        when (socialChecker) {
            "facebook" -> {
                mapSocial["facebook"] = "https://m.facebook.com/$str"
            }
            "instagram" -> {
                mapSocial["instagram"] = "https://m.facebook.com/$str"
            }
            "website" -> {
                mapSocial["facebook"] = "https://$str"
            }
        }
        usersReference?.updateChildren(mapSocial)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "Updated Successfully", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun pickImage() {
        val i = Intent()
        i.type = "image/*"
        i.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(i, RequestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RequestCode && resultCode == Activity.RESULT_OK && data?.data != null) {
            imageUri = data.data
            Toast.makeText(context, "uploading...", Toast.LENGTH_SHORT).show()
            uploadImageToDatabase()
        }
    }

    private fun uploadImageToDatabase() {
        val progressBar = ProgressDialog(context)
        progressBar.setMessage("image is uploading, please wait...")
        progressBar.show()
        if (imageUri != null) {
            val fileRef = storageRef?.child(System.currentTimeMillis().toString() + ".jpg")

            val uploadTask: StorageTask<*>
            uploadTask = fileRef?.putFile(imageUri!!)!!
            uploadTask?.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->

                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation fileRef.downloadUrl
            })?.addOnCompleteListener { task ->
                val downloadUrl = task.result
                val url = downloadUrl.toString()

                val mapProfileImg = HashMap<String, Any>()
                mapProfileImg["profile"] = url
                usersReference?.updateChildren(mapProfileImg)

                progressBar.dismiss()

            }
        }
    }
}