package com.okune.profile.api

import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.ExceptionHandler
import akka.stream.ActorMaterializer
import com.okune.profile.util.AppConfig
import akka.http.scaladsl.model.StatusCodes._
import com.okune.profile.model.Exceptions.ValidationException

import scala.concurrent.ExecutionContext

object Service extends App {
  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContext = system.dispatcher
  implicit val logger: LoggingAdapter = Logging(system, getClass)

  implicit def exceptionHandler: ExceptionHandler = ExceptionHandler {
    case e: ValidationException =>
      extractRequest { req =>
        logger.error(e, s"Request to ${req.uri} could not be handled normally")
        complete(HttpResponse(BadRequest, entity = e.getMessage))
      }

    case e: Exception =>
      extractRequest { req =>
        logger.error(e, s"Request to ${req.uri} could not be handled normally")
        complete(HttpResponse(InternalServerError, entity = "Internal server error"))
      }
  }

  val routes = new HomeRoute().routes ~ new PingRoute().routes ~ new ApiDocRoute().routes ~ new ProfileRoute().routes

  Http().bindAndHandle(routes, AppConfig.server.getString("interface"), AppConfig.server.getInt("port"))
}
