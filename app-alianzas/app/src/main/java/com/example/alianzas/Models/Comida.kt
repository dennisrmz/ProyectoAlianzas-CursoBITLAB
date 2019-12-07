package com.example.alianzas.Models

data class Comida(val Date: String?, val Description: String?, val ImageURL: String?, val Title: String?) {
    constructor() : this("", "", "", "")
}


