package com.wix.hive.commands.contacts

import com.wix.hive.model.contacts.Contact
import org.joda.time.DateTime


case class AddCustomField(contactId: String, customField: ContactCustomFieldDTO, modifiedAt: DateTime) extends AddToContactCommand[Contact] {
  override val urlParams: String = super.urlParams + "/custom"

  override val body: Option[AnyRef] = Some(customField)
}

case class ContactCustomFieldDTO(field: String, value: String)