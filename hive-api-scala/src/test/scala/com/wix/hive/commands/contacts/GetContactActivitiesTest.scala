package com.wix.hive.commands.contacts

import com.wix.hive.client.http.HttpMethod
import com.wix.hive.commands.common.PageSizes
import com.wix.hive.matchers.HiveMatchers
import com.wix.hive.model.activities.ActivityScope
import org.joda.time.DateTime
import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.specification.Scope

class GetContactActivitiesTest extends SpecificationWithJUnit with HiveMatchers {

  "createHttpRequestData" should {

    "create HttpRequestData with all fields" in new Context {
      command.createHttpRequestData must httpRequestDataWith(
      method = be_===(HttpMethod.GET),
      url = contain(contactId) and contain("activities"),
      query = havePairs(
        "activityTypes" -> types.mkString(","),
        "until" -> until.get.toString,
        "from" -> from.get.toString,
        "scope" -> scope.toString,
        "cursor" -> cursor.get,
        "pageSize" -> pageSize.toString))
    }

    "create HttpRequestData without optionals" in new Context {

      commandWithDefaults.createHttpRequestData must httpRequestDataWith(
        method = be_===(HttpMethod.GET),
        url = contain(contactId) and contain("activities"),
        query = havePairs(
          "scope" -> ActivityScope.site.toString,
          "pageSize" -> PageSizes.`25`.toString))
    }
  }


  class Context extends Scope {
    val contactId = "bb11e237-4e49-47bc-a85a-4f2493c8ffb9"
    val types = Seq("type1", "type2")
    val until = Some(new DateTime(2014, 1, 1, 0, 0))
    val from = Some(new DateTime(2010, 1, 1, 0, 0))
    val scope = ActivityScope.app
    val cursor = Some("crsr")
    val pageSize = PageSizes.`50`

    val commandWithDefaults = GetContactActivities(contactId)
    val command = GetContactActivities(contactId, types, until, from, scope, cursor, pageSize)
  }
}