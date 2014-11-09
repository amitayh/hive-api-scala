package com.wix.hive.commands.contacts

import com.wix.hive.client.http.HttpMethod
import com.wix.hive.matchers.HiveMatchers
import com.wix.hive.model.activities.{ActivityDetails, AuthRegister}
import org.joda.time.DateTime
import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.specification.Scope

class CreateContactActivityTest extends SpecificationWithJUnit with HiveMatchers {

  class Context extends Scope {
    val contactId = "a4bc32b4-66e1-4952-a31b-83cf96efe836"
    val createdAt = new DateTime(2011, 3, 5, 6, 3)
    val locationUrl = Some("http://loc-url.com")
    val details = Some(ActivityDetails("additional info", "some summary"))
    val info = AuthRegister("initiator", "prevStream", "ACTIVE")

    val cmd = CreateContactActivity(contactId, createdAt, locationUrl, details, info)
  }

  "createHttpRequestData" should {
    "work with parameters" in new Context {
      cmd.createHttpRequestData must httpRequestDataWith(
        method = be_===(HttpMethod.POST),
        url = be_===(s"/contacts/$contactId/activities"),
        body = beSome(be_==(ContactActivityDate(createdAt, locationUrl, details, info)))
      )
    }
  }
}