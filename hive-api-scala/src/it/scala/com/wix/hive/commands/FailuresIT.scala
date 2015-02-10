package com.wix.hive.commands

import com.wix.hive.drivers.ContactsTestSupport
import com.wix.hive.infrastructure.HiveSimplicatorIT
import com.wix.hive.model.WixAPIErrorException

/**
 * User: maximn
 * Date: 2/3/15
 */
class FailuresIT extends HiveSimplicatorIT with ContactsTestSupport {
  skipAll

  class clientContext extends HiveClientContext

  "When no server listening" should {
    "throw WixApiErrorException(404)" in new clientContext {
      client.execute(instance, addAddressCommand) must throwA[WixAPIErrorException].await
    }
  }

  "When server returning Not Found(404)" should {
    "throw WixApiErrorException(404)" in new clientContext {
      expectError(app, addAddressCommand)(WixAPIErrorException(404))

      client.execute(instance, addAddressCommand) must throwA[WixAPIErrorException].await
    }
  }

  "Server error code 4XX" should {
    "throw WixApiErrorException(400)" in new clientContext {
      expectError(app, addAddressCommand)(WixAPIErrorException(400))

      client.execute(instance, addAddressCommand) must throwA[WixAPIErrorException].await
    }

    "throw WixApiErrorException(408)" in new clientContext {
      expectError(app, addAddressCommand)(WixAPIErrorException(408))

      client.execute(instance, addAddressCommand) must throwA[WixAPIErrorException].await
    }
  }

  "Status code 200 but non-json response" should{
    "throw WixApiErrorException(500)" in new clientContext {
      expectCustom(app, addAddressCommand)("<test>fmejwklf7e89wf632g1$#@#^%&^%*</test>")

      client.execute(instance, addAddressCommand) must throwA[WixAPIErrorException].await
    }
  }
}
