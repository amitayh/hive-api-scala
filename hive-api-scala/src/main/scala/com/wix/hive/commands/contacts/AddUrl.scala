package com.wix.hive.commands.contacts

import com.wix.hive.model.contacts.Contact
import org.joda.time.DateTime

case class AddUrl(contactId: String, urlToAdd: ContactUrlDTO) extends AddToContactCommand[Contact] {
  override val modifiedAtOption: Option[DateTime] = None

  override val urlParams: String = super.urlParams + "/url"

  override def body: Option[AnyRef] = Some(urlToAdd)
}

object AddUrl {
  def apply(contactId: String, modifiedAt: DateTime, urlToAdd: ContactUrlDTO): AddUrl = new AddUrl(contactId, urlToAdd) {
    override val modifiedAtOption: Option[DateTime] = Some(modifiedAt)
  }
}


case class ContactUrlDTO(tag: String, url: String)