package com.wix.hive.model

import com.fasterxml.jackson.databind.SerializationFeature._
import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.module.scala.DefaultScalaModule

/**
 * User: maximn
 * Date: 4/12/15
 */
// hack to be used only in com.wix.hive.model.activities.Activity.factory
private [model] object JacksonObjectMapper{
  lazy val mapper = new ObjectMapper().registerModules(DefaultScalaModule, new JodaModule())
    .disable(WRITE_DATES_AS_TIMESTAMPS).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
}
