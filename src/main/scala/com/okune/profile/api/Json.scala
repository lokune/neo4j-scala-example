package com.okune.profile.api

import com.okune.profile.model.{RequestEntity, ResponseEntity}
import com.okune.profile.model.RequestEntity.Person
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

object Json extends DefaultJsonProtocol {
  implicit val reqPersonFormat: RootJsonFormat[Person] = jsonFormat17(RequestEntity.Person.apply)
  implicit val resPersonFormat: RootJsonFormat[ResponseEntity.Person] = jsonFormat11(ResponseEntity.Person.apply)
}
