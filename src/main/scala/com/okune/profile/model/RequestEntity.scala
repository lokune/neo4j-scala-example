package com.okune.profile.model

import com.wix.accord.dsl._


sealed trait RequestEntity

object RequestEntity {

  case class Person(uuid: Option[String],
                    full_name: String,
                    email: Option[String],
                    given_name: Option[String],
                    family_name: Option[String],
                    preferred_name: Option[String],
                    alias: Option[String],
                    title: Option[String],
                    suffix: Option[String],
                    marital_status: Option[String],
                    gender: Option[String],
                    phone: Option[String],
                    address: Option[String],
                    zip_code: Option[String],
                    country: Option[String],
                    dob: Option[String],
                    tin: Option[String]) extends RequestEntity

  object Person {
    /* Input validator */
    implicit val inputValidator = validator[RequestEntity.Person] { p =>
      p.full_name is notEmpty
      p.email.each has size > 3
    }
  }
}