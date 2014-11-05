package com.wix.hive.commands.contacts

import com.wix.hive.client.http.HttpMethod
import com.wix.hive.matchers.HiveMatchers
import org.joda.time.DateTime
import org.specs2.mutable.{SpecificationWithJUnit, Specification}
import org.specs2.specification.Scope

class AddUrlTest extends SpecificationWithJUnit with HiveMatchers {
  "createHttpRequestData" should {
    "work with parameters" in new Context {
      cmd.createHttpRequestData must httpRequestDataWith(
        method = be_===(HttpMethod.POST),
        url = contain(contactId) and contain("url"),
        query = havePair("modifiedAt", modifiedAt.toString),
        body = beSome(be_==(url))
      )
    }
  }

  class Context extends Scope {
    val contactId = "0g6f6d66-e27c-48ce-9ad0-1fa30977954d"
    val modifiedAt = new DateTime(2010, 3, 2, 1, 2)
    val url = ContactUrlDTO("contact-tag", "http://wix.com")

    val cmd = AddUrl(contactId, modifiedAt, url)
  }

}