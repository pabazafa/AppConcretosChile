package com.example.aplicacion_calculadora_concretos.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ParazonApps.ConcretosChile.Resultados.ResultadoHormigon
import kotlinx.coroutines.flow.Flow

@Dao
interface DaoResultadoHormigon {

    //Insert
    @Insert
    fun insertarResultadoHormigon(resultadoHormigonTabla: ResultadoHormigonTabla)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertarResultadosHormigon(resultados: List<ResultadoHormigonTabla>)

    //Select nombres
    @Query("SELECT nombre_resultado_hormigon FROM ResultadoHormigonTabla WHERE user_id_hormigon = :userIDHormigon")
    fun obtenerSoloNombresResultadoHormigon(userIDHormigon: String): List<String>

    //Select
    @Query("SELECT nombre_resultado_hormigon FROM ResultadoHormigonTabla WHERE nombre_resultado_hormigon = :nombreResultadoHormigon")
    fun obtenerNombreResultadoHormigon(nombreResultadoHormigon: String) :String

    @Query("SELECT cemento_resultado_hormigon FROM ResultadoHormigonTabla WHERE nombre_resultado_hormigon = :nombreResultadoHormigon")
    fun obtenerCementoResultadoHormigon(nombreResultadoHormigon: String): Double

    @Query("SELECT hormigon_resultado_hormigon FROM ResultadoHormigonTabla WHERE nombre_resultado_hormigon = :nombreResultadoHormigon")
    fun obtenerHormigonResultadoHormigon(nombreResultadoHormigon: String): Double

    @Query("SELECT agua_resultado_hormigon FROM ResultadoHormigonTabla WHERE nombre_resultado_hormigon = :nombreResultadoHormigon")
    fun obtenerAguaResultadoHormigon(nombreResultadoHormigon: String): Double

    //Select *
    @Query("SELECT * FROM ResultadoHormigonTabla")
    fun obtenerTodosResultadoHormigon(): Flow<List<ResultadoHormigonTabla>>

    //Update
    @Query("UPDATE ResultadoHormigonTabla SET nombre_resultado_hormigon = :nombreNuevo WHERE nombre_resultado_hormigon = :nombreViejo")
    fun actualizarNombreResultadoHormigon(nombreViejo: String, nombreNuevo: String)

    //Delete
    @Query("DELETE FROM ResultadoHormigonTabla WHERE nombre_resultado_hormigon = :nombreResultadoHormigon")
    fun eliminarResultadoHormigon(nombreResultadoHormigon: String)

    @Query("DELETE FROM ResultadoHormigonTabla")
    fun eliminarTodosResultadoHormigon()

    @Query("DELETE FROM ResultadoHormigonTabla WHERE user_id_hormigon IS NOT NULL AND user_id_hormigon != ''")
    fun eliminarTodosResultadoHormigonIngresados()
}