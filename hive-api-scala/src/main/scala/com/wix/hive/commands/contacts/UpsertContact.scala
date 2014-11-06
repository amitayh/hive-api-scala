package com.wix.hive.commands.contacts

import com.wix.hive.client.http.HttpMethod
import com.wix.hive.commands.contacts.UpsertContact.BodyKeys

case class UpsertContact(phone: Option[String] = None, email: Option[String] = None) extends ContactsCommand[UpsertContactResponse] {
  override val method = HttpMethod.PUT

  override def body: Option[AnyRef] = {
    val map = super.mapToStrings(Map(BodyKeys.phone -> phone, BodyKeys.email -> email))
    if (map.nonEmpty) Some(map) else None
  }
}


object UpsertContact {

  object BodyKeys {
    val phone = "phone"
    val email = "email"
  }

}


case class UpsertContactResponse(contactId: String)


