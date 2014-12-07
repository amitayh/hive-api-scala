package com.wix.hive.server.webhooks.exceptions

import com.wix.hive.client.http.HttpRequestData

import scala.util.control.NoStackTrace

/**
 * User: maximn
 * Date: 12/1/14
 */
class WebhookParsingException(val msg: String, cause: Throwable) extends RuntimeException(msg, cause) with NoStackTrace {
  def this(msg: String) = this(msg, null)
}

class InvalidSignatureException(val req: HttpRequestData, val invalidSignature: String, val calculatedSignature: String)
  extends WebhookParsingException(s"Signature validation failed. Given: $invalidSignature, calculated: $calculatedSignature. For request: $req")

class MissingHeaderException(val name: String)
  extends WebhookParsingException(s"Missing header with name: $name")

class UnkownWebhookTypeException(val `type`: String)
  extends WebhookParsingException(s"Unknown webhook type. Given: ${`type`}")

class BadFormattedWebhookException(val json: String, val cause: Throwable)
  extends WebhookParsingException(s"Bad formatted webhook body: $json. Cause: $cause", cause)
