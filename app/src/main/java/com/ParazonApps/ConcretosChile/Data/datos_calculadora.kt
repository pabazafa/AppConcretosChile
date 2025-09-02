package com.example.aplicacion_calculadora_concretos.data

//FC TABLA 1

data class fc_metrado(
    var resistencia: String,
    var cemento: Double,
    var arena: Double,
    var piedra: Double,
    var agua: Double
)

object fc_metrado_datos {
    var datos_metrado = arrayOf(
        fc_metrado("100", 5.69, 0.484, 0.806, 0.171),
        fc_metrado("140", 7.01, 0.51, 0.64, 0.184),
        fc_metrado("175", 8.43, 0.49, 0.61, 0.184),
        fc_metrado("210", 9.73, 0.48, 0.60, 0.185),
        fc_metrado("245", 11.50, 0.45, 0.58, 0.187),
        fc_metrado("280", 13.34, 0.40, 0.58, 0.188),
        fc_metrado("Firmes, plantillas", 5.0, 30.0, 35.0, 13.5),
        fc_metrado("Dalas, castillos", 6.0, 29.0, 34.5, 13.5),
        fc_metrado("Losas de entrepiso", 7.0, 28.5, 34.5, 12.70),
        fc_metrado("Columnas y trabes", 8.0, 28.5, 34.0, 12.50),

        )
}


//VOLUMENES

data class volumenes(
    var resistencia: String,
    var cemento: Double,
    var agregado_fino: Double,
    var agregado_grueso: Double,
    var agua: Double
)

object volumenes_datos {
    var datos_volumenes = arrayOf(
        volumenes("1:2:2", 420.0, 0.67, 0.67, 190.0),
        volumenes("1:2:2,5", 385.0, 0.61, 0.76, 180.0),
        volumenes("1:2:3", 350.0, 0.56, 0.84, 170.0),
        volumenes("1:2:3,5", 325.0, 0.52, 0.91, 165.0),
        volumenes("1:2:4", 300.0, 0.48, 0.96, 160.0),
        volumenes("1:2,5:2,5", 350.0, 0.70, 0.70, 170.0),
        volumenes("1:2,5:3", 325.0, 0.65, 0.78, 165.0),
        volumenes("1:2,5:3,5", 300.0, 0.60, 0.84, 160.0),
        volumenes("1:2,5:4", 280.0, 0.56, 0.90, 155.0),
        volumenes("1:2,5:4,5", 265.0, 0.53, 0.95, 150.0),
        volumenes("1:3:3", 300.0, 0.72, 0.72, 160.0),
        volumenes("1:3:4", 265.0, 0.63, 0.84, 150.0),
        volumenes("1:3:5", 235.0, 0.56, 0.93, 145.0),
        volumenes("1:4:7", 175.0, 0.56, 0.98, 120.0),
        volumenes("1:4:8", 165.0, 0.52, 1.03, 115.0),

    )
}

//MORTEROS

data class morteros(
    var proporcion: String,
    var cemento: Double,
    var agregado_fino: Double,
    var agua: Double

)

object morteros_datos {
    var datos_morteros = arrayOf(
        morteros("1:1", 900.0, 0.72, 405.0),
        morteros("1:2", 600.0, 0.96, 300.0),
        morteros("1:2,5", 515.0, 1.03, 280.0),
        morteros("1:3", 450.0, 1.08, 260.0),
        morteros("1:3,5", 400.0, 1.12, 250.0),
        morteros("1:4", 360.0, 1.15, 240.0),
        morteros("1:5", 300.0, 1.20, 225.0),
        morteros("1:6", 260.0, 1.23, 210.0),
        morteros("1:7", 225.0, 1.26, 195.0),
        morteros("1:8", 200.0, 1.28, 185.0),
        morteros("1:9", 180.0, 1.30, 175.0),
        morteros("1:10", 165.0, 1.31, 165.0),

        )
}

//HORMIGON

data class hormigon(
    var proporcion: String,
    var cemento: Double,
    var hormigon: Double,
    var agua: Double
)

object hormigon_datos {
    var datos_hormigon = arrayOf(
        hormigon("1:6", 6.20, 1.05, 0.21),
        hormigon("1:7", 5.50, 1.09, 0.19),
        hormigon("1:8", 5.0, 1.13, 0.17),
        hormigon("1:9", 4.60, 1.16, 0.16),
        hormigon("1:10", 4.20, 1.19, 0.14),
        hormigon("1:12", 3.60, 1.23, 0.12),
    )
}

