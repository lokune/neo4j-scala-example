package com.okune.profile.data.graph

import java.util.Objects

import com.fasterxml.uuid.Generators
import com.steelbridgelabs.oss.neo4j.structure.Neo4JElementIdProvider
import org.neo4j.driver.v1.types.Entity

object ElementIdProvider {
  val IdFieldName = "uuid"
}

class ElementIdProvider extends Neo4JElementIdProvider[String] {

  import ElementIdProvider._

  override def fieldName(): String = IdFieldName

  override def generate(): String = Generators.randomBasedGenerator().generate().toString

  override def processIdentifier(uuid: Any): String = {
    Objects.requireNonNull(uuid, "Element identifier cannot be null")
    uuid.toString
  }

  def get(entity: Entity): String = {
    Objects.requireNonNull(entity, "Entity cannot be null")
    entity.get(IdFieldName).toString
  }

  def matchPredicateOperand(alias: String): String = s"$alias.$IdFieldName"
}