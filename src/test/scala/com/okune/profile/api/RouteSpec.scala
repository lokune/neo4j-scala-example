package com.okune.profile.api

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.okune.profile.data.graph.Dao
import com.okune.profile.model.{RequestEntity, ResponseEntity}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}
import com.okune.profile.api.Json._

class RouteSpec extends WordSpec with Matchers with ScalatestRouteTest with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    Dao.Gremlin.graph.close()
    Dao.Cypher.driver.close()
  }

  val personPayload = RequestEntity.Person(uuid = None,
    full_name = "Laban Okune Anunda",
    email = None,
    given_name = Some("Anunda"),
    family_name = Some("Okune"),
    preferred_name = Some("Laban"),
    alias = Some("lbo"),
    title = Some("Mzee"),
    suffix = Some("Mz."),
    dob = Some("2/10/1983"),
    tin = Some("232819525333"),
    marital_status = None,
    gender = Some("Male"),
    phone = None,
    address = None,
    zip_code = None,
    country = Some("Kenya")
  )

  val routes: Route = new ProfileRoute().routes
  var uuid: Option[String] = None

  "The service" should {
    "accept and save a person profile" in {
      Post("/profiles/persons", personPayload) ~> routes ~> check {
        val responseEntity: ResponseEntity.Person = entityAs[ResponseEntity.Person]
        responseEntity.uuid should not be empty
        uuid = responseEntity.uuid
        status shouldBe OK
      }
    }

    "delete an existing person profile" in {
      Delete(s"/profiles/persons/${uuid.getOrElse("dummy")}") ~> routes ~> check {
        status shouldBe OK
      }
    }
  }
}
