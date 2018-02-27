package com.okune.profile.model

object Exceptions {
  case class ValidationException(message: String, e: Throwable = null) extends Exception(message, e)
}
