package com.example.aplicacion_calculadora_concretos.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DaoResultadoMorteros {

    //Insert
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertarResultadoMorteros(resultadoMorterosTabla: ResultadoMorterosTabla)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertarResultadosMorteros(resultados: List<ResultadoMorterosTabla>)

    //Select nombres
    @Query("SELECT nombre_resultado_morteros FROM ResultadoMorterosTabla WHERE user_id_morteros = :userIDMorteros ")
    fun obtenerSoloNombresResultadoMorteros(userIDMorteros: String): List<String>

    //Select
    @Query("SELECT nombre_resultado_morteros FROM ResultadoMorterosTabla WHERE nombre_resultado_morteros = :nombreResultadoMorteros")
    fun obtenerNombreResultadoMorteros(nombreResultadoMorteros: String): String

    @Query("SELECT cemento_resultado_morteros FROM ResultadoMorterosTabla WHERE nombre_resultado_morteros = :nombreResultadoMorteros")
    fun obtenerCementoResultadoMorteros(nombreResultadoMorteros: String): Double

    @Query("SELECT arena_resultado_morteros FROM ResultadoMorterosTabla WHERE nombre_resultado_morteros = :nombreResultadoMorteros")
    fun obtenerArenaResultadoMorteros(nombreResultadoMorteros: String): Double

    @Query("SELECT agua_resultado_morteros FROM ResultadoMorterosTabla WHERE nombre_resultado_morteros = :nombreResultadoMorteros")
    fun obtenerAguaResultadoMorteros(nombreResultadoMorteros: String): Double

    //Select *
    @Query("SELECT * FROM ResultadoMorterosTabla")
    fun obtenerTodosResultadoMorteros(): Flow<List<ResultadoMorterosTabla>>

    //Update
    @Query("UPDATE ResultadoMorterosTabla SET nombre_resultado_morteros = :nombreNuevo WHERE nombre_resultado_morteros = :nombreViejo")
    fun actualizarNombreResultadoMorteros(nombreViejo: String, nombreNuevo: String)

    //Delete
    @Query("DELETE FROM ResultadoMorterosTabla WHERE nombre_resultado_morteros = :nombreResultadoMorteros")
    fun eliminarResultadoMorteros(nombreResultadoMorteros: String)

    @Query("DELETE FROM ResultadoMorterosTabla")
    fun eliminarTodosResultadoMorteros()

    @Query("DELETE FROM ResultadoMorterosTabla WHERE user_id_morteros = :userIDMorteros")
    fun eliminarResultadoMorterosID(userIDMorteros: String)

    @Query("DELETE FROM ResultadoMorterosTabla WHERE user_id_morteros IS NOT NULL AND user_id_morteros != ''")
    fun eliminarTodosResultadoMorterosIngresados()
}