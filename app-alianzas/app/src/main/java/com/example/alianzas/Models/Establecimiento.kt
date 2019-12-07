package com.example.alianzas.Models

data class Establecimiento(val Description: String?, val ImageURL: String?, val Query: String?, val Title: String?, val Child: String?){
    constructor() : this("", "", "", "", "")
}