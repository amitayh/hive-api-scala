package com.wix.hive.commands.contacts

import com.wix.hive.commands.{BaseHiveCtx, BaseHubIt}
import com.wix.hive.drivers.ContactsTestSupport
import com.wix.hive.drivers.HiveCommandsMatchers._

/**
 * User: maximn
 * Date: 1/26/15
 */
class AddAddressIT extends BaseHubIt {

  class ctx extends BaseHiveCtx with ContactsTestSupport {
  }

  "Executing add address" should {
    "return the contact" in new ctx {
      expectAddAddress(app, addAddressCommand)(contact)

      client.execute(instance, addAddressCommand) must beContactWithId(contactId).await
    }
  }
}