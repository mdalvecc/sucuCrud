package database

import com.mongodb.MongoClient
import com.mongodb.MongoClientSettings
import com.mongodb.MongoClientURI
import com.mongodb.client.MongoDatabase
import org.bson.codecs.configuration.CodecRegistries.fromProviders
import org.bson.codecs.configuration.CodecRegistries.fromRegistries
import org.bson.codecs.pojo.PojoCodecProvider


var pojoCodecRegistry = fromRegistries(
    MongoClientSettings.getDefaultCodecRegistry(),
    fromProviders(PojoCodecProvider.builder().automatic(true).build())
)

abstract class DBConnection {
    abstract val dbName: String
    abstract val dbUri: String
}

class MongoDBConnection : DBConnection() {
    // Valores default de la base mongo. Se configuran por properties pero pongo acá los default por si no los pasan
    private val propUri = "mongodb.uri"
    private val propName = "mongodb.dbName"

    override val dbName = System.getProperty(propName) ?: "FVG"
    override val dbUri = System.getProperty(propUri) ?: "mongodb+srv://marianofvg:mariano@cluster0.8lwg0.mongodb.net/$dbName?retryWrites=true&w=majority"

    private val clientURI = MongoClientURI(dbUri)
    private val mongoClient = MongoClient(clientURI)

    val database: MongoDatabase = mongoClient.getDatabase(dbName).withCodecRegistry(pojoCodecRegistry)
}
