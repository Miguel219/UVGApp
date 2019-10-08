package com.example.douglasdeleon.horasuvg.Model

import org.w3c.dom.Comment

data class Problem (var name: String, var description: String, var place: String, var date: String, var assigned: Boolean, var hours:String, var volunteers:String, var problemId:String, var adminId:String, var cupo:String, var id:String, var solved:String,var problemClass: String,var comments: String, var solutionId:String,var solutionComments:String) {
    constructor() : this("","","","",false,"","","","","","","","","","","")
}