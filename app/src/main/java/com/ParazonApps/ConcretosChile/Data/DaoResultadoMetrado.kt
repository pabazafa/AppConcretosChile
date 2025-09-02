package com.example.aplicacion_calculadora_concretos.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DaoResultadoMetrado {

    //Insert
    @Insert
    fun insertarResultadoMetrado(resultadoMetradoTabla: ResultadoMetradoTabla)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertarResultadosMetrado(resultados: List<ResultadoMetradoTabla>)

    //Select nombres
    @Query("SELECT nombre_resultado_metrado FROM ResultadoMetradoTabla WHERE user_id_metrado = :userIDMetrado")
    fun obtenerSoloNombresResultadoMetrado(userIDMetrado: String): List<String>

    //Select
    @Query("SELECT nombre_resultado_metrado FROM resultadometradotabla WHERE nombre_resultado_metrado = :nombreResultadoMetrado")
    fun obtenerNombreResultadoMetrado(nombreResultadoMetrado: String): String

    @Query("SELECT cemento_resultado_metrado FROM ResultadoMetradoTabla WHERE nombre_resultado_metrado = :nombreResultadoMetrado")
    fun obtenerCementoResultadoMetrado(nombreResultadoMetrado: String): Double

    @Query("SELECT arena_resultado_metrado FROM ResultadoMetradoTabla WHERE nombre_resultado_metrado = :nombreResultadoMetrado")
    fun obtenerArenaResultadoMetrado(nombreResultadoMetrado: String): Double

    @Query("SELECT gravilla_resultado_metrado FROM ResultadoMetradoTabla WHERE nombre_resultado_metrado = :nombreResultadoMetrado")
    fun obtenerGravillaResultadoMetrado(nombreResultadoMetrado: String): Double

    @Query("SELECT agua_resultado_metrado FROM ResultadoMetradoTabla WHERE nombre_resultado_metrado = :nombreResultadoMetrado")
    fun obtenerAguaResultadoMetrado(nombreResultadoMetrado: String): Double

    //Select *
    @Query("SELECT * FROM ResultadoMetradoTabla")
    fun obtenerTodosResultadoMetrado(): Flow<List<ResultadoMetradoTabla>>

    //Update
    @Query("UPDATE ResultadoMetradoTabla SET nombre_resultado_metrado = :nombreNuevo WHERE nombre_resultado_metrado = :nombreViejo")
    fun actualizarNombreResultadoMetrado(nombreViejo: String, nombreNuevo: String)

    //Delete

    @Query("DELETE FROM ResultadoMetradoTabla WHERE nombre_resultado_metrado = :nombreResultadoMetrado")
    fun eliminarResultadoMetrado(nombreResultadoMetrado: String)

    @Query("DELETE FROM ResultadoMetradoTabla")
    fun eliminarTodosResultadoMetrado()

    @Query("DELETE FROM ResultadoMetradoTabla WHERE user_id_metrado IS NOT NULL AND user_id_metrado != ''")
    fun eliminarTodosResultadoMetradoIngresados()
}