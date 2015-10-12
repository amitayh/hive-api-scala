package com.wix.hive.model.activities

import com.fasterxml.jackson.annotation.JsonCreator
import com.wix.hive.model.JacksonObjectMapper
import org.joda.time.DateTime

import scala.util.control.Exception._

case class Activity(id: String,
                    createdAt: DateTime,
                    activityInfo: ActivityInfo,
                    activityLocationUrl: Option[String] = None,
                    activityDetails: Option[ActivityDetails] = None)


object Activity {

  import com.wix.hive.model.activities.ActivityType._


  val activityTypeToClass = Map(
    `auth/login` -> classOf[AuthLogin],
    `auth/register` -> classOf[AuthRegister],
    `auth/status-change` -> classOf[AuthStatusChange],
    `contact/contact-form` -> classOf[ContactContactForm],
    `contact/subscription-form` -> classOf[ContactSubscriptionForm],
    `e_commerce/purchase` -> classOf[ECommercePurchase],
    `music/album-fan` -> classOf[MusicAlbumFan],
    `music/album-share` -> classOf[MusicAlbumShare],
    `music/track-lyrics` -> classOf[MusicTrackLyrics],
    `music/track-play` -> classOf[MusicTrackPlay],
    `music/track-played` -> classOf[MusicTrackPlayed],
    `music/track-share` -> classOf[MusicTrackShare],
    `music/track-skip` -> classOf[MusicTrackSkip],
    `scheduler/appointment` -> classOf[SchedulerAppointment],
    `scheduler/cancel` -> classOf[SchedulerCancel],
    `scheduler/confirmation` -> classOf[SchedulerConfirmation]
  )

  @JsonCreator
  def factory(props: Map[String, Object]): Activity = {

    def opt[T] = allCatch[T].opt _

    val id = props("id").asInstanceOf[String]

    val createdAt = JacksonObjectMapper.mapper.convertValue(props("createdAt"), classOf[DateTime])

    val activityLocationUrl =  opt(JacksonObjectMapper.mapper.convertValue(props("activityLocationUrl"), classOf[String]))

    val activityDetails = opt(JacksonObjectMapper.mapper.convertValue(props("activityDetails"), classOf[ActivityDetails]))

    val typ = ActivityType.withName(props("activityType").asInstanceOf[String])
    val activityInfoType = activityTypeToClass(typ)
    val activityInfo = JacksonObjectMapper.mapper.convertValue(props("activityInfo"), activityInfoType)

    Activity(
      id,
      createdAt,
      activityInfo,
      activityLocationUrl,
      activityDetails)
  }

  //HACK: I use the `JacksonObjectMapper.mapper` as a hack here until find better solution.
  // The correct solution will be to use `JsonTypeInfo.As.EXTERNAL_PROPERTY` -
  //  case class Activity(id: String,
  //                      createdAt: DateTime,
  //                      @JsonSubTypes(Array(
  //                        new Type(value = classOf[AuthLogin], name = "auth/login"),
  //                        new Type(value = classOf[AuthRegister], name = "auth/register")
  //                      ))
  //                      @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "activityType")
  //                      activityInfo: ActivityInfo,
  //                      activityLocationUrl: Option[String] = None,
  //                      activityDetails: Option[ActivityDetails] = None)
  // But seems that jackson doesn't allow to use this mehtod with case classes
}


