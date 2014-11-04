package com.wix.hive.commands.activities

import com.wix.hive.client.http.HttpMethod
import com.wix.hive.matchers.HiveMatchers
import com.wix.hive.model.activities.{ActivityCreationData, ActivityDetails, AuthRegister, ContactActivity}
import org.joda.time.DateTime
import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.specification.Scope

class CreateActivityTest extends SpecificationWithJUnit with HiveMatchers {

  class Context extends Scope {
    val userToken = "tkn"
    val activityCreatedAt = new DateTime(2014, 1, 1, 0, 0)
    val userCreatedAt = new DateTime(2014, 1, 1, 0, 0)
    val activityInfo = AuthRegister("intiator", "prevAc", "ACTIVE")

    val activityLocationUrl = "urrrrrrl"
    val activityDetails = ActivityDetails("additional://InfoUrl.com", "summary_")
    val contact = ContactActivity()
    val createActivityWithParams = ActivityCreationData(activityCreatedAt, activityLocationUrl = Some(activityLocationUrl),
      Some(activityDetails), activityInfo, Some(contact))

    val createActivityWithoutParams = ActivityCreationData(activityCreatedAt, activityInfo = activityInfo)
  }

  "createHttpRequestData" should {

    "create HttpRequestData with all parameters" in new Context {
      val command = CreateActivity(userToken, createActivityWithParams)

      command.createHttpRequestData must httpRequestDataWith(
        url = be_===("/activities"),
        method = be_===(HttpMethod.POST),
        query = havePair("userSessionToken" -> userToken),
        body = beSome(createActivityWithParams))
    }

    "create HttpRequestData with all defaults" in new Context {
      val command = CreateActivity(userToken, createActivityWithoutParams)

      val httpData = command.createHttpRequestData

      httpData.queryString must havePair("userSessionToken", userToken)
      httpData.body must beSome(createActivityWithoutParams)
    }
  }


}