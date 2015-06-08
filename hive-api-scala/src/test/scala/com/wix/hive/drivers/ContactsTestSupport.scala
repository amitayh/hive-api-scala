package com.wix.hive.drivers

import com.wix.hive.commands.contacts._
import com.wix.hive.model.contacts.{PagingContactsResult, Contact, ContactName, EmailStatus}
import org.joda.time.DateTime
import org.specs2.matcher.Matcher
import org.specs2.matcher.Matchers._

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

  val createContactResult = CreatedContact(contactId)

  val upsertCommand = UpsertContact(Some(phone), Some(myEmail))
  val addAddressCommand = AddAddress(contactId, address, modifiedAt)
  val getContactByIdCommand = GetContactById(contactId)
  val getContactsCommand =GetContacts()
  val addEmailCommand = AddEmail(contactId, contactEmail, modifiedAt)
  val addPhoneCommand = AddPhone(contactId, contactPhone, modifiedAt)
  val addUrlCommand = AddUrl(contactId, contactUrl, modifiedAt)
  val addDateCommand = AddDate(contactId, contactDate, modifiedAt)
  val updateNameCommand = UpdateName(contactId, name, modifiedAt)
  val updateCompanyCommand = UpdateCompany(contactId, contactCompany, modifiedAt)
  val updateAddressCommand = UpdateAddress(contactId, addressId, contactAddress, modifiedAt)
  val updateEmailCommand = UpdateEmail(contactId, emailId, contactEmail, modifiedAt)
  val updatePhoneCommand = UpdatePhone(contactId, phoneId, contactPhone, modifiedAt)
  val updateUrlCommand = UpdateUrl(contactId, urlId, contactUrl, modifiedAt)
  val updateDateCommand = UpdateDate(contactId, dateId, contactDate, modifiedAt)
  val createContactCommand = CreateContact()
}

object ContactsTestSupport extends ContactsTestSupport