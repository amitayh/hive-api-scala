package com.wix.hive.commands.contacts

import com.wix.hive.model.contacts.{Contact, ContactName}
import org.joda.time.DateTime

case class UpdateName(contactId: String, name: ContactName, modifiedAt: DateTime) extends UpdateContactCommand[Contact] {
  override def urlParams: String = super.urlParams + "/name"

  override def body: Option[AnyRef] = Some(name)
}