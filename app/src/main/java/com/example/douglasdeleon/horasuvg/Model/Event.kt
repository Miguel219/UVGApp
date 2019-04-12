package com.example.douglasdeleon.horasuvg.Model

data class Event (var name: String, var description: String, var place: String, var date: String, var assigned: Boolean,var hours:String,var volunteers:String,var eventId:String) {
    constructor() : this("","","","",false,"","","")
}