package com.wix.hive.json

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializationFeature._
import com.fasterxml.jackson.databind.{SerializerProvider, JsonSerializer, ObjectMapper}
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormatter, ISODateTimeFormat}

object JacksonObjectMapper {
  lazy val mapper = new ObjectMapper().registerModules(DefaultScalaModule, new JodaModule())
    .disable(WRITE_DATES_AS_TIMESTAMPS)
}
