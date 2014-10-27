package com.wix.hive.commands.activities

import com.wix.hive.commands.contacts.PageSizes
import com.wix.hive.commands.{ActivityScope, GetActivities, GetActivityById}
import org.joda.time.DateTime
import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.specification.Scope

class GetActivitiesTest extends SpecificationWithJUnit {

  "createHttpRequestData" should {

    "create HttpRequestData with all fields" in new Context {


      private val types = Seq("type1", "type2")
      private val until = Some(new DateTime(2014, 1, 1, 0, 0))
      private val from = Some(new DateTime(2010, 1, 1, 0, 0))
      private val scope = ActivityScope.app
      private val cursor = Some("crsr")
      private val pageSize = PageSizes.`50`
      val command = GetActivities(types, until, from, scope, cursor, pageSize)

      val httpData = command.createHttpRequestData

      httpData.queryString must havePairs(
        "activityTypes" -> types.mkString(","),
        "until" -> until.get.toString,
        "from" -> from.get.toString,
        "scope" -> scope.toString,
        "cursor" -> cursor.get,
        "pageSize" -> pageSize.toString)

      httpData.queryString must haveSize(6)
    }

    "create HttpRequestData without optionals" in new Context {
      val command = GetActivities()

      val httpData = command.createHttpRequestData

      httpData.queryString must havePairs(
        "scope" -> ActivityScope.site.toString,
        "pageSize" -> PageSizes.`25`.toString)

      httpData.queryString must haveSize(2)
    }
  }


  class Context extends Scope {
  }

}