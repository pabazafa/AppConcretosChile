package com.example.aplicacion_calculadora_concretos.data

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class ResultadoMetradoTabla (
    @PrimaryKey(autoGenerate = false)
    val nombre_resultado_metrado : String,
    val cemento_resultado_metrado : Double,
    val arena_resultado_metrado : Double,
    val gravilla_resultado_metrado : Double,
    val agua_resultado_metrado : Double,
    val user_id_metrado : String
)