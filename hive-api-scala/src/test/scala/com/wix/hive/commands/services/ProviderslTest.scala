package com.wix.hive.commands.services

import com.wix.hive.client.http.HttpMethod
import com.wix.hive.drivers.ServicesTestSupport
import com.wix.hive.matchers.HiveMatchers
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

/**
 * User: maximn
 * Date: 1/18/15
 */
class ProviderslTest extends Specification with HiveMatchers {

  class Context extends Scope with ServicesTestSupport {
    val command = providersCommand
  }

  "GET /services/actions/email/providers" should {
    "create HttpRequestData with all parameters" in new Context {
      command.createHttpRequestData must httpRequestDataWith(
        url = be_===("/services/actions/email/providers"),
        method = be_===(HttpMethod.GET))
    }
  }
}
