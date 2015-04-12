package com.wix.hive.infrastructure

import com.fasterxml.jackson.databind.SerializationFeature._
import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.module.scala.DefaultScalaModule

/**
 * User: maximn
 * Date: 4/12/15
 */
object JacksonObjectMapper{
  lazy val mapper = new ObjectMapper().registerModules(DefaultScalaModule, new JodaModule())
    .disable(WRITE_DATES_AS_TIMESTAMPS).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
}
