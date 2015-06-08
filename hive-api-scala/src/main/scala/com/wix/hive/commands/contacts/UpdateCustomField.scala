package com.wix.hive.commands.contacts

import com.wix.hive.model.contacts.{ContactName, Contact}
import org.joda.time.DateTime

case class UpdateCustomField(contactId: String, modifiedAt: DateTime, customFieldId: String, customField: ContactCustomFieldDTO) extends UpdateContactCommand[Contact] {
  override def urlParams: String = super.urlParams + s"/custom/$customFieldId"

  override def body: Option[AnyRef] = Some(customField)
}
