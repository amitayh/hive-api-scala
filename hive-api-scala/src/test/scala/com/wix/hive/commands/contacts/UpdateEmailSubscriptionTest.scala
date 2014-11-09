package com.wix.hive.commands.contacts

import com.wix.hive.client.http.HttpMethod
import com.wix.hive.matchers.HiveMatchers
import com.wix.hive.model.contacts.EmailStatus
import org.specs2.mutable.{SpecificationWithJUnit, Specification}

class UpdateEmailSubscriptionTest extends SpecificationWithJUnit with HiveMatchers {

  class Context extends ContextForModification {
    val emailId = "76588154-5d9e-4726-a07f-ddea73c77d57"
    val subscription = EmailStatus.OptOut
    val res = ContactResult(EmailStatus.OptOut)

    val cmd = UpdateEmailSubscription(contactId, modifiedAt, emailId, subscription)
  }

  "createHttpRequestData" should {
    "work with parameters" in new Context {
      cmd.createHttpRequestData must httpRequestDataWith(
        method = be_===(HttpMethod.PUT),
        url = be_===(s"/contacts/$contactId/email/$emailId/subscription"),
        query = havePair("modifiedAt", modifiedAt.toString),
        body = beSome(be_==(res))
      )
    }
  }
}
