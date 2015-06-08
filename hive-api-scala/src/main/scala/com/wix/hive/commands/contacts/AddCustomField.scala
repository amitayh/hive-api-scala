package com.wix.hive.commands.contacts

import com.wix.hive.model.contacts.Contact
import org.joda.time.DateTime


case class AddCustomField(contactId: String, customField: ContactCustomFieldDTO) extends AddToContactCommand[Contact] {
  override val modifiedAtOption: Option[DateTime] = None

  override val urlParams: String = super.urlParams + "/custom"

  override val body: Option[AnyRef] = Some(customField)
}

object AddCustomField {
  def apply(contactId: String, modifiedAt: DateTime, customField: ContactCustomFieldDTO): AddCustomField =
    new AddCustomField(contactId, customField) { override val modifiedAtOption: Option[DateTime] = Some(modifiedAt) }
}

case class ContactCustomFieldDTO(field: String, value: String)