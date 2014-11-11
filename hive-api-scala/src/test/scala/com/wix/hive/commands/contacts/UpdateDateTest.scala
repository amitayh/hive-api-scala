package com.wix.hive.commands.contacts

import com.wix.hive.client.http.HttpMethod
import com.wix.hive.matchers.HiveMatchers
import org.joda.time.DateTime
import org.specs2.mutable.SpecificationWithJUnit

class UpdateDateTest extends SpecificationWithJUnit with HiveMatchers {

  class Context extends ContextForModification {
    val dateId = "a2111887-8a12-40bf-9d45-7ecc8b33553d"
    val dateTime = new DateTime(2013, 3,6, 3, 5)
    val date = ContactDateDTO("tag-date", dateTime)

    val cmd = UpdateDate(contactId, modifiedAt, dateId, date)
  }

  "createHttpRequestData" should {
    "work with parameters" in new Context {
      cmd.createHttpRequestData must httpRequestDataWith(
        method = be_===(HttpMethod.PUT),
        url = be_===(s"/contacts/$contactId/date/$dateId"),
        query = havePair("modifiedAt", modifiedAt.toString),
        body = beSome(date)
      )
    }
  }
}
