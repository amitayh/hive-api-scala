package com.wix.hive.commands.contacts

import com.wix.hive.client.http.HttpMethod
import com.wix.hive.matchers.HiveMatchers
import com.wix.hive.model.contacts.EmailStatus
import org.specs2.mutable.SpecificationWithJUnit

class UpdateEmailTest extends SpecificationWithJUnit with HiveMatchers {

  class Context extends ContextForModification {
    val emailId = "222163d7-7d97-49aa-8ee2-e1af48db4241"
    val email = ContactEmailDTO("tag-address", "some@wix.com", EmailStatus.OptOut)

    val cmd = UpdateEmail(contactId, modifiedAt, emailId, email)
  }

  "createHttpRequestData" should {
    "work with parameters" in new Context {
      cmd.createHttpRequestData must httpRequestDataWith(
        method = be_===(HttpMethod.PUT),
        url = be_===(s"/contacts/$contactId/email/$emailId"),
        query = havePair("modifiedAt", modifiedAt.toString),
        body = beSome(be_==(email))
      )
    }
  }
}
