package com.wix.hive.commands.contacts

import com.wix.hive.client.http.HttpMethod.{HttpMethod, _}
import com.wix.hive.client.http._
import org.joda.time.DateTime

trait ContactsModificationCommand[TResult] extends ContactsCommand[TResult] {
  val modifiedAtKey = "modifiedAt"

  val contactId: String

  protected val modifiedAtOption: Option[DateTime]

  override def query: NamedParameters = modifiedAtOption map { mAt => Map(modifiedAtKey -> mAt.toString) } getOrElse Map.empty

  override def urlParams: String = s"/$contactId"
}

trait AddToContactCommand[TResult] extends ContactsModificationCommand[TResult] {
  override def method: HttpMethod = POST

}

trait UpdateContactCommand[TResult] extends ContactsModificationCommand[TResult] {
  override def method: HttpMethod = PUT
}