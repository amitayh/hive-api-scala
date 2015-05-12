package com.wix.hive.commands.contacts

import com.wix.hive.model.contacts.Contact
import org.joda.time.DateTime

case class UpdateDate(contactId: String, dateId: String, date: ContactDateDTO) extends UpdateContactCommand[Contact] {
  override val modifiedAtOption: Option[DateTime] = None

  override def urlParams: String = super.urlParams + s"/date/$dateId"

  override def body: Option[AnyRef] = Some(date)
}

object UpdateDate {
  def apply(contactId: String, modifiedAt: DateTime, dateId: String, date: ContactDateDTO): UpdateDate =
    new UpdateDate(contactId, dateId, date) { override val modifiedAtOption: Option[DateTime] = Some(modifiedAt) }
}

