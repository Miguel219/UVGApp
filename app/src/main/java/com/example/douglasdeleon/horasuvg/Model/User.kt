package com.example.douglasdeleon.horasuvg.Model

data class User (var name: String, var email: String, var type: Int,var userId:String) {
    constructor() : this("","",0,"")
}