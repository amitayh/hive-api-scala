package com.wix.hive.commands.contacts

import com.wix.hive.client.http.HttpMethod
import com.wix.hive.commands.contacts.PageSizes.PageSizes
import com.wix.hive.model.PagingContactsResult

case class GetContacts(tag: Seq[String] = Nil, email: Option[String] = None, phone: Option[String] = None,
                       firstName: Option[String] = None, lastName: Option[String] = None, cursor: Option[String] = None, pageSize: Option[PageSizes] = None) extends ContactsCommand[GetContactsResponse] {

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

case class GetContactsResponse(results: PagingContactsResult)

object PageSizes extends Enumeration {
  type PageSizes = Value
  val `25`, `50`, `100` = Value
}
