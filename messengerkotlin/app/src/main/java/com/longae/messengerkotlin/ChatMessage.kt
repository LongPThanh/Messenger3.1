package com.longae.messengerkotlin

class ChatMessage(val id:String, val text:String,
                  val fromID:String,val toId:String,val timeStamp:Long){
    constructor():this("","","","",-1)
}