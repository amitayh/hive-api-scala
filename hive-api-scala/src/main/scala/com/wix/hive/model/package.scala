package com.wix.hive

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.module.scala.DefaultScalaModule

package object model {
  lazy val mapper = new ObjectMapper().registerModules(DefaultScalaModule, new JodaModule)
}
