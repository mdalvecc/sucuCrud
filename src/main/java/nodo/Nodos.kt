package nodo

import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters.eq
import coordenada.Coordenada
import database.MongoDBConnection
import org.bson.types.ObjectId

abstract class Nodo(coord: Coordenada) {
    var id: ObjectId = ObjectId()
    val coordenada: Coordenada = coord

    protected val dbConn = MongoDBConnection()
    private val propColl = "mongodb.dbColl"
    protected val collection = System.getProperty(propColl) ?: "SucuCrud"

    constructor(latitud: Double, longitud: Double) : this(Coordenada(latitud, longitud))

    // Crea un nodo en mongo con la info que tiene la instancia
    abstract fun create(): Nodo
    // Encuentra un nodo por id. Puede devolver null si no lo encontró
    abstract fun find(id: ObjectId): Nodo?
    // Actualiza un nodo por id. Puede devolver null si falló
    abstract fun update(id: ObjectId, modNodo: Nodo): Nodo?
    // Borra un nodo por id. Lo devuelve si lo pudo borrar, sino devuelve null
    abstract fun delete(id: ObjectId): Nodo?
}

class Sucursal(coord: Coordenada, dir: String, h: Pair<Int, Int>) : Nodo(coord) {
    var direccion: String = dir
    var horario: Pair<Int,Int> = h

    constructor(latitud: Double, longitud: Double, dir: String, h: Pair<Int, Int>) : this(Coordenada(latitud, longitud), dir, h)

    val dbColl: MongoCollection<Sucursal> = dbConn.database.getCollection(collection, Sucursal::class.java)

    override fun create(): Sucursal {
        dbColl.insertOne(this)
        return this
    }

    override fun find(id: ObjectId): Sucursal? {
        return dbColl.find(eq("_id", id)).first()
    }

    override fun update(id: ObjectId, modNodo: Nodo): Nodo? {
        if (modNodo !is Sucursal)
            return null

        val updateRes = dbColl.replaceOne(eq("_id", id), modNodo)
        return if (updateRes.modifiedCount == 0L)
            null
        else modNodo
    }

    override fun delete(id: ObjectId): Sucursal? {
        val delSucu = find(id)
        val delRes = dbColl.deleteOne(eq("_id", id))
        return if (delRes.deletedCount == 0L)
            null
        else {
            delSucu
        }
    }

    override fun toString(): String {
        return "Id: $id - Dirección: $direccion - Horario: $horario - Coords: $coordenada"
    }


}

class PuntoRetiro(coord: Coordenada, cap: Int) : Nodo(coord) {
    var capacidad: Int = cap

    constructor(latitud: Double, longitud: Double, cap: Int) : this(Coordenada(latitud, longitud), cap)

    val dbColl: MongoCollection<PuntoRetiro> = dbConn.database.getCollection(collection, PuntoRetiro::class.java)

    override fun create(): PuntoRetiro {
        dbColl.insertOne(this)
        return this
    }

    override fun find(id: ObjectId): PuntoRetiro? {
        return dbColl.find(eq("_id", id)).first()
    }

    override fun update(id: ObjectId, modNodo: Nodo): Nodo? {
        if (modNodo !is PuntoRetiro)
            return null

        val updateRes = dbColl.replaceOne(eq("_id", id), modNodo)
        return if (updateRes.modifiedCount == 0L)
            null
        else modNodo
    }

    override fun delete(id: ObjectId): PuntoRetiro? {
        val delSucu = find(id)
        val delRes = dbColl.deleteOne(eq("_id", id))
        return if (delRes.deletedCount == 0L)
            null
        else {
            delSucu
        }
    }

    override fun toString(): String {
        return "Id: $id - Capacidad: $capacidad - Coords: $coordenada"
    }
}