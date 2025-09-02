package com.example.aplicacion_calculadora_concretos.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DaoResultadoFC {

    //Insert
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertarResultadoFC(resultadoFCTabla: ResultadoFCTabla)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertarResultadosFC(resultados: List<ResultadoFCTabla>)

    //Select nombres
    @Query("SELECT nombre_resultado_fc FROM ResultadoFCTabla WHERE user_id_fc = :userIDFC")
    fun obtenerSoloNombresResultadoFC(userIDFC: String): List<String>

    //Select
    @Query("SELECT nombre_resultado_fc FROM ResultadoFCTabla WHERE nombre_resultado_fc = :nombreResultadoFC")
    fun obtenerNombreResultadoFC(nombreResultadoFC: String): String

    @Query("SELECT cemento_resultado_fc FROM ResultadoFCTabla WHERE nombre_resultado_fc = :nombreResultadoFC")
    fun obtenerCementoResultadoFC(nombreResultadoFC: String): Double

    @Query("SELECT arena_resultado_fc FROM ResultadoFCTabla WHERE nombre_resultado_fc = :nombreResultadoFC")
    fun obtenerArenaResultadoFC(nombreResultadoFC: String): Double

    @Query("SELECT gravilla_resultado_fc FROM ResultadoFCTabla WHERE nombre_resultado_fc = :nombreResultadoFC")
    fun obtenerGravillaResultadoFC(nombreResultadoFC: String): Double

    @Query("SELECT agua_resultado_fc FROM ResultadoFCTabla WHERE nombre_resultado_fc = :nombreResultadoFC")
    fun obtenerAguaResultadoFC(nombreResultadoFC: String): Double

    //Select *
    @Query("SELECT * FROM ResultadoFCTabla")
    fun obtenerTodosResultadoFC(): Flow<List<ResultadoFCTabla>>

    //Update
    @Query("UPDATE ResultadoFCTabla SET nombre_resultado_fc = :nombreNuevo WHERE nombre_resultado_fc = :nombreViejo")
    fun actualizarNombreResultadoFC(nombreViejo: String, nombreNuevo: String)

    //Delete
    @Query("DELETE FROM ResultadoFCTabla WHERE nombre_resultado_fc = :nombreResultadoFC")
    fun eliminarResultadoFC(nombreResultadoFC: String)

    @Query("DELETE FROM ResultadoFCTabla WHERE user_id_fc = :userIDFC")
    fun eliminarResultadoFCID(userIDFC: String)

    @Query("DELETE FROM ResultadoFCTabla")
    fun eliminarTodosResultadoFC()

    @Query("DELETE FROM ResultadoFCTabla WHERE user_id_fc IS NOT NULL AND user_id_fc != ''")
    fun eliminarTodosResultadoFCIngresados()


}