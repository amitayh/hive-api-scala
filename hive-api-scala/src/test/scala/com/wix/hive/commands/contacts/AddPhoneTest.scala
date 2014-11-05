package com.wix.hive.commands.contacts

import com.wix.hive.client.http.HttpMethod
import com.wix.hive.matchers.HiveMatchers
import org.specs2.mutable.SpecificationWithJUnit

class AddPhoneTest extends SpecificationWithJUnit with HiveMatchers {
  "createHttpRequestData" should {
    "work with parameters" in new Context {
      cmd.createHttpRequestData must httpRequestDataWith(
        method = be_===(HttpMethod.POST),
        url = contain(contactId) and contain("phone"),
        query = havePair("modifiedAt", modifiedAt.toString),
        body = beSome(be_==(phone))
      )
    }
  }

  class Context extends ContextForModification {
    val phone = ContactPhoneDTO("tag-phone", "972-54-5551234")

    val cmd = AddPhone(contactId, modifiedAt, phone)
  }

}
