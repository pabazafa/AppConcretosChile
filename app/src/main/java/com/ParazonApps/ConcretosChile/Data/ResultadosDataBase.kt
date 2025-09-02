package com.example.aplicacion_calculadora_concretos.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ResultadoFCTabla::class, ResultadoVolumenTabla::class, ResultadoHormigonTabla::class, ResultadoMetradoTabla::class, ResultadoMorterosTabla::class], version = 6, exportSchema = false)
abstract class ResultadosDataBase : RoomDatabase() {

    abstract val daoResultadoFC : DaoResultadoFC
    abstract val daoResultadoVolumen : DaoResultadoVolumen
    abstract val daoResultadoHormigon : DaoResultadoHormigon
    abstract val daoResultadoMetrado : DaoResultadoMetrado
    abstract val daoResultadoMorteros : DaoResultadoMorteros

    companion object{

        @Volatile
        private var INSTANCE: ResultadosDataBase? = null

        fun getInstance(context: Context) : ResultadosDataBase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        ResultadosDataBase::class.java,
                        "resultados_data_base"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}