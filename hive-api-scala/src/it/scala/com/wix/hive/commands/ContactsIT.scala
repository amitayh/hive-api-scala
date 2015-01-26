package com.wix.hive.commands

import com.wix.hive.commands.contacts.{UpsertContactResponse, GetContacts, GetContactById}
import com.wix.hive.drivers.ContactsTestSupport
import com.wix.hive.drivers.HiveCommandsMatchers._
import com.wix.hive.model.contacts.PagingContactsResult

/**
 * User: maximn
 * Date: 1/26/15
 */
class ContactsIT extends BaseHubIt {

  class ctx extends BaseHiveCtx with ContactsTestSupport {

  }

  "Executing add address" should {
    "return the contact" in new ctx {
      expectAddAddress(app, addAddressCommand)(contact)

      client.execute(instance, addAddressCommand) must beContactWithId(contactId).await
    }
  }

  "Executing add email" should {
    "return the contact" in new ctx {
      expectAddEmail(app, addEmailCommand)(contact)

      client.execute(instance, addEmailCommand) must beContactWithId(contactId).await
    }
  }

  "Executing get contact by id" should {
    "return the contact" in new ctx {
      givenContactFetchById(app, contact)

      client.execute(instance, GetContactById(contactId)) must beContactWithId(contactId).await
    }
  }

  "Executing get contact" should {
    "return the contacts" in new ctx {
      givenAppWithContacts(app)(PagingContactsResult(total = 2, pageSize = 25, previous = None, next = None, results = Seq(contact, anotherContact)))

      client.execute(instance, GetContacts()) must beContactsWith(contain(allOf(beContactWithId(contactId), beContactWithId(anotherContactId)))).await
    }
  }

  "Executing upsert contact" should {
    "upsert the contact and return new contact" in new ctx {
      expectUpsertContact(app, upsertCommand)(UpsertContactResponse(contactId))

      client.execute(instance, upsertCommand) must haveUpsertContactId(contactId).await
    }
  }

}
