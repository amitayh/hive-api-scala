package com.wix.hive.commands.contacts

import com.wix.hive.model.contacts.Contact
import org.joda.time.DateTime

case class AddDate(contactId: String, date: ContactDateDTO, modifiedAt: DateTime) extends AddToContactCommand[Contact] {
  override val urlParams: String = super.urlParams + "/date"

  override def body: Option[AnyRef] = Some(date)
}

case class ContactDateDTO(tag: String, date: DateTime)