package com.wix.hive.commands.contacts

import com.wix.hive.model.contacts.Contact
import org.joda.time.DateTime

case class AddDate(contactId: String, date: ContactDateDTO) extends AddToContactCommand[Contact] {
  override val modifiedAtOption: Option[DateTime] = None
  override val urlParams: String = super.urlParams + "/date"

  override def body: Option[AnyRef] = Some(date)
}

object AddDate {
  def apply(contactId: String, modifiedAt: DateTime, date: ContactDateDTO): AddDate = new AddDate(contactId, date) {
    override val modifiedAtOption: Option[DateTime] = Option(modifiedAt)
  }
}

case class ContactDateDTO(tag: String, date: DateTime)