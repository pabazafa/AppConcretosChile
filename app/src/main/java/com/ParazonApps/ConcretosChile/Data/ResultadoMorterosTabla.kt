package com.example.aplicacion_calculadora_concretos.data

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class ResultadoMorterosTabla (
    @PrimaryKey(autoGenerate = false)
    val nombre_resultado_morteros : String,
    val cemento_resultado_morteros : Double,
    val arena_resultado_morteros : Double,
    val agua_resultado_morteros : Double,
    val user_id_morteros : String
)