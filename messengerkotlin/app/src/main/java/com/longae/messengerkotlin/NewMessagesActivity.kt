package com.longae.messengerkotlin

import android.content.Intent
import android.os.Bundle
import android.provider.Contacts.SettingsColumns.KEY
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_new_messages.*
import kotlinx.android.synthetic.main.user_new_message.view.*
import kotlinx.coroutines.*

class NewMessagesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_messages)
        supportActionBar?.title = "Select User" // Rename title
        fetchUsers()
    }
    companion object{
        const val USER_KEY = "USER_KEY"
    }
    private fun fetchUsers() {
        val ref = Firebase.database.reference.child("users")
        ref.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val adapter = GroupAdapter<GroupieViewHolder>()
                snapshot.children.forEach{
                    val user = it.getValue(User::class.java)
                    if (user != null){
                        adapter.add(UserItem(user))
                    }
                }
                adapter.setOnItemClickListener { item, view ->
                    val userItem = item as UserItem
                    val intent = Intent(view.context,chatlogActivity::class.java)

                    intent.putExtra(USER_KEY,userItem.user)
                    startActivity(intent)

                    finish()
                }
                recyclerView_newmessage.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                /////
            }
        })
    }
}
class UserItem(val user:User): Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        GlobalScope.launch(Dispatchers.Main){
            async(Dispatchers.IO){viewHolder.itemView.textviewUsernameNewmessage
                                .text = user.username}.await()
            withContext(Dispatchers.Main){Picasso.get().load(user.profileImageUrl)
                         .into(viewHolder.itemView.imageView_new_massage)}
        }
    }

    override fun getLayout(): Int {
        return R.layout.user_new_message
    }

}