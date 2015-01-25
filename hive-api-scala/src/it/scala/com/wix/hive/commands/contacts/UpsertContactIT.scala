package com.wix.hive.commands.contacts

import com.wix.hive.commands.{BaseHiveCtx, BaseHubIt}
import com.wix.hive.drivers.ContactsTestSupport
import com.wix.hive.drivers.HiveCommandsMatchers._

/**
 * User: maximn
 * Date: 1/25/15
 */
class UpsertContactIT extends BaseHubIt {
    class ctx extends BaseHiveCtx with ContactsTestSupport {
    }

    "Executing upsert contact" should {
      "upsert the contact and return new contact" in new ctx {
        expectUpsertContact(app, upsertCommand)(UpsertContactResponse(contactId))

        client.execute(instance, upsertCommand) must haveUpsertContactId(contactId).await
      }
    }
  }
