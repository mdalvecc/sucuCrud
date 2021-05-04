import coordenada.Coordenada
import nodo.PuntoRetiro
import nodo.Sucursal
import kotlin.test.assertEquals
import kotlin.test.assertTrue

val BA_COORDINATE = Coordenada(-34.548905, -58.465733)
val V25MAYO = Coordenada(-35.487653 , -59.946357)

fun main() {
    test1CercaDeBsAs()
    testCercaDe25Mayo()
    testPuntoRetiro1()
}

fun test1CercaDeBsAs() {
    val nodosByDist = DistanciaSucu.getNodosByDist(BA_COORDINATE.latitud, BA_COORDINATE.longitud)
    assertEquals(nodosByDist.size, 1)
    val nodo = nodosByDist[0].second
    assertTrue { nodo is Sucursal }
    assertTrue { (nodo as Sucursal).direccion.equals("Dir Sucu Microcentro") }

    val nodosByRadio = DistanciaSucu.getNodosEnRadio(BA_COORDINATE.latitud, BA_COORDINATE.longitud, 10.0)
    assertEquals(nodosByRadio.size, 5)
    val nodoR = nodosByRadio[2].second
    assertTrue { nodoR is Sucursal }
    assertTrue { (nodoR as Sucursal).direccion.equals("Dir Sucu Saavedra") }
}

fun testCercaDe25Mayo() {
    val nodosByDist = DistanciaSucu.getNodosByDist(V25MAYO.latitud, V25MAYO.longitud, 5)
    assertEquals(nodosByDist.size, 5)
    val nodo = nodosByDist[0].second
    assertTrue { nodo is Sucursal }
    assertTrue { (nodo as Sucursal).direccion.equals("Dir Sucu 25 de Mayo") }

    val nodosByRadio = DistanciaSucu.getNodosEnRadio(V25MAYO.latitud, V25MAYO.longitud, 100.0)
    assertEquals(nodosByRadio.size, 4)
    val nodoR = nodosByRadio[2].second
    assertTrue { nodoR is PuntoRetiro }
    assertTrue { (nodoR as PuntoRetiro).capacidad == 30 }
}

fun testPuntoRetiro1() {
    val nodosByDist = DistanciaSucu.getNodosByDist(-36.834724, -59.156966, 3)
    assertEquals(nodosByDist.size, 3)
    val nodo = nodosByDist[0].second
    assertTrue { nodo is PuntoRetiro }
    assertTrue { (nodo as PuntoRetiro).capacidad == 34 }
}
