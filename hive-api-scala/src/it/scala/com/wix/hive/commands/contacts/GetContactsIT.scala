package com.wix.hive.commands.contacts

import com.wix.hive.commands.{BaseHiveCtx, BaseHubIt}
import com.wix.hive.drivers.ContactsTestSupport
import com.wix.hive.drivers.HiveCommandsMatchers._
import com.wix.hive.model.contacts.PagingContactsResult


/**
 * User: maximn
 * Date: 1/22/15
 */
class GetContactsIT extends BaseHubIt {
    class ctx extends BaseHiveCtx with ContactsTestSupport {
    }

    "Executing get contact" should {
      "return the contacts" in new ctx {
        givenAppWithContacts(app)(PagingContactsResult(total =2, pageSize = 25, previous = None, next=None, results= Seq(contact, anotherContact)))

        client.execute(instance, GetContacts()) must beContactsWith(contain(allOf(beContactWithId(contactId), beContactWithId(anotherContactId)))).await
      }
    }
  }

