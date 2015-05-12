package com.wix.hive.commands.contacts

import com.wix.hive.model.contacts.Contact
import org.joda.time.DateTime

case class AddAddress(contactId: String, address: AddressDTO) extends AddToContactCommand[Contact] {
  override val modifiedAtOption: Option[DateTime] = None

  override val urlParams: String = super.urlParams + "/address"

  override val body: Option[AnyRef] = Some(address)
}

object AddAddress {
  def apply(contactId: String, modifiedAt: DateTime, address: AddressDTO): AddAddress =
    new AddAddress(contactId, address) { override val modifiedAtOption: Option[DateTime] = Some(modifiedAt) }
}



