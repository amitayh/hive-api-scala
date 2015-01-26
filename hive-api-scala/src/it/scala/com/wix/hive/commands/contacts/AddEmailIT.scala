package com.wix.hive.commands.contacts

import com.wix.hive.commands.{BaseHiveCtx, BaseHubIt}
import com.wix.hive.drivers.ContactsTestSupport
import com.wix.hive.drivers.HiveCommandsMatchers._

/**
 * User: maximn
 * Date: 1/26/15
 */
class AddEmailIT extends BaseHubIt {

  class ctx extends BaseHiveCtx with ContactsTestSupport {
  }

  "Executing add email" should {
    "return the contact" in new ctx {
      expectAddEmail(app, addEmailCommand)(contact)

      client.execute(instance, addEmailCommand) must beContactWithId(contactId).await
    }
  }
}
