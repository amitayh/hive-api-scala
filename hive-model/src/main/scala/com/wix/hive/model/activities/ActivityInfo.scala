package com.wix.hive.model.activities

import com.fasterxml.jackson.annotation.JsonIgnore
import com.wix.hive.model.activities.ActivityType._
import com.wix.hive.model.contacts.ContactName
import org.joda.time.DateTime

// HACK: This is a temporary base class to hive-client activity info & activities-domain activity info.
// it's temp until we move all ActivityInfos to inherit com.wix.hive.model.activities.ActivityInfo
trait ActivityInfoBase

trait ActivityInfo extends ActivityInfoBase {
  @JsonIgnore
  def activityType: ActivityType
}


case class AuthRegister(initiator: String, previousActivityStreamId: String, status: String) extends ActivityInfo {
  override val activityType = `auth/register`
}

case class AuthStatusChange() extends ActivityInfo {
  override val activityType = `auth/status-change`
}

case class NameValuePair(name: String, value: String)

case class ContactContactForm(fields: Seq[NameValuePair]) extends ActivityInfo {
  override val activityType = `contact/contact-form`
}

case class ContactSubscriptionForm(email: String, name: Option[ContactName], phone: Option[String], fields: Seq[NameValuePair]) extends ActivityInfo {
  override val activityType = `contact/subscription-form`
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
                    media: Media, variants: Seq[Variant], categories: Seq[String] = Nil, metadata: Seq[ItemMetadata] = Nil)

case class ItemMetadata(name: String, value: String)

case class ECommercePurchase(
                              cartId: String,
                              storeId: String,
                              orderId: Option[String],
                              items: Seq[CartItem],
                              payment: Payment,
                              shippingAddress: Option[CartAddress],
                              billingAddress: Option[CartAddress],
                              paymentGateway: Option[String],
                              note: Option[String],
                              buyerAcceptsMarketing: Option[Boolean]
                              ) extends ActivityInfo {
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

case class SchedulerAppointment(
                                 appointmentId: Option[String],
                                 source: Source,
                                 title: String,
                                 description: String,
                                 infoLink: Option[String],
                                 price: Option[Price],
                                 location: Option[Location],
                                 time: Option[Time],
                                 attendees: Seq[Attendee]
                                 ) extends ActivityInfo {
  override val activityType = `scheduler/appointment`
}

case class SchedulerConfirmation(
                                  appointmentId: Option[String],
                                  source: Source,
                                  title: String,
                                  description: String,
                                  infoLink: Option[String],
                                  price: Option[Price],
                                  location: Option[Location],
                                  time: Option[Time],
                                  attendees: Seq[Attendee]
                                  ) extends ActivityInfo {
  override val activityType = `scheduler/confirmation`
}

case class SchedulerCancel(
                            appointmentId: Option[String],
                            source: Source,
                            cancelDate: DateTime,
                            refund: Option[Refund],
                            title: String,
                            description: String,
                            infoLink: Option[String],
                            price: Option[Price],
                            location: Option[Location],
                            time: Option[Time],
                            attendees: Seq[Attendee]
                            ) extends ActivityInfo {
  override val activityType = `scheduler/cancel`
}

case class Price(
                  price: java.math.BigDecimal,
                  currency: String,
                  formattedPrice: Option[String]
                  )

case class Location(
                     address: Option[String],
                     city: Option[String],
                     region: Option[String],
                     postalCode: Option[String],
                     country: Option[String],
                     url: Option[String]
                     )

case class Time(
                 start: DateTime,
                 end: DateTime,
                 timezone: String
                 )

case class Refund(
                   kind: RefundKind,
                   total: java.math.BigDecimal,
                   formattedTotal: Option[String],
                   currency: String,
                   notes: Option[String]
                   )

case class Attendee(
                     contactId: Option[String],
                     name: Option[Name],
                     phone: Option[String],
                     email: Option[String],
                     notes: Option[String],
                     self: Option[Boolean]
                     )

case class Name(
                 prefix: Option[String],
                 first: Option[String],
                 middle: Option[String],
                 last: Option[String],
                 suffix: Option[String]
                 )