package com.example.chartaplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ChartActivity : AppCompatActivity() {


private lateinit var chartRecyclerView: RecyclerView
private lateinit var messageBox:EditText
private lateinit var sentButton:ImageView
private lateinit var messageAdapter: MessageAdapter
private lateinit var messageList: ArrayList<message>
private lateinit var mDbRef : DatabaseReference

var receiverRoom:String?=null
    var senderRoom:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart)


        val name= intent.getStringExtra("name")
        var receiverUid= intent.getStringExtra("uid")
        var senderUid = FirebaseAuth.getInstance().currentUser?.uid
        mDbRef = FirebaseDatabase.getInstance().getReference()

        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid +receiverUid


        supportActionBar?.title =name


        chartRecyclerView =findViewById(R.id.chartRecyclerView)
        messageBox= findViewById(R.id.messageBox)
        sentButton=findViewById(R.id.sentButton)
        messageList = ArrayList()
        messageAdapter = MessageAdapter(this,messageList)
        chartRecyclerView.layoutManager = LinearLayoutManager(this)
        chartRecyclerView.adapter =messageAdapter
//        This is for adding data to recyclerView
        mDbRef.child("charts").child(senderRoom!!).child("messages")
            .addValueEventListener(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    messageList.clear()
                    for (postSnapshot in snapshot.children){
                        val message =postSnapshot.getValue(message::class.java)
                        messageList.add(message!!)
                    }
                    messageAdapter.notifyDataSetChanged()

                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

//        This is for adding message to the database.

        sentButton.setOnClickListener{
            val message =messageBox.text.toString()
            val messageObjects = Message(message,senderUid)
            mDbRef.child("chart").child(senderRoom!!).child("messages").push()
                .setValue(messageObjects).addOnSuccessListener {
                    mDbRef.child("chart").child(receiverRoom!!).child("messages").push()
                        .setValue(messageObjects)
                }
            messageBox.setText("")

        }
    }
//    This is not in code Note

    private fun Message(message: String, senderUid: String?) {

    }
}