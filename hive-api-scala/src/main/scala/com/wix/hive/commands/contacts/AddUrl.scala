package com.wix.hive.commands.contacts

import com.wix.hive.model.contacts.Contact
import org.joda.time.DateTime

case class AddUrl(contactId: String, urlToAdd: ContactUrlDTO, modifiedAt: DateTime) extends AddToContactCommand[Contact] {
  override val urlParams: String = super.urlParams + "/url"

  override def body: Option[AnyRef] = Some(urlToAdd)
}


case class ContactUrlDTO(tag: String, url: String)