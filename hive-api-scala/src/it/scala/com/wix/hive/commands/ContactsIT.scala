package com.wix.hive.commands

import com.wix.hive.commands.contacts._
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

  "Contacts operations" should {
    "add address to contact" in new ctx {
      expect(app, addAddressCommand)(contact)

      client.execute(instance, addAddressCommand) must beContactWithId(contactId).await
    }


    "add an email to contact" in new ctx {
      expect(app, addEmailCommand)(contact)

      client.execute(instance, addEmailCommand) must beContactWithId(contactId).await
    }

    "get contact by id" in new ctx {
      expect(app, getContactByIdCommand)(contact)

      client.execute(instance, getContactByIdCommand) must beContactWithId(contactId).await
    }

    "get contacts with filtering" in new ctx {
      expect(app, getContactsCommand)(PagingContactsResult(total = 2, pageSize = 25, previous = None, next = None, results = Seq(contact, anotherContact)))

      client.execute(instance, getContactsCommand) must beContactsWith(contain(allOf(beContactWithId(contactId), beContactWithId(anotherContactId)))).await
    }

    "upsert the contact" in new ctx {
      expect(app, upsertCommand)(UpsertContactResponse(contactId))

      client.execute(instance, upsertCommand) must haveUpsertContactId(contactId).await
    }

    "add phone to contact" in new ctx {
      expect(app, addPhoneCommand)(contact)

      client.execute(instance, addPhoneCommand) must beContactWithId(contactId).await
    }

    "add URL to contact" in new ctx {
      expect(app, addUrlCommand)(contact)

      client.execute(instance, addUrlCommand) must beContactWithId(contactId).await
    }

    "add date to contact" in new ctx {
      expect(app, addDateCommand)(contact)

      client.execute(instance, addDateCommand) must beContactWithId(contactId).await
    }

    "update contact's name" in new ctx {
      expect(app, updateNameCommand)(contact)

      client.execute(instance, updateNameCommand) must beContactWithId(contactId).await
    }

    "update contact's company" in new ctx {
      expect(app, updateCompanyCommand)(contact)

      client.execute(instance, updateCompanyCommand) must beContactWithId(contactId).await
    }

    "update contact's address" in new ctx {
      expect(app, updateAddressCommand)(contact)

      client.execute(instance, updateAddressCommand) must beContactWithId(contactId).await
    }

    "update contact's email" in new ctx {
      expect(app, updateEmailCommand)(contact)

      client.execute(instance, updateEmailCommand) must beContactWithId(contactId).await
    }

    "update cotnact's phone" in new ctx {
      expect(app, updatePhoneCommand)(contact)

      client.execute(instance, updatePhoneCommand) must beContactWithId(contactId).await
    }

    "update contact's url" in new ctx {
      expect(app, updateUrlCommand)(contact)

      client.execute(instance, updateUrlCommand) must beContactWithId(contactId).await
    }

    "update contact's date" in new ctx {
      expect(app, updateDateCommand)(contact)

      client.execute(instance, updateDateCommand) must beContactWithId(contactId).await
    }

  }
}
