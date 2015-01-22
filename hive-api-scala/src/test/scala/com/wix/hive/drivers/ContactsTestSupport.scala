package com.wix.hive.drivers

import com.wix.hive.commands.contacts._
import com.wix.hive.model.contacts.{PagingContactsResult, EmailStatus, ContactName, Contact}
import org.joda.time.DateTime
import org.specs2.matcher.Matchers._
import org.specs2.matcher.Matcher

/**
 * User: maximn
 * Date: 1/21/15
 */
trait ContactsTestSupport {
  def beContactWithId(matcher: Matcher[String]): Matcher[Contact] = matcher ^^ { (_: Contact).id aka "contactId" }
  def beContactsWith(matcher: Matcher[Seq[Contact]]): Matcher[PagingContactsResult] = matcher ^^ { (_: PagingContactsResult).results aka "results" }


  val contactId = "e5d81850-5dd8-407f-9acc-7ffd6c924ecf"
  val contact = Contact(contactId, new DateTime(2010, 1, 1, 0, 0))
  val anotherContactId = "c34a8709-6b14-4959-9db7-33a584daefad"
  val anotherContact = Contact(anotherContactId, new DateTime(2010, 1, 1, 0, 0))

  val name = ContactName(first = Some("First"), last = Some("Last"))

  val emailId = "48d21810-1a8a-4b69-ba25-8272f598667b"
  val contactEmail = ContactEmailDTO(email = "maximn@wix.com", tag = "emailtag", emailStatus = Some(EmailStatus.OptOut))

  val dateId = "e1157acc-41aa-460e-87e9-7cee90778b06"
  val contactData = ContactData(name = Some(name), emails = Seq(contactEmail))

  val date = new DateTime(2013, 1, 2, 2, 3)
  val contactDate = ContactDateDTO(tag = "date-tag", date)

  val addressId = "9a9cf711-d537-44a5-97e3-d45b7e7ffe53"
  val address = AddressDTO("tag-address-dto")

  val phone = "972-54-5556767"
  val myEmail = "maximn@wix.com"
  val emailStatus = EmailStatus.OptOut

  val modifiedAt = new DateTime(2012, 2, 10, 10, 10)
  val phoneId = "2ac68c77-d4e6-4e37-9e82-bfa2479bb1d1"
  val contactPhone = ContactPhoneDTO("tag-phone-add", phone)

  val urlId = "c8226786-cca9-48a9-8750-a2043c867d35"
  val url = "http://wix.com/somesite"
  val contactUrl = ContactUrlDTO("tag-contact-add", url)

  val contactCompany = CompanyDTO(Some("role-comp"), name.first)
  val contactPicture = PictureDTO("some-pic")
  val contactAddress = AddressDTO("tag-address-contact")
}
