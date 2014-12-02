package com.wix.hive.server.webhooks

import org.joda.time.DateTime

/**
 * User: maximn
 * Date: 12/1/14
 */
case class Webhook(instanceId: String, data: WebhookData, parameters: WebhookParameters)
case class WebhookParameters(appId: String, timestamp: DateTime)


sealed trait WebhookData

case class Provision(instanceId: String, originInstanceId: Option[String]) extends WebhookData

case class BillingUpgrade(vendorProductId: String) extends WebhookData

case class BillingCancel() extends WebhookData

case class ContactsCreated(contactId: String) extends WebhookData

case class ContactsUpdated(contactId: String) extends WebhookData

case class ActivitiesPosted(activityId: String, activityType: String) extends WebhookData