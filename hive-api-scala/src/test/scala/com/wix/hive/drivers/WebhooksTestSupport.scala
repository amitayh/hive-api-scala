package com.wix.hive.drivers

import java.util.UUID

import com.wix.hive.commands.services.{EmailContactMethod, EmailContacts}
import com.wix.hive.matchers.HiveMatchers
import com.wix.hive.server.webhooks._
import org.joda.time.DateTime
import org.specs2.matcher.{Matcher, Matchers}

/**
 * User: maximn
 * Date: 1/13/15
 */
trait WebhooksTestSupport extends Matchers
with HiveMatchers {
  def aWebhookParams(appId: String = appId, timestamp: DateTime = timestamp) = WebhookParameters(appId, timestamp)


  def randomId: String = {
    UUID.randomUUID().toString
  }

  def anEmailSendWebhookData = EmailSend(randomId, randomId, randomId, EmailContacts(EmailContactMethod.Id, Seq(randomId)))


  def anEmailSendWebhook = new Webhook(instanceId, anEmailSendWebhookData, aWebhookParams())

  def beWebhook[T <: WebhookData](instanceId: Matcher[String], appId: Matcher[String], dataMatcher: Matcher[T]): Matcher[Webhook[T]] = {
    instanceId ^^ {(_: Webhook[T]).instanceId aka "instanceId"} and
      appId ^^ {(_: Webhook[T]).parameters.appId aka "parameters.appId"} and
      dataMatcher ^^ {(_: Webhook[T]).data aka "parameters.data"}
  }

  def beProvision(instanceId: Matcher[String], originInstanceId: Matcher[Option[String]] = beNone): Matcher[Provision] = {
    instanceId ^^ {(_: Provision).instanceId aka "instanceId"} and
      originInstanceId ^^ {(_: Provision).originInstanceId aka "originInstanceId"}
  }


  def beEmailSend(originId: Matcher[String],
                  correlationId: Matcher[String],
                  redemptionToken: Matcher[String],
                  contacts: Matcher[EmailContacts]): Matcher[EmailSend] = {
    originId ^^ {(_: EmailSend).originId aka "originId"} and
      correlationId ^^ {(_: EmailSend).correlationId aka "correlationId"} and
      redemptionToken ^^ {(_: EmailSend).redemptionToken aka "redemptionToken"} and
      contacts ^^ {(_: EmailSend).contacts aka "contacts"}
  }

  def beServiceRunResult(providerId: Matcher[String] = not(be(empty)),
                         correlationId: Matcher[String] = not(be(empty)),
                         data: Matcher[ServiceRunData] = anything): Matcher[ServiceResult] = {
    providerId ^^ {(_: ServiceResult).providerId aka "providerId"} and
    correlationId ^^ { (_: ServiceResult).correlationId aka "correlationId" } and
    data ^^ { (_: ServiceResult).data aka "data" }
  }

  def aProvisionWebhook(instanceId: String = instanceId) = Webhook(instanceId, Provision(instanceId, None), aWebhookParams())

  def aProvisionDisabledWebhook(instanceId: String = instanceId) = Webhook(instanceId, ProvisionDisabled(instanceId, None), aWebhookParams())

  val activityId = "someActivityId"
  val activityType = "auth/register"

  def anActivityPostedWebhook(instanceId: String = instanceId) = Webhook(instanceId, ActivitiesPosted(activityId, activityType, None), aWebhookParams())
  def aServicesDoneWebhook(providerAppId: String = UUID.randomUUID().toString, instanceId: String = instanceId) = Webhook(instanceId, ServiceResult(providerAppId, "af142114-f616-4594-9fb8-1253d317541e", ServiceRunData("SUCCESS", None, None)), aWebhookParams(appId))

  def beActivity(activityId: Matcher[String], activityType: Matcher[String], contactId: Matcher[Option[String]] = beNone): Matcher[ActivitiesPosted] = {
    activityId ^^ {(_: ActivitiesPosted).activityId aka "activityId"} and
      activityType ^^ {(_: ActivitiesPosted).activityType aka "activityType"} and
      contactId ^^ {(_: ActivitiesPosted).contactId aka "contactId"}
  }


  def beProvisionDisabled(instanceId: Matcher[String], originInstanceId: Matcher[Option[String]] = beNone): Matcher[ProvisionDisabled] = {
    instanceId ^^ {(_: ProvisionDisabled).instanceId aka "instanceId"} and
      originInstanceId ^^ {(_: ProvisionDisabled).originInstanceId aka "originInstanceId"}
  }

  def aSiteSettingsChangedWebhook = Webhook(
    instanceId,
    SiteSettingsChanged(SiteSettingChange.UPDATED, "DateFormat"),
    aWebhookParams()
  )

}

object WebhooksTestSupport extends WebhooksTestSupport