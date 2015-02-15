package com.wix.hive.commands.services.email

import com.wix.hive.client.http.HttpMethod
import com.wix.hive.drivers.ServicesTestSupport
import com.wix.hive.matchers.HiveMatchers
import org.specs2.matcher.Scope
import org.specs2.mutable.Specification

/**
 * User: maximn
 * Date: 2/14/15
 */
class SendSingleTest extends Specification with HiveMatchers {

  class Context extends Scope with ServicesTestSupport {
    val command = singleEmailCommand
  }

  "services email single" should {
    "create HttpRequestData with all parameters" in new Context {
      command.createHttpRequestData must httpRequestDataWith(
        url = be_===("/services/actions/email/single"),
        method = be_===(HttpMethod.POST),
        body = beSome(SendSingleData(servicesCorrelationId, Seq(to), mailHeaders, from, subject, html, text)))
    }
  }
}
