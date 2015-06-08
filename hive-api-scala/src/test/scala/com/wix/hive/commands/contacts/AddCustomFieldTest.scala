package com.wix.hive.commands.contacts

import com.wix.hive.client.http.HttpMethod
import com.wix.hive.matchers.HiveMatchers
import org.specs2.mutable.SpecificationWithJUnit

class AddCustomFieldTest extends SpecificationWithJUnit with HiveMatchers {
  "createHttpRequestData" should {
    "work with parameters" in new Context {
      cmd.createHttpRequestData must httpRequestDataWith(
        method = be_===(HttpMethod.POST),
        url = be_===(s"/contacts/$contactId/custom"),
        query = havePair("modifiedAt", modifiedAt.toString),
        body = beSome(customField)
      )
    }
  }

  class Context extends ContextForModification {
    val customField = ContactCustomFieldDTO("time zone", "Eastern Time Zone (UTC-05:00)")

    val cmd = AddCustomField(contactId, customField, modifiedAt)
  }

}
