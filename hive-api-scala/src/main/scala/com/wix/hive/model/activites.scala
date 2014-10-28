package com.wix.hive.model

import com.fasterxml.jackson.annotation.{JsonCreator, JsonIgnore}
import com.fasterxml.jackson.module.scala.JsonScalaEnumeration
import com.wix.hive.model.ActivityType.ActivityType
import org.joda.time.DateTime

import scala.util.Try

case class Activity(id: String, createdAt: DateTime, activityLocationUrl: Option[String] = None,
                    activityDetails: Option[ActivityDetails] = None, activityInfo: ActivityInfo)

object Activity {

  import com.wix.hive.model.ActivityType._

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


  //TODO : see if possible to extract it out
  @JsonCreator
  def factory(props: Map[String, Object]): Activity = {
    val id = props("id").asInstanceOf[String]

    val createdAt = mapper.convertValue(props("createdAt"), classOf[DateTime])

    val activityLocationUrl = Try(mapper.convertValue(props("activityLocationUrl"), classOf[String])).toOption

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

case class CreateActivity(createdAt: DateTime, activityLocationUrl: Option[String] = None, activityDetails: Option[ActivityDetails] = None,
                          activityInfo: ActivityInfo, contactUpdate: Option[ContactActivity] = None) {
  val activityType = activityInfo.activityType.toString
}

case class ContactActivity(name: Option[ContactName] = None, picture: Option[String] = None, company: Option[Company] = None,
                           emails: Seq[ContactEmail] = Nil, phones: Seq[ContactPhone] = Nil,
                           addresses: Seq[Address] = Nil, dates: Seq[ImportantDate] = Nil, urls: Seq[ContactUrl] = Nil)


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

import com.wix.hive.model.ActivityType._

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

case class NameValuePair(name: String, value: String)

case class ContactContactForm(items: Seq[NameValuePair]) extends ActivityInfo {
  override val activityType = `contact/contact-form`
}

case class Media(thumbnail: String)

case class Variant(title: String, value: Option[String])

case class Coupon(total: BigDecimal, formattedTotal: Option[String], title: String)

case class Tax(total: BigDecimal, formattedTotal: Option[String])

case class Shipping(total: BigDecimal, formattedTotal: Option[String])

case class Payment(total: BigDecimal, subtotal: BigDecimal, formattedTotal: Option[String], formattedSubtotal: Option[String],
                   currency: String, coupon: Option[Coupon], tax: Option[Tax], shipping: Shipping)

case class CartAddress(firstName: String, lastName: String, email: String, phone: String, country: String, countryCode: String,
                       region: String, regionCode: String, city: String, address1: String, address2: String, zip: String, company: String)

case class CartItem(id: String, sku: Option[String], title: String, quantity: Int, price: BigDecimal, formattedPrice: Option[String],
                    currency: String, productLink: Option[String], weight: BigDecimal, formattedWeight: Option[String],
                    media: Media, variants: Seq[Variant], payment: Payment, shippingAddress: Option[CartAddress],
                    billingAddress: Option[CartAddress], paymentGateway: Option[String], note: Option[String],
                    buyerAcceptsMarketing: Option[Boolean])

case class ECommercePurchase(cartId: String, storeId: String, orderId: Option[String], items: Seq[CartItem]) extends ActivityInfo {
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

case class ActivityTypes(types: Seq[String])

case class ActivityCreatedResult(activityId: String, contactId: String)