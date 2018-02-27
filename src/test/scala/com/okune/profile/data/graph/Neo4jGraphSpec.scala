package com.okune.profile.data.graph

import java.util.UUID

import com.okune.profile.util.AppConfig
import com.steelbridgelabs.oss.neo4j.structure.{Neo4JGraphConfigurationBuilder, Neo4JGraphFactory}
import gremlin.scala.{ScalaGraph, _}
import org.apache.commons.configuration.Configuration
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}

class Neo4jGraphSpec extends WordSpec with Matchers with BeforeAndAfterAll {
  val fixture = new {
    val configuration: Configuration = Neo4JGraphConfigurationBuilder.connect(
      AppConfig.neo4j.getString("host"), AppConfig.neo4j.getString("user"), AppConfig.neo4j.getString("password"))
      .withName(s"Test_${UUID.randomUUID.toString}")
      .withElementIdProvider(classOf[ElementIdProvider])
      .build()
    val graph: ScalaGraph = Neo4JGraphFactory.open(configuration).asScala

    def cleanup(): Unit = {
      graph.V.drop().iterate()
      graph.tx().commit()
    }
  }

  override def afterAll(): Unit = {
    val f = fixture
    f.graph.close()
  }

  "Gremlin" should {
    "create business vertex with properties" in {
      val f = fixture
      val Name: Key[String] = Key[String]("name")
      val Pin: Key[String] = Key[String]("pin")
      val RegistrationDate: Key[String] = Key[String]("registration_date")
      f.graph + ("Business", Name → "Safaricom Limited", Pin → "45678", RegistrationDate → "04/06/1998")
      f.graph.tx.commit()

      val vertices: List[Vertex] = f.graph.V.has(label = "Business", key = Name, predicate = P.eq[String]("Safaricom Limited")).toList
      vertices.size shouldBe 1
      vertices.head.property(Name).value shouldBe "Safaricom Limited"

      f.cleanup()
      f.graph.V.toList.size shouldBe 0
    }
  }

  it should {
    "update a vertex" in {
      val f = fixture
      val Name: Key[String] = Key[String]("name")
      val Pin: Key[String] = Key[String]("pin")
      val RegistrationDate: Key[String] = Key[String]("registration_date")
      f.graph + ("Business", Name → "Safaricom Limited", Pin → "45678", RegistrationDate → "04/06/1998")
      f.graph.tx.commit()

      val bizVertex: Vertex = f.graph.V.has(label = "Business", key = Name, predicate = P.eq[String]("Safaricom Limited")).toList.head
      bizVertex.setProperty(Name, "Safaricom Ltd")
      f.graph.tx.commit()

      val vertices: List[Vertex] = f.graph.V.has(label = "Business", key = Name, predicate = P.eq[String]("Safaricom Ltd")).toList
      vertices.size shouldBe 1

      f.cleanup()
      f.graph.V.toList.size shouldBe 0
    }
  }

  it should {
    "create business vertex, person vertex and a relationship between them" in {
      val f = fixture
      val FirstName: Key[String] = Key[String]("first_name")
      val MiddleName: Key[String] = Key[String]("middle_name")
      val LastName: Key[String] = Key[String]("last_name")
      val GovId: Key[String] = Key[String]("gov_id")
      val fred = f.graph + ("Person", FirstName → "Fred", MiddleName → "Njenga", LastName → "Njenga", GovId → "23281956")

      val Name: Key[String] = Key[String]("name")
      val Pin: Key[String] = Key[String]("pin")
      val RegistrationDate: Key[String] = Key[String]("registration_date")
      val safaricom = f.graph + ("Business", Name → "Safaricom Limited", Pin → "45678", RegistrationDate → "04/06/1998")

      val Since = Key[String]("since")
      fred --- ("EMPLOYEE_AT", Since → "01/06/2001") --> safaricom
      f.graph.tx.commit()

      fred.outE("EMPLOYEE_AT").value(Since).head shouldBe "01/06/2001"

      f.cleanup()
      f.graph.V.toList.size shouldBe 0
    }
  }

  /* The case class to map to a vertex MUST be defined outside the scope where it's being used */
  @label("Business")
  case class Business(@id uuid: Option[String],
                      name: String,
                      pin: String,
                      registration_date: String)

  it should {
    "create and modify a business vertex using a case class " in {
      val f = fixture
      val safaricom = Business(None, name = "Safaricom Limited", pin = "45678", registration_date = "04/06/1998")
      val bizVertex = f.graph + safaricom
      f.graph.tx.commit()


      val createdBiz: Business = bizVertex.toCC[Business]
      createdBiz.uuid should not be empty

      val vertices = f.graph.V.hasLabel[Business].toList
      vertices.size shouldBe 1
      createdBiz.name shouldBe "Safaricom Limited"

      val updatedBizVertex = bizVertex.updateAs[Business](_.copy(name = "Safaricom Ltd"))
      f.graph.tx.commit()

      updatedBizVertex.toCC[Business].name shouldBe "Safaricom Ltd"

      f.cleanup()
      f.graph.V.toList.size shouldBe 0
    }
  }

  it should {
    "retrieve an existing vertex using uuid and delete it" in {
      val f = fixture
      val safaricom = Business(None, name = "Safaricom Limited", pin = "45678", registration_date = "04/06/1998")
      val bizVertex = f.graph + safaricom
      f.graph.tx.commit()

      val uuid: String = bizVertex.toCC[Business].uuid.getOrElse("dummy")
      val vertices: List[Vertex] = f.graph.V(uuid).toList
      vertices.size shouldBe 1
      vertices.foreach { v => v.remove() }
      f.graph.tx().commit()
      f.graph.V(uuid).toList.size shouldBe 0
    }
  }
}