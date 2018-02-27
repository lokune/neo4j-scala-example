package com.okune.profile.model

sealed trait ResponseEntity

object ResponseEntity {

  /* This is expected to be dynamic based on caller needs */
  case class Person(uuid: Option[String],
                    full_name: String,
                    email: Option[String],
                    given_name: Option[String],
                    family_name: Option[String],
                    preferred_name: Option[String],
                    alias: Option[String],
                    title: Option[String],
                    suffix: Option[String],
                    dob: Option[String],
                    tin: Option[String]) extends ResponseEntity

}
