package com.okune.profile.util

import gremlin.scala.Key

object Graph {

  object Relationship extends Enumeration {
    type Relationship = Value
    val PERSONAL_EMAIL, WORK_EMAIL, DOMAIN, APPEARED_ON,
    HOME_ADDRESS, WORK_ADDRESS, PLACE_OF_BIRTH, POSTAL_CODE, COUNTRY,
    HOME_PHONE_NUMBER, WORK_PHONE_NUMBER, MOBILE_PHONE_NUMBER, COUNTRY_CALLING_CODE,
    MARITAL_STATUS, GENDER = Value
  }

  object Property {
    val UUID: Key[String] = Key[String]("uuid")
    val Name: Key[String] = Key[String]("name")
    val FullName: Key[String] = Key[String]("full_name")
    val EmailAddress: Key[String] = Key[String]("email_address")
    val Number: Key[String] = Key[String]("number")
    val Code: Key[String] = Key[String]("code")
    val Gender: Key[String] = Key[String]("gender")
    val Domain: Key[String] = Key[String]("domain")
    val Status: Key[String] = Key[String]("status")
    val TaxIdentificationNumber: Key[String] = Key[String]("tin")
    val RegistrationNumber: Key[String] = Key[String]("rn")
    val Type: Key[String] = Key[String]("type")
  }

  object Label extends Enumeration {
    type Label = Value
    val Person,Email, Gender, Domain, MaritalStatus, Telephone, CountryCallingCode, Country = Value
  }
}
