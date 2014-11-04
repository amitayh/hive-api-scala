package com.wix.hive.commands.contacts

import com.wix.hive.client.http.HttpMethod
import com.wix.hive.client.http.HttpMethod._
import com.wix.hive.model.contacts.Contact

case class GetContactById(id: String) extends ContactsCommand[Contact] {
  override val method: HttpMethod = HttpMethod.GET

  override val urlParams = s"/$id"
}
