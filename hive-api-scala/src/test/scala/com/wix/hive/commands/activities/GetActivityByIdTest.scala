package com.wix.hive.commands.activities

import com.wix.hive.commands.GetActivityById
import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.specification.Scope

class GetActivityByIdTest extends SpecificationWithJUnit {

  "createHttpRequestData" should {

    "create HttpRequestData with id" in new Context {
      val httpData = command.createHttpRequestData

      httpData.url must endWith(s"/$id")
    }
  }


  class Context extends Scope {
    val id = "124132432"
    val command = GetActivityById(id)
  }
}