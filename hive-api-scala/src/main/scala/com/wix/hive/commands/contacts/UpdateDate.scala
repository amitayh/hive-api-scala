package com.wix.hive.commands.contacts

import com.wix.hive.model.contacts.Contact
import org.joda.time.DateTime

case class UpdateDate(contactId: String, modifiedAt: DateTime, dateId: String, date: ContactDateDTO) extends UpdateContactCommand[Contact] {
  override def urlParams: String = super.urlParams + s"/date/$dateId"

  override def body: Option[AnyRef] = Some(date)
}
