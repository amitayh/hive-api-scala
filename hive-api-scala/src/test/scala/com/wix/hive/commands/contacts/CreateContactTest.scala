package com.wix.hive.commands.contacts

import com.wix.hive.client.http.HttpMethod
import com.wix.hive.matchers.HiveMatchers
import com.wix.hive.model.contacts._
import org.joda.time.DateTime
import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.specification.Scope

class CreateContactTest extends SpecificationWithJUnit with HiveMatchers {
  "createHttpRequestData" should {
    "work with parameters" in new Context {
      cmd.createHttpRequestData must httpRequestDataWith(
      method = be_===(HttpMethod.POST),
      url = be_===("/contacts"),
      body = beSome(ContactData(cmd.name, cmd.picture, cmd.company, cmd.emails, cmd.phones, cmd.addresses, cmd.urls, cmd.dates))
      )
    }
  }

  class Context extends Scope {
    val contactName = ContactName(Some("Mr"), Some("First"), Some("Middle"), Some("Last"), Some("SUFFIX"))
    val company = Company(Some("Role"), Some("Name"), Some("Middle"))
    val tags = Seq("tag1", "tag2")
    val emails = Seq(ContactEmailDTO("emlTag1,Tag2", "eml1@google.com", EmailStatus.Recurring), ContactEmailDTO("TG_", "fir.lst@yahoo.co.il", EmailStatus.OptOut))
    val phones = Seq(ContactPhoneDTO("phnTag", "+972-54-5551234321"))
    val address = Seq(ContactAddress("adTag", Some("some address, in TLV"), Some("neigberhood"), Some("TelAviv"), Some("Central District"), Some("Israel"), Some("40700")))
    val urls = Seq(ContactUrl("ur1Tag", "http://wix.com", Some(5)))
    val dates = Seq(ImportantDate("Birthday", new DateTime(1985, 2, 11, 0, 0, 0, 0), Some(6)))
    val notes = Seq(Note(Some(new DateTime(2014, 3, 21, 10, 35, 0, 0)), "first note", Some(7)))
    val custom = Seq(CustomField("filed_name", "value for a custom field", Some(8)))

    val cmd = CreateContact(Some(contactName), Some("pic.jpg"), Some(company), emails, phones, address, urls, dates)
  }

}