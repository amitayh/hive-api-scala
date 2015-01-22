package com.wix.hive.commands.contacts

import com.wix.hive.commands.{BaseHiveCtx, BaseHubIt}
import com.wix.hive.drivers.ContactsTestSupport
import com.wix.hive.drivers.HiveCommandsMatchers._

/**
 * User: maximn
 * Date: 1/21/15
 */
class GetContactByIdIT extends BaseHubIt {

  class ctx extends BaseHiveCtx with ContactsTestSupport {
  }

  "Executing get contact by id" should {
    "return the contact" in new ctx {
      givenContactFetchById(app, contact)

      client.execute(instance, GetContactById(contactId)) must beContactWithId(contactId).await
    }
  }
}