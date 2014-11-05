package com.wix.hive.commands.contacts

import com.wix.hive.client.http.HttpMethod
import com.wix.hive.matchers.HiveMatchers
import org.joda.time.DateTime
import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.specification.Scope

class AddDateTest extends SpecificationWithJUnit with HiveMatchers {
  "createHttpRequestData" should {
    "work with parameters" in new Context {
      cmd.createHttpRequestData must httpRequestDataWith(
        method = be_===(HttpMethod.POST),
        url = contain(contactId) and contain("date"),
        query = havePair("modifiedAt", modifiedAt.toString),
        body = beSome(be_==(date))
      )
    }
  }

  class Context extends Scope {
    val contactId = "ff45a6b8-cc75-41cb-815d-9e6584e5067d"
    val modifiedAt = new DateTime(2010, 3, 2, 1, 2)
    val contactDate = new DateTime(2012, 1, 2, 3, 2)
    val date = ContactDateDTO("tag-date", contactDate)

    val cmd = AddDate(contactId, modifiedAt, date)
  }

}