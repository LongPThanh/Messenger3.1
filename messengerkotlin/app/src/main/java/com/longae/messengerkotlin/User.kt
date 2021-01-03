package com.longae.messengerkotlin

data class User(val uid:String, val username:String, val profileImageUrl:String) {
        constructor():this("","","")
}