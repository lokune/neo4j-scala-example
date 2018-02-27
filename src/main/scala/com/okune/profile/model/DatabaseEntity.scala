package com.okune.profile.model

import java.util.Date

import com.okune.profile.util.Graph
import gremlin.scala.{id, label}

sealed trait DatabaseEntity {
  def uuid: Option[String]
}

sealed trait Auditable[E <: DatabaseEntity] {
  def created_at: Option[Long]

  def created_by: Option[Long]

  def modified_at: Option[Long]

  def modified_by: Option[Long]

  def onCreate(): E

  def onModify(): E
}

object DatabaseEntity {

  @label(Graph.Label.Person.toString)
  case class Person(@id uuid: Option[String],
                    full_name: String,
                    given_name: Option[String],
                    family_name: Option[String],
                    preferred_name: Option[String],
                    alias: Option[String],
                    title: Option[String],
                    suffix: Option[String],
                    dob: Option[String],
                    tin: Option[String],
                    created_at: Option[Long],
                    created_by: Option[Long],
                    modified_at: Option[Long],
                    modified_by: Option[Long]) extends DatabaseEntity with Auditable[Person] {

    def onCreate(): Person = {
      this.copy(created_at = Some(new Date().getTime), created_by = Some(1l))
    }

    def onModify(): Person = {
      this.copy(modified_at = Some(new Date().getTime), modified_by = Some(1l))
    }
  }

  @label(Graph.Label.Email.toString)
  case class Email(@id uuid: Option[String], email_address: String) extends DatabaseEntity

  @label(Graph.Label.Domain.toString)
  case class Domain(@id uuid: Option[String], domain: String) extends DatabaseEntity

}
