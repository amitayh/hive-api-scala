package com.wix.hive.commands.contacts

import com.wix.hive.client.http.{HttpRequestData, HttpMethod}
import com.wix.hive.commands.{HiveBaseCommandResponse, HiveBaseCommand}

trait ContactsCommand[TResponse <: HiveBaseCommandResponse] extends HiveBaseCommand[TResponse]{
  override val url: String = "/contacts"
}

case class UpsertContact(phone: Option[String], email: Option[String]) extends ContactsCommand[UpsertContactResponse] {
  override val method = HttpMethod.PUT

  override def createHttpRequestData: HttpRequestData = ???
}

case class UpsertContactResponse(id: String) extends HiveBaseCommandResponse
