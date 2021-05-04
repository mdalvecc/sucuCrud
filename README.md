Challenge 1 - Sucursal CRUD

Hola! Antes que nada les quiero aclarar que el desarrollo funciona pero no está terminado. Todo lo que hice es nuevo para mí
ya que no trabajé con ninguna de estas tecnologías anteriormente. Me resultó divertida la experiencia.
Estuve trabajando en esto el domingo y parte del día lunes y martes. No voy a pretender aprender Kotlin + Mongo +
framework de test + api rest + deploy en un web server en 3 días, con lo cual les entrego la solución con 2 ejecutables:
1 para el cálculo de la distancia y 1 para los tests.

La parte del ABM de las sucus está planteada en código pero no probada ya que logré armar la conexión con Mongo y
me empecé a topar con problemas de generics al convertir los pojo.
Prefiero entregarles la solución planteada así no los tengo esperando sin darles una respuesta.

Otra cosa que no armé fue una api rest por el mismo tema del tiempo pero se puede ejecutar por línea de comando o dentro del Idea.

Como decía, hay 2 ejecutables

A. Para ejecutar el cálculo de distancia con un conjunto predefinido de datos, ejecutar el método main de la clase DistanciaSucu.kt
Lo pueden ejecutar con el idea, pasándole parámetros de <latitud> <longitud> [<cantidadResultados>]

B. Para ejecutar los tests, tienen que ejecutar el método main de la clase Tests.kt
