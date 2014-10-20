package com.wix.hive.commands.contacts

import com.wix.hive.client.http.{HttpRequestData, HttpMethod}
import com.wix.hive.commands.{HiveBaseCommandResponse}
import com.wix.hive.model._

case class CreateContact(name: Option[ContactName] = None, picture: Option[String] = None, company: Option[Company] = None,
                         tags: Seq[String] = Nil, emails: Seq[ContactEmail] = Nil, phone: Seq[ContactPhone] = Nil,
                         addresses: Seq[Address] = Nil, urls: Seq[ContactUrl] = Nil, dates: Seq[ImportantDate] = Nil,
                         notes: Seq[Note] = Nil, custom: Seq[CustomField] = Nil) extends ContactsCommand[CreateContactResponse] {
  override val method = HttpMethod.POST

  override def createHttpRequestData: HttpRequestData = ???
}

case class CreateContactResponse(id: String) extends HiveBaseCommandResponse
