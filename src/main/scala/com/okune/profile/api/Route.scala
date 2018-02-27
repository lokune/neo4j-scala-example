package com.okune.profile.api

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.okune.profile.api.Json._
import com.okune.profile.model.RequestEntity

import scala.concurrent.ExecutionContext

class HomeRoute {
  val routes: Route = logRequestResult("demo-service/") {
    path("") {
      get {
        complete {
          HttpEntity(ContentTypes.`text/html(UTF-8)`,
            "<html><body><h5>Welcome! If you see this message, the profile service is up and running!</h5></body></html>")
        }
      }
    }
  }
}

class PingRoute {
  val routes: Route = logRequestResult("demo-service/") {
    path("ping") {
      get {
        complete {
          HttpEntity(ContentTypes.`text/plain(UTF-8)`, "PONG!")
        }
      }
    }
  }
}

class ApiDocRoute {
  val routes: Route = logRequestResult("demo-service/") {
    path("apidoc") {
      getFromResource("apidoc.raml", ContentTypes.`text/plain(UTF-8)`)
    }
  }
}

class ProfileRoute()(implicit ec: ExecutionContext) {

  val personsCtrl: PersonsController = new PersonsController()

  val routes: Route = logRequestResult("demo-service/profiles") {
    pathPrefix("profiles") {
      path("persons") {
        (post & entity(as[RequestEntity.Person])) { person =>
            complete(personsCtrl.post(person))
        }
      } ~ path("persons" / JavaUUID) { uuid =>
        get {
            onSuccess(personsCtrl.get(uuid)) {
              case Some(p) => complete(p)
              case _ => complete(StatusCodes.NotFound)
            }
        } ~ delete {
            onSuccess(personsCtrl.delete(uuid)) {
              case Some(i) => complete(s"$i profiles deleted.")
              case _ => complete(StatusCodes.NotFound)
            }
        }
      }
    }
  }
}
