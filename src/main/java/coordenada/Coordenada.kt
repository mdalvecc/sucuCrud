package coordenada

import java.lang.Double.isNaN
import java.lang.Math.round

private object WGS84 {
    // WGS-84 standard constants
    val ellipsoidA = 6378137.0
    val ellipsoidB = 6356752.31425
    val ellipsoidF = 0.0033528106647474805  // 1 / 298.257223563
}

private const val ITERACIONES_MAX = 200

class Coordenada(lat: Double, long: Double) {
    var latitud: Double = lat
    var longitud: Double = long

    override fun toString(): String {
        return "Latitud: $latitud // Longitud: $longitud"
    }

    /**
     * Devuelve la distancia geodésica entre 2 coordenadas en kms.
     * Es la implementación de "Vincenty solutions of geodesics on the ellipsoid" basado en la información en
     * http://www.movable-type.co.uk/scripts/latlong-vincenty.html
     */
    fun distancia(coordDestino: Coordenada): Double {
        val latOrigin = Math.toRadians(latitud)
        val lonOrigin = Math.toRadians(longitud)
        val latDestination = Math.toRadians(coordDestino.latitud)
        val lonDestination = Math.toRadians(coordDestino.longitud)

        val lonDifference = lonDestination - lonOrigin
        val tanU1 = (1 - WGS84.ellipsoidF) * Math.tan(latOrigin)
        val cosU1 = 1 / Math.sqrt(1 + tanU1 * tanU1)
        val sinU1 = tanU1 * cosU1
        val tanU2 = (1 - WGS84.ellipsoidF) * Math.tan(latDestination)
        val cosU2 = 1 / Math.sqrt(1 + tanU2 * tanU2)
        val sinU2 = tanU2 * cosU2

        var sinDelta: Double
        var cosDelta: Double
        var delta: Double
        var cosSqAlpha: Double
        var cos2DeltaM: Double

        var lambda = lonDifference
        var lambdaP: Double
        var iterations = 0.0
        do {
            val sinLambda = Math.sin(lambda)
            val cosLambda = Math.cos(lambda)
            val sinSqDelta =
                cosU2 * sinLambda * (cosU2 * sinLambda) + (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda) * (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda)
            sinDelta = Math.sqrt(sinSqDelta)
            if (sinDelta == 0.0) return 0.0                      // co-incident points
            cosDelta = sinU1 * sinU2 + cosU1 * cosU2 * cosLambda
            delta = Math.atan2(sinDelta, cosDelta)
            val sinAlpha = cosU1 * cosU2 * sinLambda / sinDelta
            cosSqAlpha = 1 - sinAlpha * sinAlpha
            cos2DeltaM = cosDelta - 2.0 * sinU1 * sinU2 / cosSqAlpha
            if (isNaN(cos2DeltaM)) cos2DeltaM = 0.0            // equatorial line: cosSqAlpha=0
            val C = WGS84.ellipsoidF / 16 * cosSqAlpha * (4 + WGS84.ellipsoidF * (4 - 3 * cosSqAlpha))
            lambdaP = lambda
            lambda = lonDifference + (1 - C) * WGS84.ellipsoidF * sinAlpha *
                    (delta + C * sinDelta * (cos2DeltaM + C * cosDelta * (-1 + 2.0 * cos2DeltaM * cos2DeltaM)))
        } while (Math.abs(lambda - lambdaP) > 1e-12 && ++iterations < ITERACIONES_MAX)

        if (iterations >= ITERACIONES_MAX)
            throw RuntimeException(
                "La fórmula de cálculo de distancia falló al intentar converger para las coordenadas $this : $coordDestino"
            )

        val upsilonSq =
            cosSqAlpha * (WGS84.ellipsoidA * WGS84.ellipsoidA - WGS84.ellipsoidB * WGS84.ellipsoidB) / (WGS84.ellipsoidB * WGS84.ellipsoidB)
        val A = 1 + upsilonSq / 16384 * (4096 + upsilonSq * (-768 + upsilonSq * (320 - 175 * upsilonSq)))
        val B = upsilonSq / 1024 * (256 + upsilonSq * (-128 + upsilonSq * (74 - 47 * upsilonSq)))
        val deltaOmicron = B * sinDelta *
                (cos2DeltaM + B / 4 * (cosDelta * (-1 + 2.0 * cos2DeltaM * cos2DeltaM) - B / 6 * cos2DeltaM * (-3 + 4.0 * sinDelta * sinDelta) * (-3 + 4.0 * cos2DeltaM * cos2DeltaM)))

        return round(WGS84.ellipsoidB * A * (delta - deltaOmicron)).toDouble() / 1000  // devuelve kms
    }
}