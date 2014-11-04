package com.wix.hive.commands.contacts

import com.wix.hive.client.http.HttpMethod.{HttpMethod, _}
import com.wix.hive.client.http._
import org.joda.time.DateTime

trait AddToContactCommand[TResult] extends ContactsCommand[TResult] {
  val modifiedAtKey = "modifiedAt"

  val contactId: String
  val modifiedAt: DateTime

  override def method: HttpMethod = POST

  override def query: NamedParameters = Map(
    modifiedAtKey -> modifiedAt.toString
  )

  override def urlParams: String = s"/$contactId"
}
