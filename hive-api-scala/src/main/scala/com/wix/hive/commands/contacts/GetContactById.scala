package com.wix.hive.commands.contacts

import com.wix.hive.client.http.HttpMethod
import com.wix.hive.client.http.HttpMethod.HttpMethod
import com.wix.hive.commands.HiveBaseCommandResponse

case class GetContactById(id: String) extends ContactsCommand[GetContactByIdResponse] {
  override def method: HttpMethod = HttpMethod.GET

  override def urlParams = s"/${id}"

  //override def createHttpRequestData: HttpRequestData = HttpRequestData(method ,this.url + "/" + id)
}

case class GetContactByIdResponse(id: String) extends HiveBaseCommandResponse