package com.example.douglasdeleon.horasuvg.Model

data class User (var name: String, var email: String, var type: Int, var hours: Int,var userId:String,var problemsSolved: Int) {
    constructor() : this("","",0,0,"",0)
}