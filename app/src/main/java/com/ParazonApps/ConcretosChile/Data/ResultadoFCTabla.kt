package com.example.aplicacion_calculadora_concretos.data

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class ResultadoFCTabla (
    @PrimaryKey(autoGenerate = false)
    var nombre_resultado_fc : String,
    val cemento_resultado_fc : Double,
    val arena_resultado_fc : Double,
    val gravilla_resultado_fc : Double,
    val agua_resultado_fc : Double,
    var user_id_fc : String
)