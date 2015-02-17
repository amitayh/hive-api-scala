package com.wix.hive.model.activities

import com.fasterxml.jackson.annotation.JsonIgnore
import com.wix.hive.model.activities.ActivityType._
import com.wix.hive.model.contacts.ContactName

sealed trait ActivityInfo {
  @JsonIgnore
  def activityType: ActivityType
}


case class AuthLogin() extends ActivityInfo {
  override val activityType: ActivityType = `auth/login`

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
                    media: Media, variants: Seq[Variant])

case class ECommercePurchase(cartId: String,
                             storeId: String,
                             orderId: Option[String],
                             items: Seq[CartItem],
                             payment: Payment,
                             shippingAddress: Option[CartAddress],
                             billingAddress: Option[CartAddress],
                             paymentGateway: Option[String],
                             note: Option[String],
                             buyerAcceptsMarketing: Option[Boolean]) extends ActivityInfo {
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