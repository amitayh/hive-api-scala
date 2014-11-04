package com.wix.hive.commands.contacts

import com.fasterxml.jackson.module.scala.JsonScalaEnumeration
import com.wix.hive.client.http.HttpMethod
import com.wix.hive.model.contacts.EmailStatus._
import com.wix.hive.model.contacts.{Address, Company, ContactName, EmailStatusRef, _}

case class CreateContact(name: Option[ContactName] = None, picture: Option[String] = None, company: Option[Company] = None,
                         emails: Seq[ContactEmailDTO] = Nil, phones: Seq[ContactPhoneDTO] = Nil,
                         addresses: Seq[Address] = Nil, urls: Seq[ContactUrl] = Nil, dates: Seq[ImportantDate] = Nil)
  extends ContactsCommand[CreatedContact] {
  override val method = HttpMethod.POST

  override val body = Some(ContactData(name, picture, company, emails, phones, addresses, urls, dates))
}


case class ContactData(name: Option[ContactName] = None, picture: Option[String] = None, company: Option[Company] = None,
                       emails: Seq[ContactEmailDTO] = Nil, phones: Seq[ContactPhoneDTO] = Nil,
                       addresses: Seq[Address] = Nil, urls: Seq[ContactUrl] = Nil, dates: Seq[ImportantDate] = Nil)

case class ContactPhoneDTO(tag: String, phone: String)

case class ContactEmailDTO(tag: String, email: String, @JsonScalaEnumeration(classOf[EmailStatusRef]) emailStatus: EmailStatus)
