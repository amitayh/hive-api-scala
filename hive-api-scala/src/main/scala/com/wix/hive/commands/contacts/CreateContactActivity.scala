package com.wix.hive.commands.contacts

import com.wix.hive.client.http.HttpMethod
import com.wix.hive.client.http.HttpMethod.HttpMethod
import com.wix.hive.model.activities.{ActivityCreatedResult, ActivityDetails, ActivityInfo}
import org.joda.time.DateTime

case class CreateContactActivity(contactID: String,
                                 createdAt: DateTime,
                                 activityLocationUrl: Option[String],
                                 activityDetails: Option[ActivityDetails],
                                 activityInfo: ActivityInfo) extends ContactsCommand[ActivityCreatedResult] {
  override def method: HttpMethod = HttpMethod.POST


  override def urlParams: String = super.urlParams + s"/$contactID/activities"

  override def body: Option[AnyRef] = Some(ContactActivityDate(createdAt, activityLocationUrl, activityDetails, activityInfo))
}

case class ContactActivityDate(createdAt: DateTime,
                               activityLocationUrl: Option[String],
                               activityDetails: Option[ActivityDetails],
                               activityInfo: ActivityInfo) {
  val activityType = activityInfo.activityType.toString
}