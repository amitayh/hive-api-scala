package com.wix.hive.commands.contacts

import com.wix.hive.client.http.HttpMethod
import com.wix.hive.matchers.HiveMatchers
import org.specs2.mutable.SpecificationWithJUnit

class UpdateCustomFieldTest extends SpecificationWithJUnit with HiveMatchers {

  class Context extends ContextForModification {
    val customFieldId = "222163d7-7d97-49aa-8ee2-e1af48db4241"
    val customField = ContactCustomFieldDTO("field1", "value1")

    val cmd = UpdateCustomField(contactId, modifiedAt, customFieldId, customField)
  }

  "createHttpRequestData" should {
    "work with parameters" in new Context {
      cmd.createHttpRequestData must httpRequestDataWith(
        method = be_===(HttpMethod.PUT),
        url = be_===(s"/contacts/$contactId/custom/$customFieldId"),
        query = havePair("modifiedAt", modifiedAt.toString),
        body = beSome(customField)
      )
    }
  }
}
