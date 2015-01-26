package com.wix.hive.server.webhooks

import com.wix.hive.client.http.HttpRequestData
import com.wix.hive.server.webhooks.WebhooksMarshaller._
import com.wix.hive.server.webhooks.exceptions.UnkownWebhookTypeException

import scala.util.{Failure, Success, Try}

/**
 * User: maximn
 * Date: 12/1/14
 */
object WebhooksMarshaller {
  val eventTypeKey = "x-wix-event-type"
}

class WebhooksMarshaller extends HttpRequestHelpers {
  private val eventType2Class = Map(
    "/provision/provision" -> classOf[Provision],
    "/provision/disabled" -> classOf[ProvisionDisabled],
    "/activities/posted" -> classOf[ActivitiesPosted],
    "/services/actions/done" -> classOf[ServiceResult],
    "/services/actions/email/send" -> classOf[EmailSend]
    //    "/billing/upgrade" -> classOf[BillingUpgrade],
    //    "/billing/cancel" -> classOf[BillingCancel],
    //    "/contacnts/created" -> classOf[ContactsCreated],
    //    "/contacts/updated" -> classOf[ContactsUpdated],
  )

  def unmarshal(req: HttpRequestData): Try[WebhookData] = {
    for {
      typ <- tryHeader(req, eventTypeKey)
      clas <- eventType2Class.get(typ).fold[Try[Class[_]]](Failure(new UnkownWebhookTypeException(typ)))(Success.apply)
      webhook <- tryUnmarshal(req, clas)
    } yield webhook
  }
}