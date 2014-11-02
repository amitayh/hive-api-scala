package com.wix.hive.commands.insights

import com.wix.hive.client.http.HttpMethod
import com.wix.hive.commands.{InsightActivitySummary, ActivityScope}
import com.wix.hive.matchers.HiveMatchers
import org.joda.time.DateTime
import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.specification.Scope

class InsightActivitySummaryForContactTest extends SpecificationWithJUnit with HiveMatchers {

  class Context extends Scope {
    val contactId = "contact-id"
    val scope = ActivityScope.site
    val from = new DateTime(2010, 1, 1, 0, 0)
    val until = new DateTime(2014, 1, 1, 0, 0)

    val commandWithParams = InsightActivitySummary(Some(contactId), scope, Some(from), Some(until))
    val commandWithDefaults = InsightActivitySummary()
  }

  "createHttpRequestData" should {

    "create HttpRequestData with all parameters" in new Context {
      commandWithParams.createHttpRequestData must httpRequestDataWith(
        url = be_===(s"/insights/contacts/$contactId/activities/summary"),
        method = be_===(HttpMethod.GET),
        query = havePairs("scope" -> scope.toString,
          "from" -> from.toString,
          "until" -> until.toString))
    }

    "create HttpRequestData with all defaults" in new Context {
      commandWithDefaults.createHttpRequestData must httpRequestDataWith(
        url = be_===(s"/insights/activities/summary"),
        method = be_===(HttpMethod.GET),
        query = havePair("scope" -> ActivityScope.app.toString))
    }
  }


}