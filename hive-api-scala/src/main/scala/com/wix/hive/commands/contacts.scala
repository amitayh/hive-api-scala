package com.wix.hive.commands

import com.fasterxml.jackson.module.scala.JsonScalaEnumeration
import com.wix.hive.client.http.HttpMethod
import com.wix.hive.client.http.HttpMethod._
import com.wix.hive.commands.common.PageSizes
import PageSizes.PageSizes
import com.wix.hive.model.EmailStatus.EmailStatus
import com.wix.hive.model._

trait ContactsCommand[TResponse] extends HiveBaseCommand[TResponse] {
  override val url: String = "/contacts"
}

case class CreateContact(name: Option[ContactName] = None, picture: Option[String] = None, company: Option[Company] = None,
                         emails: Seq[ContactEmailDTO] = Nil, phones: Seq[ContactPhoneDTO] = Nil,
                         addresses: Seq[Address] = Nil, urls: Seq[ContactUrl] = Nil, dates: Seq[ImportantDate] = Nil)
  extends ContactsCommand[CreatedContact] {
  override val method = HttpMethod.POST

  override val body = Some(ContactData(name, picture, company, emails, phones, addresses, urls, dates))
}

case class ContactData(name: Option[ContactName] = None, picture: Option[String] = None, company: Option[Company] = None,
                       emails: Seq[ContactEmailDTO] = Nil, phones: Seq[ContactPhoneDTO] = Nil,
                       addresses: Seq[Address] = Nil, urls: Seq[ContactUrl] = Nil, dates: Seq[ImportantDate] = Nil)

case class ContactEmailDTO(tag: String, email: String, @JsonScalaEnumeration(classOf[EmailStatusRef])emailStatus: EmailStatus)
case class ContactPhoneDTO(tag: String, phone: String)


case class CreatedContact(contactId: String)

case class GetContactById(id: String) extends ContactsCommand[Contact] {
  override val method: HttpMethod = HttpMethod.GET

  override val urlParams = s"/$id"
}

case class GetContacts(tag: Seq[String] = Nil, email: Option[String] = None, phone: Option[String] = None,
                       firstName: Option[String] = None, lastName: Option[String] = None, cursor: Option[String] = None,
                       pageSize: Option[PageSizes] = None) extends ContactsCommand[PagingContactsResult] {

  override val method = HttpMethod.GET

  override val query = {
    super.removeOptionalParameters({
      import GetContacts.QueryKeys
      Map(
        QueryKeys.tag -> tag,
        QueryKeys.email -> email,
        QueryKeys.phone -> phone,
        QueryKeys.firstName -> firstName,
        QueryKeys.lastName -> lastName,
        QueryKeys.cursor -> cursor,
        QueryKeys.pageSize -> pageSize
      )
    })
  }
}

object GetContacts {

  object QueryKeys {
    val tag = "tag"
    val email = "email"
    val phone = "phone"
    val firstName = "firstName"
    val lastName = "lastName"
    val cursor = "cursor"
    val pageSize = "pageSize"
  }

}


case class UpsertContact(phone: Option[String], email: Option[String]) extends ContactsCommand[UpsertContactResponse] {
  override val method = HttpMethod.PUT
}

case class UpsertContactResponse(id: String)
