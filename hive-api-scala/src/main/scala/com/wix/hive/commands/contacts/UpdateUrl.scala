package com.wix.hive.commands.contacts

import com.wix.hive.model.contacts.Contact
import org.joda.time.DateTime

case class UpdateUrl(contactId: String, modifiedAt: DateTime, urlId: String, urlToUpdate: ContactUrlDTO) extends UpdateContactCommand[Contact] {
  override def urlParams: String = super.urlParams + s"/url/$urlId"

  override def body: Option[AnyRef] = Some(urlToUpdate)
}
