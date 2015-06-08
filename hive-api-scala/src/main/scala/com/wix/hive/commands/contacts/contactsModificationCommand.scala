package com.wix.hive.commands.contacts

import com.wix.hive.client.http.HttpMethod.{HttpMethod, _}
import com.wix.hive.client.http._
import org.joda.time.DateTime

trait ContactsModificationCommand[TResult] extends ContactsCommand[TResult] {
  private val modifiedAtKey = "modifiedAt"
  val contactId: String
  val modifiedAt: DateTime

  override def query: NamedParameters = super.query + (modifiedAtKey -> modifiedAt.toString)

  override def urlParams: String = s"/$contactId"
}

trait AddToContactCommand[TResult] extends ContactsModificationCommand[TResult] {
  override def method: HttpMethod = POST
}

trait UpdateContactCommand[TResult] extends ContactsModificationCommand[TResult] {
  override def method: HttpMethod = PUT
}