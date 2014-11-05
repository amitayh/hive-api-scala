package com.wix.hive.commands.contacts

import com.wix.hive.client.http.HttpMethod
import com.wix.hive.model.contacts.{Address, Company, ContactName, _}

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




