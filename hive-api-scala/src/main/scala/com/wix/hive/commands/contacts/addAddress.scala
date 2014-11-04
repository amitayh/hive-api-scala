package com.wix.hive.commands.contacts

import com.wix.hive.model.contacts.Contact
import org.joda.time.DateTime

case class AddContactAddress(contactId: String, modifiedAt: DateTime, address: AddressDTO) extends AddToContactCommand[Contact] {
  override def urlParams: String = super.urlParams + "/address"

  override def body: Option[AnyRef] = Some(address)
}

case class AddressDTO(tag: String, address: Option[String] = None, neighborhood: Option[String] = None, city: Option[String] = None,
                      region: Option[String] = None, country: Option[String] = None, postalCode: Option[String] = None)

