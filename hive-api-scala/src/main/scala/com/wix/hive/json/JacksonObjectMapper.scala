package com.wix.hive.json

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.module.scala.DefaultScalaModule

object JacksonObjectMapper {
  lazy val mapper = new ObjectMapper().registerModules(DefaultScalaModule, new JodaModule)
}
