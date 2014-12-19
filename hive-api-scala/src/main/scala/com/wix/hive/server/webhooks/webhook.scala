package com.wix.hive.server.webhooks

import com.fasterxml.jackson.annotation.{JsonCreator, JsonProperty}
import org.joda.time.DateTime

/**
 * User: maximn
 * Date: 12/1/14
 */
case class Webhook(instanceId: String, data: WebhookData, parameters: WebhookParameters)

object Webhook {
  def resolveType(wh: Webhook) = {
    wh.data match {
      case t: Provision => "/provision/provision"
      case t: BillingUpgrade => "/billing/upgrade"
      case t: BillingCancel => "/billing/cancel"
      case t: ContactsCreated => "/contacnts/created"
      case t: ContactsUpdated => "/contacts/updated"
      case t: ActivitiesPosted => "/activities/posted"
      case _ => throw new RuntimeException("Unsupported webhook type")
    }
  }
}

case class WebhookParameters(appId: String, timestamp: DateTime)

sealed trait WebhookData

case class Provision (@JsonProperty("instance-id")instanceId: String, @JsonProperty("origin-instance-id")originInstanceId: Option[String]) extends WebhookData

case class BillingUpgrade(vendorProductId: String) extends WebhookData

case class BillingCancel() extends WebhookData

case class ContactsCreated(contactId: String) extends WebhookData

case class ContactsUpdated(contactId: String) extends WebhookData

case class ActivitiesPosted(activityId: String, activityType: String, contactId: Option[String] = None) extends WebhookData