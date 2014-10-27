package com.wix.hive.model

import com.fasterxml.jackson.annotation.{JsonIgnore, JsonCreator}
import com.wix.hive.model.ActivityType.ActivityType
import org.joda.time.DateTime

import scala.annotation.switch
import scala.util.Try


case class Activity(id: String, createdAt: DateTime, activityLocationUrl: Option[String] = None,
                    activityDetails: Option[ActivityDetails] = None, activityInfo: ActivityInfo)

case class CreateActivity(createdAt: DateTime, activityLocationUrl: Option[String] = None, activityDetails: Option[ActivityDetails] = None,
                          activityInfo: ActivityInfo, contactUpdate: Option[Contact] = None) {
  val activityType = activityInfo.activityType.toString
}

object Activity {

  import ActivityType._

  val activityTypeToClass = Map(
    `auth/login` -> classOf[AuthLogin],
    `auth/register` -> classOf[AuthRegister],
    `auth/status-change` -> classOf[AuthStatusChange],
    `contact/contact-form` -> classOf[ContactContactForm],
    `e_commerce/purchase` -> classOf[ECommercePurchase],
    `music/album-fan` -> classOf[MusicAlbumFan],
    `music/album-share` -> classOf[MusicAlbumShare],
    `music/track-lyrics` -> classOf[MusicTrackLyrics],
    `music/track-play` -> classOf[MusicTrackPlay],
    `music/track-played` -> classOf[MusicTrackPlayed],
    `music/track-share` -> classOf[MusicTrackShare],
    `music/track-skip` -> classOf[MusicTrackSkip])


  @JsonCreator
  def factory(props: Map[String, Object]): Activity = {
    val id = props("id").asInstanceOf[String]

    val createdAt = mapper.convertValue(props("createdAt"), classOf[DateTime]) //new DateTime(props("createdAt").asInstanceOf[String])

    val activityLocationUrl = Try(mapper.convertValue(props("activityLocationUrl"), classOf[String])).toOption // Some(props.getOrElse("activityLocationUrl", "").asInstanceOf[String]).filter(_.nonEmpty)

    val activityDetails = Try(mapper.convertValue(props("activityDetails"), classOf[ActivityDetails])).toOption

    val typ = ActivityType.withName(props("activityType").asInstanceOf[String])
    val activityInfoType = activityTypeToClass(typ)
    val activityInfo = mapper.convertValue(props("activityInfo"), activityInfoType)


    com.wix.hive.model.Activity(id,
      createdAt,
      activityLocationUrl,
      activityDetails,
      activityInfo)
  }
}

object ActivityType extends Enumeration {
  type ActivityType = Value
  val `auth/login` = Value("auth/login")
  val `auth/register` = Value("auth/register")
  val `auth/status-change` = Value("auth/status-change")
  val `contact/contact-form` = Value("contact/contact-form")
  val `e_commerce/purchase` = Value("e_commerce/purchase")
  val `music/album-fan` = Value("music/album-fan")
  val `music/album-share` = Value("music/album-share")
  val `music/track-lyrics` = Value("music/track-lyrics")
  val `music/track-play` = Value("music/track-play")
  val `music/track-played` = Value("music/track-played")
  val `music/track-share` = Value("music/track-share")
  val `music/track-skip` = Value("music/track-skip")
}


case class ActivityDetails(additionalInfoUrl: String, summary: String)

import ActivityType._

abstract class ActivityInfo {
  @JsonIgnore
  def activityType: ActivityType
}

case class AuthLogin() extends ActivityInfo {
  override val activityType = `auth/login`
}

case class AuthRegister(initiator: String, previousActivityStreamId: String, status: String) extends ActivityInfo {
  override val activityType = `auth/register`
}

case class AuthStatusChange() extends ActivityInfo {
  override val activityType = `auth/status-change`
}

case class ContactContactForm() extends ActivityInfo {
  override val activityType = `contact/contact-form`
}

case class ECommercePurchase() extends ActivityInfo {
  override val activityType = `e_commerce/purchase`
}

case class MusicAlbumFan() extends ActivityInfo {
  override val activityType = `music/album-fan`
}

case class MusicAlbumShare() extends ActivityInfo {
  override val activityType = `music/album-share`
}

case class MusicTrackLyrics() extends ActivityInfo {
  override val activityType = `music/track-lyrics`
}

case class MusicTrackPlay() extends ActivityInfo {
  override val activityType = `music/track-play`
}

case class MusicTrackPlayed() extends ActivityInfo {
  override val activityType = `music/track-played`

}

case class MusicTrackShare() extends ActivityInfo {
  override val activityType = `music/track-share`
}

case class MusicTrackSkip() extends ActivityInfo {
  override val activityType = `music/track-skip`
}

case class ActivityTypes(types:Seq[String])

case class ActivityCreatedResult(activityId: String, contactId: String)