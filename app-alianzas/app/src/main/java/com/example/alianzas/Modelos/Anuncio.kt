package com.example.alianzas.Modelos

//cambian data class para real time
data class Anuncio (val Nombre: String?, val Fecha: String?, val Imagen: String?, var Detalle: String?){
    constructor() : this("", "", "","")
}