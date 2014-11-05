package com.wix.hive.json

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature._
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.module.scala.DefaultScalaModule

object JacksonObjectMapper {
  lazy val mapper = new ObjectMapper().registerModules(DefaultScalaModule, new JodaModule())
    .disable(WRITE_DATES_AS_TIMESTAMPS)
}
