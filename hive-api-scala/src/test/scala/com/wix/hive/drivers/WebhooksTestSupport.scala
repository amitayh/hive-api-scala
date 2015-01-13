package com.wix.hive.drivers

import java.util.UUID

import com.wix.hive.commands.services.EmailContacts
import com.wix.hive.matchers.HiveMatchers
import com.wix.hive.server.webhooks._
import org.joda.time.DateTime
import org.specs2.matcher.{AlwaysMatcher, Matchers, Matcher}

/**
 * User: maximn
 * Date: 1/13/15
 */
trait WebhooksTestSupport extends Matchers
with HiveMatchers {
  val appId = UUID.randomUUID().toString
  val instanceId = UUID.randomUUID().toString
  val timestamp = new DateTime(2014, 2, 11, 1, 2)

  def aWebhookParams(appId: String = appId, timestamp: DateTime = timestamp) = WebhookParameters(appId, timestamp)


  def randomId: String = {
    UUID.randomUUID().toString
  }

  def anEmailSendWebhookData = EmailSend(randomId, randomId, randomId, EmailContacts("id", Seq(randomId)))


  def anEmailSendWebhook = new Webhook(instanceId, anEmailSendWebhookData, aWebhookParams())

  def beWebhook[T <: WebhookData](instanceId: Matcher[String], appId: Matcher[String], dataMatcher: Matcher[T]): Matcher[Webhook[T]] = {
    instanceId ^^ {(_: Webhook[T]).instanceId aka "instanceId"} and
      appId ^^ {(_: Webhook[T]).parameters.appId aka "parameters.appId"} and
      dataMatcher ^^ {(_: Webhook[T]).data aka "parameters.data"}
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


}
