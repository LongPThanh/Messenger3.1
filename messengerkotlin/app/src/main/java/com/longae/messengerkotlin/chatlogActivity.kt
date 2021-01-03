package com.longae.messengerkotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chatlog.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*

class chatlogActivity : AppCompatActivity() {
    companion object{
        val TAG = "Chat Log"
    }
    val adapter = GroupAdapter<GroupieViewHolder>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatlog)

        recyclerView_ChatLog.adapter = adapter


//        val username = intent.getStringExtra(NewMessagesActivity.USER_KEY)
        val user = intent.getParcelableExtra<User>(NewMessagesActivity.USER_KEY)
        supportActionBar?.title = user?.username
//        setupDummyData()
        listenForMessages()
        button_chat_log.setOnClickListener {
            Log.d(TAG,"attemp to send message")
            perFormSendMessage()
        }
    }

    private fun listenForMessages() {
        val reference = Firebase.database.reference.child("messages")

        reference.addChildEventListener(object:ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)
                if(chatMessage!= null){
                    Log.d(TAG, chatMessage.text)
                    if(chatMessage.fromID == Firebase.auth.uid){
                        adapter.add(ChatFromItem(chatMessage.text))
                    }else{
                        adapter.add(ChatToItem(chatMessage.text))
                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        } )
    }

    private fun perFormSendMessage() {
        val user = intent.getParcelableExtra<User>(NewMessagesActivity.USER_KEY)
        val text = edittext_chat_log.text.toString()
        val fromID = Firebase.auth.uid.toString()
        val toId = user?.uid
        if(fromID == null)return
        val reference = Firebase.database.reference.child("messages").push()
        val chatmessage =
            toId?.let {
                reference.key?.let { it1 ->
                    ChatMessage(
                        it1,text,fromID, it,
                        System.currentTimeMillis()/1000)
                }
            }
        reference.setValue(chatmessage).addOnCompleteListener {
            Log.d(TAG,"Saved our chat message: ${reference.key}")
        }
    }

    private fun setupDummyData() {
        val adapter = GroupAdapter<GroupieViewHolder>()

        adapter.add(ChatToItem("text"))
        adapter.add(ChatFromItem("text"))
        recyclerView_ChatLog.adapter = adapter
    }
}
class ChatFromItem(val text:String): Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            viewHolder.itemView.textView_from_row.text = text
    }

    override fun getLayout(): Int {
        return  R.layout.chat_from_row

    }

}
class ChatToItem(val text:String): Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.textView_to_row.text = text
    }

    override fun getLayout(): Int {
        return  R.layout.chat_to_row

    }

}