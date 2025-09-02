package com.example.aplicacion_calculadora_concretos.data

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class ResultadoVolumenTabla (
    @PrimaryKey(autoGenerate = false)
    val nombre_resultado_volumen : String,
    val cemento_resultado_volumen : Double,
    val arena_resultado_volumen : Double,
    val gravilla_resultado_volumen : Double,
    val agua_resultado_volumen : Double,
    val user_id_volumen : String
)