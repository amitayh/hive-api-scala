package com.wix.hive.commands

import com.fasterxml.jackson.module.scala.JsonScalaEnumeration
import com.wix.hive.client.http.{NamedParameters, HttpMethod}
import com.wix.hive.client.http.HttpMethod._
import com.wix.hive.commands.UpsertContact.BodyKeys
import com.wix.hive.commands.common.PageSizes
import PageSizes.PageSizes
import com.wix.hive.model.EmailStatus.EmailStatus
import com.wix.hive.model._
import org.joda.time.DateTime

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

case class ContactEmailDTO(tag: String, email: String, @JsonScalaEnumeration(classOf[EmailStatusRef]) emailStatus: EmailStatus)

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


case class UpsertContact(phone: Option[String] = None, email: Option[String] = None) extends ContactsCommand[UpsertContactResponse] {
  override val method = HttpMethod.PUT

  override def body: Option[AnyRef] = {
    val map = super.removeOptionalParameters(Map(BodyKeys.phone -> phone, BodyKeys.email -> email))
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


trait AddToContactCommand[TResult] extends ContactsCommand[TResult] {
  val modifiedAtKey = "modifiedAt"

  val contactId: String
  val modifiedAt: DateTime

  override def method: HttpMethod = POST

  override def query: NamedParameters = Map(
    modifiedAtKey -> modifiedAt.toString
  )

  override def urlParams: String = s"/$contactId"
}


case class AddContactAddress(contactId: String, modifiedAt: DateTime, address: AddressDTO) extends AddToContactCommand[Contact] {
  override def urlParams: String = super.urlParams + "/address"

  override def body: Option[AnyRef] = Some(address)
}


case class AddressDTO(tag: String, address: Option[String] = None, neighborhood: Option[String] = None, city: Option[String] = None,
                      region: Option[String] = None, country: Option[String] = None, postalCode: Option[String] = None)


case class AddContactEmail(contactId: String, modifiedAt: DateTime, email: ContactEmailDTO) extends AddToContactCommand[Contact] {
  override def urlParams: String = super.urlParams + "/email"

  override def body: Option[AnyRef] = Some(email)
}