package com.wix.hive.commands.contacts

import com.wix.hive.model._
import org.joda.time.DateTime
import org.specs2.execute.Pending
import org.specs2.mutable.{SpecificationWithJUnit}
import org.specs2.specification.Scope

class CreateContactTest extends SpecificationWithJUnit {

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
    val emails = Seq(ContactEmail(Some(1), "emlTag1,Tag2", "eml1@google.com", EmailStatus.Recurring), ContactEmail(Some(2), "TG_", "fir.lst@yahoo.co.il", EmailStatus.OptOut))
    val phones = Seq(ContactPhone(Some(3), "phnTag", "+972-54-5551234321", Some("972545551234321")))
    val address = Seq(Address(Some(4), "adTag", Some("some address, in TLV"), Some("neigberhood"), Some("TelAviv"), Some("Central District"), Some("Israel"), Some("40700")))
    val urls = Seq(ContactUrl(Some(5), "ur1Tag", "http://wix.com"))
    val dates = Seq(ImportantDate(Some(6), "Birthday", new DateTime(1985, 2, 11, 0, 0, 0, 0)))
    val notes = Seq(Note(Some(7), Some(new DateTime(2014, 3, 21, 10, 35, 0, 0)), "first note"))
    val custom = Seq(CustomField(Some(8), "filed_name", "value for a custom field"))

    val command = CreateContact(Some(contactName), Some("pic.jpg"), Some(company), tags, emails, phones, address, urls, dates, notes, custom)
  }

}