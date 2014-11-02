package com.wix.hive.commands

import com.wix.hive.client.http.HttpMethod
import com.wix.hive.client.http.HttpMethod._
import com.wix.hive.model.PageSizes.PageSizes
import com.wix.hive.model._

case class CreateContact(name: Option[ContactName] = None, picture: Option[String] = None, company: Option[Company] = None,
                         tags: Seq[String] = Nil, emails: Seq[ContactEmail] = Nil, phone: Seq[ContactPhone] = Nil,
                         addresses: Seq[Address] = Nil, urls: Seq[ContactUrl] = Nil, dates: Seq[ImportantDate] = Nil,
                         notes: Seq[Note] = Nil, custom: Seq[CustomField] = Nil) extends ContactsCommand[CreatedContact] {
  override val method = HttpMethod.POST
}

case class CreatedContact(id: String)

case class GetContactById(id: String) extends ContactsCommand[Contact] {
  override val method: HttpMethod = HttpMethod.GET

  override val urlParams = s"/$id"
}

case class GetContacts(tag: Seq[String] = Nil, email: Option[String] = None, phone: Option[String] = None,
                       firstName: Option[String] = None, lastName: Option[String] = None, cursor: Option[String] = None, pageSize: Option[PageSizes] = None) extends ContactsCommand[PagingContactsResult] {

  override val method = HttpMethod.GET

  override val query = {
    val tagsParam = if (tag.nonEmpty) Map("tag" -> tag.mkString(",")) else Map[String, String]()
    val emailParam = email.map("email" -> _).toMap
    val phoneParam = phone.map("phone" -> _).toMap
    val firstNameParam = firstName.map("firstName" -> _).toMap
    val lastNameParam = lastName.map("lastName" -> _).toMap
    val cursorParam = cursor.map("cursor" -> _).toMap
    val pageSizeParam = pageSize.map("pageSize" -> _.toString).toMap

    tagsParam ++ emailParam ++ phoneParam ++ firstNameParam ++ lastNameParam ++ cursorParam ++ pageSizeParam
  }
}


trait ContactsCommand[TResponse] extends HiveBaseCommand[TResponse]{
  override val url: String = "/contacts"
}

case class UpsertContact(phone: Option[String], email: Option[String]) extends ContactsCommand[UpsertContactResponse] {
  override val method = HttpMethod.PUT
}

case class UpsertContactResponse(id: String)
