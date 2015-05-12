package com.wix.hive.commands.contacts

import com.wix.hive.model.contacts.Contact
import org.joda.time.DateTime

case class UpdateUrl(contactId: String, urlId: String, urlToUpdate: ContactUrlDTO) extends UpdateContactCommand[Contact] {
  override val modifiedAtOption: Option[DateTime] = None

  override def urlParams: String = super.urlParams + s"/url/$urlId"

  override def body: Option[AnyRef] = Some(urlToUpdate)
}

object UpdateUrl {
  def apply(contactId: String, modifiedAt: DateTime, urlId: String, urlToUpdate: ContactUrlDTO): UpdateUrl =
    new UpdateUrl(contactId, urlId, urlToUpdate) { override val modifiedAtOption: Option[DateTime] = Some(modifiedAt) }
}

