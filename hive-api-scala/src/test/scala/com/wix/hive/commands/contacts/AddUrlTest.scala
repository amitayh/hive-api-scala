package com.wix.hive.commands.contacts

import com.wix.hive.client.http.HttpMethod
import com.wix.hive.matchers.HiveMatchers
import org.specs2.mutable.SpecificationWithJUnit

class AddUrlTest extends SpecificationWithJUnit with HiveMatchers {
  "createHttpRequestData" should {
    "work with parameters" in new Context {
      cmd.createHttpRequestData must httpRequestDataWith(
        method = be_===(HttpMethod.POST),
        url = be_===(s"/contacts/$contactId/url"),
        query = havePair("modifiedAt", modifiedAt.toString),
        body = beSome(url)
      )
    }
  }

  class Context extends ContextForModification {
    val url = ContactUrlDTO("contact-tag", "http://wix.com")

    val cmd = AddUrl(contactId, url, modifiedAt)
  }

}