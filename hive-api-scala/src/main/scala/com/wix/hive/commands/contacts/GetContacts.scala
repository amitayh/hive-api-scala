package com.wix.hive.commands.contacts

import com.wix.hive.client.http.HttpMethod
import com.wix.hive.commands.common.PageSizes._
import com.wix.hive.model.contacts.PagingContactsResult


case class GetContacts(labels: Seq[String] = Nil,
                       email: Option[String] = None,
                       phone: Option[String] = None,
                       firstName: Option[String] = None,
                       lastName: Option[String] = None,
                       cursor: Option[String] = None,
                       pageSize: Option[PageSizes] = None) extends ContactsCommand[PagingContactsResult] {

  override val method = HttpMethod.GET

  override val query = {
    super.mapValuesToStrings({
      import com.wix.hive.commands.contacts.GetContacts.QueryKeys
      Map(
        QueryKeys.labels -> labels,
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
    val labels = "labels"
    val email = "email"
    val phone = "phone"
    val firstName = "name.first"
    val lastName = "name.last"
    val cursor = "cursor"
    val pageSize = "pageSize"
  }

}
