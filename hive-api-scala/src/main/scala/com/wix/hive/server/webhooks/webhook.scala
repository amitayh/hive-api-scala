package com.wix.hive.server.webhooks

import com.fasterxml.jackson.annotation.JsonProperty
import com.wix.hive.commands.services.EmailContacts
import org.joda.time.DateTime

/**
 * User: maximn
 * Date: 12/1/14
 */
case class Webhook[T <: WebhookData](instanceId: String, data: T, parameters: WebhookParameters)


case class WebhookParameters(appId: String, timestamp: DateTime)

sealed trait WebhookData

case class Provision (@JsonProperty("instance-id")instanceId: String, @JsonProperty("origin-instance-id")originInstanceId: Option[String]) extends WebhookData
case class ProvisionDisabled (@JsonProperty("instance-id")instanceId: String, @JsonProperty("origin-instance-id")originInstanceId: Option[String]) extends WebhookData

//case class BillingUpgrade(vendorProductId: String) extends WebhookData
//
//case class BillingCancel() extends WebhookData
//
//case class ContactsCreated(contactId: String) extends WebhookData
//
//case class ContactsUpdated(contactId: String) extends WebhookData

case class ActivitiesPosted(activityId: String, activityType: String, contactId: Option[String] = None) extends WebhookData

case class ServiceResult(providerId: String, correlationId: String, data: ServiceRunData) extends WebhookData
//TODO: status -> enum
//TODO: errorType -> enum [UNKNOWN_TOKEN' or 'MISSING_PARAMETERS' or 'INTERNAL_ERROR' or 'LIMIT_REACHED' or 'MISSING_PREMIUM']]
case class ServiceRunData(status: String, errorType: Option[String], errorMessage: Option[String])

case class EmailSend(originId: String, correlationId: String, redemptionToken: String, contacts: EmailContacts) extends WebhookData