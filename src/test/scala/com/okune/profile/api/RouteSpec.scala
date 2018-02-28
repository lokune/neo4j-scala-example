package com.okune.profile.api

import akka.actor.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.{RouteTestTimeout, ScalatestRouteTest}
import com.okune.profile.data.graph.Dao
import com.okune.profile.model.{RequestEntity, ResponseEntity}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}
import com.okune.profile.api.Json._
import scala.concurrent.duration._

class RouteSpec extends WordSpec with Matchers with ScalatestRouteTest with BeforeAndAfterAll {

  implicit def default(implicit system: ActorSystem): RouteTestTimeout = RouteTestTimeout(2.second)

  override def afterAll(): Unit = {
    Dao.Gremlin.graph.V.drop().iterate()
    Dao.Gremlin.graph.tx().commit()
    Dao.Gremlin.graph.close()
  }

  val personPayload = RequestEntity.Person(uuid = None,
    full_name = "Laban Okune Anunda",
    email = Some("laban.okune@gmail.com"),
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
        Dao.Gremlin.graph.V(uuid).out.toList.size should be > 0 // We should have joined vertices
        Dao.Gremlin.graph.V(uuid).outE.toList.size should be > 0 // We should have outgoing edges
        status shouldBe OK
      }
    }

    "delete an existing person profile" in {
      Delete(s"/profiles/persons/${uuid.getOrElse("dummy")}") ~> routes ~> check {
        Dao.Gremlin.graph.E.toList.size shouldBe 0 // After deleting person and email vertices, we should not have any edges
        Dao.Gremlin.graph.V.toList.size shouldBe 1 // Only domain vertex should be remaininf
        status shouldBe OK
      }
    }

    "accept and save a second person profile but use existing domain vertex in db" in {
      Post("/profiles/persons", personPayload) ~> routes ~> check {
        val responseEntity: ResponseEntity.Person = entityAs[ResponseEntity.Person]
        responseEntity.uuid should not be empty
        uuid = responseEntity.uuid
        Dao.Gremlin.graph.V(uuid).out.toList.size should be > 0
        Dao.Gremlin.graph.V(uuid).out.hasLabel("Email").outE("DOMAIN").toList.size should be > 0
        status shouldBe OK
      }
    }
  }
}
