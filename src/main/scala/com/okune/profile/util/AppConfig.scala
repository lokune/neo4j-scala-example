package com.okune.profile.util

import com.typesafe.config.{Config, ConfigFactory}

object AppConfig {
  val root: Config = ConfigFactory.load()
  val server: Config = root.getConfig("server")
  val neo4j: Config =  root.getConfig("okune.neo4j")
}