package com.okune.profile.data.graph

import com.okune.profile.data.graph.Dao.Gremlin
import com.okune.profile.model.Exceptions.ValidationException
import com.okune.profile.model.{DatabaseEntity, RequestEntity, ResponseEntity}
import com.okune.profile.util.{AppConfig, Graph}
import com.steelbridgelabs.oss.neo4j.structure.{Neo4JGraphConfigurationBuilder, Neo4JGraphFactory}
import gremlin.scala.{ScalaGraph, _}
import henkan.optional.all._
import org.apache.commons.configuration.Configuration

sealed trait Dao {
  import Gremlin._

  /**
    * Find a vertex based on a unique property
    *
    * @param label    the label for the vertex
    * @param key      the property key
    * @param optValue the optional value to match. If `None`, we get `None` back
    * @tparam A the type of the key value
    * @return a `Vertex`
    */
  protected def findVertex[A](label: String,
                              key: Key[A],
                              optValue: Option[A]): Option[Vertex] =
    for {
      value <- optValue
      vertices = graph.V.has(label = label, key = key, predicate = P.eq[A](value)).toList
      if vertices.nonEmpty
    } yield vertices.head


  /**
    * Remove a vertex
    *
    * @param uuid  the UUID of the vertex to be deleted
    * @return the number of vertices deleted
    */
  protected def _remove(uuid: String): Option[Int] = {
    val matchingVs = graph.V(uuid).toList
    val count = matchingVs.size

    matchingVs.foreach { v => v.remove() }
    graph.tx().commit()
    if(count > 0) Some(count) else None
  }

  private def sanitize(s: String): String = s.replaceAll("\"", "")
}

object Dao {

  object Gremlin {
    /**
      * The `Configuration` required to connect to Neo4j using the bolt protocol.
      *
      * @see <a href="https://boltprotocol.org">http://google.com</a>
      */
    val configuration: Configuration = Neo4JGraphConfigurationBuilder.connect(
      AppConfig.neo4j.getString("host"), AppConfig.neo4j.getString("user"), AppConfig.neo4j.getString("password"))
      .withName(s"Profiles")
      .withElementIdProvider(classOf[ElementIdProvider])
      .build()
    lazy val graph: ScalaGraph = Neo4JGraphFactory.open(configuration).asScala
  }

  object Person extends Dao {

    import Gremlin._

    /**
      * Create a person vertex
      *
      * @param p      the request body from the caller unmarshalled into `RequestEntity.Person`.
      * @return the database vertex deserialized into `ResponseEntity.Person`
      */
    def create(p: RequestEntity.Person): ResponseEntity.Person = {
      val databasePersonEntity = from(p).toOptional[DatabaseEntity.Person].onCreate()

      /* Find email vertex in database */
      val existingEmailVertex: Option[Vertex] =
        findVertex(Graph.Label.Email.toString, Graph.Property.EmailAddress, p.email)

      /* If email vertex exists, throw a `ValidationException` */
      if (existingEmailVertex.nonEmpty) throw ValidationException("A profile with the email address provided exists!")

      val newPersonVertex: Vertex = graph + databasePersonEntity
      val newOptEmailVertex = p.email.map(email => graph + DatabaseEntity.Email(uuid = None, email_address = email))

      var emailAddress: String = null
      var domain: String = null
      newOptEmailVertex.foreach { emailVertex =>
        // Create a domain vertex
        emailAddress = emailVertex.toCC[DatabaseEntity.Email].email_address
        domain = s"www.${emailAddress.split("@")(1)}"
        val domainEntity = DatabaseEntity.Domain(None, domain)

        val existingDomainVertex: Option[Vertex] = findVertex(Graph.Label.Domain.toString,
          Graph.Property.Domain, Some(domain))

        val domainVertex = existingDomainVertex.getOrElse(graph + domainEntity)

        //Create edge: emailVertex --> Domain
        //TODO: Find out why it's not able to create an edge to an existing vertex yet it works for new.
        emailVertex --- Graph.Relationship.DOMAIN.toString --> domainVertex

        //Create edge: personVertex --> emailVertex
        newPersonVertex --- Graph.Relationship.PERSONAL_EMAIL.toString --> emailVertex
      }
      graph.tx.commit()

      /* Construct the response entity */
      val savedPerson = newPersonVertex.toCC[DatabaseEntity.Person]
      val savedEmail = newOptEmailVertex.map(_.toCC[DatabaseEntity.Email])

      from(savedPerson).toOptional[ResponseEntity.Person]
        .copy(email = savedEmail.map(_.email_address))
    }

    /**
      * Find a person vertex
      *
      * @param uuid the UUID of the vertex to be retrieved
      * @return the result deserialized into `ResponseEntity.Person`
      */
    def find(uuid: String): Option[ResponseEntity.Person] = {
      val matchingVs = graph.V(uuid).toList
      val count = matchingVs.size
      //TODO: Logic to add extra information to the response based on caller needs
      if (count > 0) Some(matchingVs.head.toCC[ResponseEntity.Person]) else None
    }

    /**
      * Remove a person vertex
      *
      * @param uuid the UUID of the vertex to be deleted
      * @return the number of vertices deleted
      */
    def remove(uuid: String): Option[Int] = {
      // graph.V(uuid).outE - Following outgoing edges returns empty!
      graph.V(uuid).outE.toList.foreach(_.remove()) // Remove outgoing edges
      _remove(uuid)
    }
  }

}
