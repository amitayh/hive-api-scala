package com.wix.hive.commands.contacts

import com.wix.hive.client.http.HttpMethod
import com.wix.hive.client.http.HttpMethod.HttpMethod
import com.wix.hive.model.Contact


case class GetContactById(id: String) extends ContactsCommand[Contact] {
  override def method: HttpMethod = HttpMethod.GET

  override def urlParams = s"/$id"
}

case class GetContactByIdResponse()