package com.wix.hive.commands.contacts

import com.wix.hive.model.contacts.{Contact, ContactName}
import org.joda.time.DateTime

case class UpdateName(contactId: String, name: ContactName) extends UpdateContactCommand[Contact] {
  override val modifiedAtOption: Option[DateTime] = None

  override def urlParams: String = super.urlParams + "/name"

  override def body: Option[AnyRef] = Some(name)
}

object UpdateName {
  def apply(contactId: String, modifiedAt: DateTime, name: ContactName): UpdateName =
    new UpdateName(contactId, name) { override val modifiedAtOption: Option[DateTime] = Some(modifiedAt) }
}