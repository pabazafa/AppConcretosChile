package com.example.aplicacion_calculadora_concretos.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DaoResultadoVolumen {

    //Insert
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertarResultadoVolumen(resultadoVolumenTabla: ResultadoVolumenTabla)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertarResultadosVolumen(resultados: List<ResultadoVolumenTabla>)

    //Select nombres
    @Query("SELECT nombre_resultado_volumen FROM ResultadoVolumenTabla WHERE user_id_volumen = :userIDVolumen")
    fun obtenerSoloNombresResultadoVolumen(userIDVolumen: String): List<String>

    //Select
    @Query("SELECT nombre_resultado_volumen FROM ResultadoVolumenTabla WHERE nombre_resultado_volumen = :nombreResultadoVolumen")
    fun obtenerNombreResultadoVolumen(nombreResultadoVolumen: String): String

    @Query("SELECT cemento_resultado_volumen FROM ResultadoVolumenTabla WHERE nombre_resultado_volumen = :nombreResultadoVolumen")
    fun obtenerCementoResultadoVolumen(nombreResultadoVolumen: String): Double

    @Query("SELECT arena_resultado_volumen FROM ResultadoVolumenTabla WHERE nombre_resultado_volumen = :nombreResultadoVolumen")
    fun obtenerArenaResultadoVolumen(nombreResultadoVolumen: String): Double

    @Query("SELECT gravilla_resultado_volumen FROM ResultadoVolumenTabla WHERE nombre_resultado_volumen = :nombreResultadoVolumen")
    fun obtenerGravillaResultadoVolumen(nombreResultadoVolumen: String): Double

    @Query("SELECT agua_resultado_volumen FROM ResultadoVolumenTabla WHERE nombre_resultado_volumen = :nombreResultadoVolumen")
    fun obtenerAguaResultadoVolumen(nombreResultadoVolumen: String): Double

    //Select *
    @Query("SELECT * FROM ResultadoVolumenTabla")
    fun obtenerTodosResultadoVolumen(): Flow<List<ResultadoVolumenTabla>>

    //Update
    @Query("UPDATE ResultadoVolumenTabla SET nombre_resultado_volumen = :nombreNuevo WHERE nombre_resultado_volumen = :nombreViejo")
    fun actualizarNombreResultadoVolumen(nombreViejo: String, nombreNuevo: String)

    //Delete
    @Query("DELETE FROM ResultadoVolumenTabla WHERE nombre_resultado_volumen = :nombreResultadoVolumen")
    fun eliminarResultadoVolumen(nombreResultadoVolumen: String)

    @Query("DELETE FROM ResultadoVolumenTabla")
    fun eliminarTodosResultadoVolumen()

    @Query("DELETE FROM ResultadoVolumenTabla WHERE user_id_volumen = :userIDVolumen")
    fun eliminarResultadoVolumenID(userIDVolumen: String)

    @Query("DELETE FROM ResultadoVolumenTabla WHERE user_id_volumen IS NOT NULL AND user_id_volumen != ''")
    fun eliminarTodosResultadoVolumenIngresados()
}