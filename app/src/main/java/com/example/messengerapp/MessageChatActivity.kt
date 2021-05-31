package com.example.messengerapp

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.messengerapp.adapters.ChatsAdapter
import com.example.messengerapp.model_classes.Chat
import com.example.messengerapp.model_classes.Users
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_message_chat.*

class MessageChatActivity : AppCompatActivity() {

    private var userIdVisit: String = ""
    private var firebaseUser: FirebaseUser? = null
    private var chatsAdapter: ChatsAdapter? = null
    var mChatList: List<Chat>? = null
    private var reference: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_chat)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_messagechat)
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            val i = Intent(this@MessageChatActivity, WelcomeActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(i)
            finish()
        }

        intent = intent
        userIdVisit = intent.getStringExtra("visit_id").toString()
        firebaseUser = FirebaseAuth.getInstance().currentUser

        rv_message_chat.setHasFixedSize(true)
        var linearLayoutManager = LinearLayoutManager(applicationContext)
        linearLayoutManager.stackFromEnd = true
        rv_message_chat.layoutManager = linearLayoutManager

        reference = FirebaseDatabase.getInstance().reference.child("Users")
            .child(userIdVisit)
        reference?.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val user: Users? = snapshot.getValue(Users::class.java)
                username_chat.text = user?.getUsername()
                Picasso.get().load(user?.getProfile()).into(profile_image_message_chat)

                retriveMessage(firebaseUser!!.uid, userIdVisit, user!!.getProfile())
            }

        })

        btn_send_message_chat.setOnClickListener {
            val message = edt_chat_typed_message.text.toString()

            if (message == "") {
                Toast.makeText(
                    this@MessageChatActivity,
                    "Please write a message first",
                    Toast.LENGTH_SHORT
                ).show()

            } else {
                sendMessageToUser(firebaseUser!!.uid, userIdVisit, message)
            }
            edt_chat_typed_message.setText("")
        }

        attach_image_file.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(Intent.createChooser(intent, "Pick image"), 438)
        }

        seenMessage(userIdVisit)
    }

    private fun sendMessageToUser(sender_id: String, receiver_id: String, message: String) {

        val reference = FirebaseDatabase.getInstance().reference
        val messageKey = reference.push().key

        val messageHashMap = HashMap<String, Any?>()

        messageHashMap["sender"] = sender_id
        messageHashMap["message"] = message
        messageHashMap["receiver"] = receiver_id
        messageHashMap["isseen"] = false
        messageHashMap["url"] = ""
        messageHashMap["messageId"] = messageKey

        reference.child("Chats").child(messageKey!!).setValue(messageHashMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val chatsListReference =
                        FirebaseDatabase.getInstance().reference.child("ChatList")
                            .child(firebaseUser!!.uid)
                            .child(userIdVisit)
                    chatsListReference.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(error: DatabaseError) {

                        }

                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (!snapshot.exists()) {
                                chatsListReference.child("id").setValue(userIdVisit)
                            }
                        }

                    })

                    val chatsListReceiverReference =
                        FirebaseDatabase.getInstance().reference.child("ChatList")
                            .child(userIdVisit)
                            .child(firebaseUser!!.uid)
                    chatsListReceiverReference.child("id").setValue(firebaseUser!!.uid)

                    //implement the push notification using fcm
                    val reference = FirebaseDatabase.getInstance().reference.child("Users")
                        .child(firebaseUser!!.uid)


                } else {

                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 438 && resultCode == RESULT_OK && data != null && data.data != null) {
            val progressBar = ProgressDialog(this)
            progressBar.setMessage("image is uploading, please wait...")
            progressBar.show()

            val fileUri = data.data
            val storageReference = FirebaseStorage.getInstance().reference.child("Chat Images")
            val ref = FirebaseDatabase.getInstance().reference
            val messageId = ref.push().key
            val filePath = storageReference.child("$messageId.jpg")

            val uploadTask: StorageTask<*>
            uploadTask = filePath.putFile(fileUri!!)
            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->

                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation filePath.downloadUrl
            }).addOnCompleteListener { task ->
                val downloadUrl = task.result
                val url = downloadUrl.toString()
                val messageHashMap = HashMap<String, Any?>()

                messageHashMap["sender"] = firebaseUser?.uid
                messageHashMap["message"] = "sent you an image"
                messageHashMap["receiver"] = userIdVisit
                messageHashMap["isseen"] = false
                messageHashMap["url"] = url
                messageHashMap["messageId"] = messageId

                ref.child("Chats").child(messageId!!).setValue(messageHashMap)
                progressBar.dismiss()
            }
        }
    }

    private fun retriveMessage(senderId: String, receiverId: String, receiverImageUrl: String) {
        mChatList = ArrayList()
        val reference = FirebaseDatabase.getInstance().reference.child("Chats")

        reference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                (mChatList as ArrayList<Chat>).clear()

                for (snap in snapshot.children) {
                    val chat = snap.getValue(Chat::class.java)

                    if (chat?.getReceiver().equals(senderId) && chat?.getSender().equals(receiverId)
                        || chat?.getReceiver().equals(receiverId) && chat?.getSender()
                            .equals(senderId)
                    ) {
                        (mChatList as ArrayList<Chat>).add(chat!!)
                    }
                    chatsAdapter = ChatsAdapter(
                        this@MessageChatActivity,
                        (mChatList as ArrayList<Chat>),
                        receiverImageUrl
                    )
                    rv_message_chat.adapter = chatsAdapter
                    chatsAdapter?.notifyDataSetChanged()
                }
            }
        })
    }

    var seenListener: ValueEventListener? = null

    private fun seenMessage(userId: String){
        val ref = FirebaseDatabase.getInstance().reference.child("Chats")

        seenListener = ref?.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                for (snap in snapshot.children){
                    val chat = snap.getValue(Chat::class.java)

                    if (chat!!.getReceiver().equals(firebaseUser!!.uid) && chat!!.getSender().equals(userId)){
                        val hashMap = HashMap<String,Any>()
                        hashMap["isseen"] = true
                        snap.ref.updateChildren(hashMap)
                    }
                }
            }
        })
    }

    override fun onPause() {
        super.onPause()

        reference?.removeEventListener(seenListener!!)
    }
}