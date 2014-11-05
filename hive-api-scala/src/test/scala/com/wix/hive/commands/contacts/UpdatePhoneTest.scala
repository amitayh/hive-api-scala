package com.wix.hive.commands.contacts

import com.wix.hive.client.http.HttpMethod
import com.wix.hive.matchers.HiveMatchers
import com.wix.hive.model.contacts.EmailStatus
import org.specs2.mutable.{SpecificationWithJUnit, Specification}

class UpdatePhoneTest extends SpecificationWithJUnit with HiveMatchers {

  class Context extends ContextForModification {
    val phoneId = "222163d7-7d97-49aa-8ee2-e1af48db4241"
    val phone = ContactPhoneDTO("tag-phone", "+972-54-5554321")

    val cmd = UpdatePhone(contactId, modifiedAt, phoneId, phone)
  }

  "createHttpRequestData" should {
    "work with parameters" in new Context {
      cmd.createHttpRequestData must httpRequestDataWith(
        method = be_===(HttpMethod.PUT),
        url = contain(contactId) and contain("phone") and contain(phoneId),
        query = havePair("modifiedAt", modifiedAt.toString),
        body = beSome(be_==(phone))
      )
    }
  }
}
