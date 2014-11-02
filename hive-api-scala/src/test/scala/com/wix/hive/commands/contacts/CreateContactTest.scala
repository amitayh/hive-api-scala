package com.wix.hive.commands.contacts

import com.wix.hive.commands.CreateContact
import com.wix.hive.model._
import org.joda.time.DateTime
import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.specification.Scope

class CreateContactTest extends SpecificationWithJUnit {
  skipAll
  "createHttpRequestData" should {
    "work with parameters" in new Context {
      val httpData = command.createHttpRequestData

      val data = httpData.body
    }.pendingUntilFixed("Not implemented yet")

    "create HttpRequestData with empty parameters" in new Context {
      val emptyCommand = CreateContact()

      val httpData = emptyCommand.createHttpRequestData

      httpData.body must beNone
    }.pendingUntilFixed("Not implemented yet")
  }

  class Context extends Scope {
    val contactName = ContactName(Some("Mr"), Some("First"), Some("Middle"), Some("Last"), Some("SUFFIX"))
    val company = Company(Some("Role"), Some("Name"), Some("Middle"))
    val tags = Seq("tag1", "tag2")
    val emails = Seq(ContactEmail("emlTag1,Tag2", "eml1@google.com", EmailStatus.Recurring, Some(2)), ContactEmail("TG_", "fir.lst@yahoo.co.il", EmailStatus.OptOut, Some(1)))
    val phones = Seq(ContactPhone("phnTag", "+972-54-5551234321", Some("972545551234321"), Some(3)))
    val address = Seq(Address("adTag", Some(4), Some("some address, in TLV"), Some("neigberhood"), Some("TelAviv"), Some("Central District"), Some("Israel"), Some("40700")))
    val urls = Seq(ContactUrl("ur1Tag", "http://wix.com", Some(5)))
    val dates = Seq(ImportantDate("Birthday", new DateTime(1985, 2, 11, 0, 0, 0, 0), Some(6)))
    val notes = Seq(Note(Some(new DateTime(2014, 3, 21, 10, 35, 0, 0)), "first note", Some(7)))
    val custom = Seq(CustomField("filed_name", "value for a custom field", Some(8)))

    val command = CreateContact(Some(contactName), Some("pic.jpg"), Some(company), tags, emails, phones, address, urls, dates, notes, custom)
  }

}