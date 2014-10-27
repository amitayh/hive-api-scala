package com.wix.hive.commands.activities

import com.wix.hive.commands.{PostActivity, GetActivityById}
import com.wix.hive.model._
import org.joda.time.DateTime
import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.specification.Scope

class PostActivityTest extends SpecificationWithJUnit {

  "createHttpRequestData" should {

    "create HttpRequestData with all parameters" in new Context {
      val activityLocationUrl = "urrrrrrl"
      val activityDetails = ActivityDetails("additional://InfoUrl.com", "summary_")
      val contact = Contact("id", createdAt = userCreatedAt)
      val createActivity = CreateActivity(activityCreatedAt, activityLocationUrl = Some(activityLocationUrl),
        Some(activityDetails), activityInfo, Some(contact))
      val command = PostActivity(userToken, createActivity)

      val httpData = command.createHttpRequestData

      httpData.queryString must havePair("userSessionToken", userToken)
      httpData.body must_== Some(createActivity)
    }

    "create HttpRequestData with all defaults" in new Context {
      val createActivity = CreateActivity(activityCreatedAt, activityInfo = activityInfo)
      val command = PostActivity(userToken, createActivity)

      val httpData = command.createHttpRequestData

      httpData.queryString must havePair("userSessionToken", userToken)
      httpData.body must_== Some(createActivity)
    }
  }


  class Context extends Scope {
    val userToken = "tkn"
    val activityCreatedAt = new DateTime(2014, 1, 1, 0, 0)
    val userCreatedAt = new DateTime(2014, 1, 1, 0, 0)
    val activityInfo = AuthRegister("intiator", "prevAc", "ACTIVE")
  }
}