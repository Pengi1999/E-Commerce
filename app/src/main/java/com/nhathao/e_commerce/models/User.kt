package com.nhathao.e_commerce.models

import java.io.Serializable


class User(
    var userAccountName: String,
    var userPWD: String,
    var userName:String,
    var secretCode:String,
    var birthday:String? = "",
    var avatar:String? = "",
    var email:String? =""
) : Serializable {
}