package com.okune.profile.api

import java.util.UUID

import com.okune.profile.data.graph.Dao
import com.okune.profile.model.{RequestEntity, ResponseEntity}
import com.wix.accord._

import scala.concurrent.{ExecutionContext, Future}

sealed trait Controller[REQ <: RequestEntity, RES <: ResponseEntity] {

  def get(uuid: UUID): Future[Option[RES]]

  def post(p: REQ): Future[RES]

  def put(p: REQ): Future[RES]

  def delete(uuid: UUID): Future[Option[Int]]
}

class PersonsController()(implicit ec: ExecutionContext)
  extends Controller[RequestEntity.Person, ResponseEntity.Person] {

  def get(uuid: UUID) = Future {
    Dao.Person.find(uuid.toString)
  }

  def post(p: RequestEntity.Person) = Future {
    /* Input validation */
    validate(p)
    Dao.Person.create(p)
  }

  def put(p: RequestEntity.Person) = Future {
    ResponseEntity.Person(uuid = Some("dummy"),
      full_name = "dummy",
      email = Some("dummy"),
      given_name = Some("dummy"),
      family_name = Some("dummy"),
      preferred_name = Some("dummy"),
      alias = Some("dummy"),
      title = Some("dummy"),
      suffix = Some("dummy"),
      dob = Some("dummy"),
      tin = Some("dummy")
    )
  }

  def delete(uuid: UUID): Future[Option[Int]] = Future {
    Dao.Person.remove(uuid.toString)
  }
}
