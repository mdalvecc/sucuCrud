import DistanciaSucu.getNodosByDist
import coordenada.Coordenada
import nodo.Nodo
import nodo.PuntoRetiro
import nodo.Sucursal
import kotlin.system.exitProcess

val BA_COORDINATE = Coordenada(-34.548905, -58.465733)
val BELGRANO = Coordenada(-34.560416, -58.456794)
val COLEGIALES = Coordenada(-34.575921, -58.448171)
val PARQUE_SAAVEDRA = Coordenada(-34.550228, -58.479799)
val NUNEZ = Coordenada(-34.542524, -58.459978)
val V25MAYO = Coordenada(-35.487653 , -59.946357)

/**
 * Recibe 2 números decimales indicando latitud y longitud del punto para el cual se van a devolver las sucursales o
 * puntos de retiro más cercanos en orden de cercanía al mismo.
 * El 3er argumento indica la cantidad de resultados a devolver. Si hay menos nodos que dicho número, devuelve todos.
 */
fun main(args : Array<String>) {
    // Parseo los argumentos de entrada
    val cantArgs = args.size
    if (cantArgs < 2 || cantArgs > 3) {
        print(uso())
        exitProcess(1)
    }

    var error = false
    var lat: Double
    try {
        lat = args[0].toDouble()
    } catch (e: Exception) {
        error = true
        lat = 0.0
    }
    var long: Double
    try {
        long = args[1].toDouble()
    } catch (e: Exception) {
        error = true
        long = 0.0
    }
    var cantNodos = 1
    if (cantArgs == 3)
        cantNodos = try {
            args[2].toInt()
        } catch (e: Exception) {
            error = true
            1
        }

    if (error) {
        print(uso())
        exitProcess(1)
    }

    getNodosByDist(lat, long, cantNodos).map { n -> n.second }.forEach{ n -> println(n) }

    return
}

object DistanciaSucu {
    // Obtengo la info de nodos
    private val fullNodos: List<Nodo> = generarSucus().plus(generarPuntoRetiro())

    fun getNodosByDist(lat: Double, long: Double): List<Pair<Double, Nodo>> {
        return getNodosByDist(lat, long, null)
    }

    /*
     * Obtiene la cantidad de nodos solicitada, que estén más cercanos a la latitud y longitud indicadas.
     * Los nodos se ordenan por cercanía a la coordenada.
     */
    fun getNodosByDist(lat: Double, long: Double, cantNodos: Int?): List<Pair<Double, Nodo>> {
        // Genero la info de nodos
        val coordenadaDestino = Coordenada(lat, long)

        //Calculo la distancia y devuelvo los resultados
        var cantNodoProcesado = 0
        val cantNodoReturn: Int = cantNodos ?: 1
        return fullNodos.map { n -> Pair(n.coordenada.distancia(coordenadaDestino), n) }
            .sortedBy { p -> p.first }
            .takeWhile { cantNodoReturn > cantNodoProcesado++ }
    }

    /*
     * Obtiene todos los nodos ubicados en el círculo formado por el centro con latitud y longitud indicadas, y con el
     * radio solicitado.
     * Los nodos se ordenan por cercanía a la coordenada.
     */
    fun getNodosEnRadio(lat: Double, long: Double, radio: Double): List<Pair<Double, Nodo>> {
        // Pido todos los nodos y me quedo con los que están dentro del radio
        return getNodosByDist(lat, long, Int.MAX_VALUE).filter { p ->  p.first <= radio}
    }
}

fun uso(): String {
    return "Argumentos inválidos en la invocación del método\n"+
            "Uso: DistanciaSucu <latitud> <longitud> [<cantidad de resultados>]"
}

/*
 * Estos 2 métodos iban a insertar la info en mongo pero me topé con un error y les tuve que sacar la invocación al
 * método create de forma de que se pueda ejecutar
 *
 * El error es Caused by: org.bson.codecs.configuration.CodecConfigurationException: Failed to encode 'Sucursal'. Encoding 'dbColl' errored with: An exception occurred when encoding using the AutomaticPojoCodec.
 * Encoding a MongoCollectionImpl: 'com.mongodb.client.internal.MongoCollectionImpl@7e19ebf0' failed with the following exception:
 *
 * MongoCollectionImpl contains generic types that have not been specialised.
 * Top level classes with generic types are not supported by the PojoCodec.
 *
 * A custom Codec or PojoCodec may need to be explicitly configured and registered to handle this type.
 * 	at org.bson.codecs.pojo.PojoCodecImpl.encodeValue(PojoCodecImpl.java:182)
 * 	at org.bson.codecs.pojo.PojoCodecImpl.encodeProperty(PojoCodecImpl.java:168)
 * 	at org.bson.codecs.pojo.PojoCodecImpl.encode(PojoCodecImpl.java:105)
 * 	at org.bson.codecs.pojo.AutomaticPojoCodec.encode(AutomaticPojoCodec.java:50)
 * 	... 37 more
 */
fun generarPuntoRetiro(): List<PuntoRetiro> {
    val prs = listOf(
        PuntoRetiro(BA_COORDINATE.latitud - 0.1, BA_COORDINATE.longitud - 0.2, 40),
        PuntoRetiro(-35.987653, -59.846357, 30),
        PuntoRetiro(-34.835288, -59.739067, 20),
        PuntoRetiro(-34.534275, -59.534590, 10),
        PuntoRetiro(-36.834724, -59.156966, 34),
        PuntoRetiro(-35.924547, -59.634588, 22)
    )
    return prs
}

fun generarSucus(): List<Sucursal> {
    val sucs = listOf(
        Sucursal(BA_COORDINATE, "Dir Sucu Microcentro", Pair(8, 22)),
        Sucursal(BELGRANO, "Dir Sucu Belgrano", Pair(9, 20)),
        Sucursal(V25MAYO, "Dir Sucu 25 de Mayo", Pair(10, 20)),
        Sucursal(COLEGIALES, "Dir Sucu Colegiales", Pair(9, 20)),
        Sucursal(PARQUE_SAAVEDRA, "Dir Sucu Saavedra", Pair(9, 20)),
        Sucursal(NUNEZ, "Dir Sucu Nuñez", Pair(9, 22))
    )
    return sucs
}