package com.okune.profile.util

import gremlin.scala.Key

object Graph {

  object Relationship extends Enumeration {
    type Relationship = Value
    val PERSONAL_EMAIL, WORK_EMAIL, DOMAIN = Value
  }

  object Property {
    val UUID: Key[String] = Key[String]("uuid")
    val Name: Key[String] = Key[String]("name")
    val FullName: Key[String] = Key[String]("full_name")
    val EmailAddress: Key[String] = Key[String]("email_address")
    val Domain: Key[String] = Key[String]("domain")
  }

  object Label extends Enumeration {
    type Label = Value
    val Person,Email, Gender, Domain = Value
  }
}
