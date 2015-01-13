package com.wix.hive.commands.services

import com.wix.hive.client.http.HttpMethod
import com.wix.hive.drivers.ServicesTestSupport
import com.wix.hive.matchers.HiveMatchers
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

/**
 * User: maximn
 * Date: 1/7/15
 */
class SendEmailTest extends Specification with HiveMatchers {

  class Context extends Scope with ServicesTestSupport {
    val command = emailCommand
  }

  "services email" should {
    "create HttpRequestData with all parameters" in new Context {
      command.createHttpRequestData must httpRequestDataWith(
        url = be_===("/services/email"),
        method = be_===(HttpMethod.POST),
        body = beSome(EmailServiceData(command.providerId, command.redemptionToken, command.correlationId, command.contacts)))
    }
  }
}