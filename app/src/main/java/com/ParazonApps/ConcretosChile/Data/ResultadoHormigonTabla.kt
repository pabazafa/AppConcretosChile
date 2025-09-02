package com.example.aplicacion_calculadora_concretos.data

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class ResultadoHormigonTabla (
    @PrimaryKey(autoGenerate = false)
    val nombre_resultado_hormigon : String,
    val cemento_resultado_hormigon : Double,
    val hormigon_resultado_hormigon : Double,
    val agua_resultado_hormigon : Double,
    var user_id_hormigon : String
)