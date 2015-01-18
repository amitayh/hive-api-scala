package com.wix.hive.drivers

import com.wix.hive.client.http.{HttpMethod, HttpRequestData}
import com.wix.hive.server.webhooks.{Provision, Webhook, WebhookParameters}
import org.joda.time.DateTime

/**
  * User: maximn
 * Date: 11/30/14
 */
trait SigningTestSupport {
  val key = "e5f5250a-dbd0-42a1-baf9-c61ea20c401b"
  val ts = "2014-10-08T10:20:51.036+03:00"
  private val app = "e37bd20e-a4a3-4cf8-b392-03269add8b69"
  private val instance = "d0f9764d-d255-490c-95da-d16e0efaf423"

  val headers = Map("X-Wix-Instance-Id" -> instance,
    "X-Wix-Application-Id" -> app,
    "X-Wix-Timestamp" -> ts)
  val query = Map("version" -> "1.0.0")


  val dataWithNoBody = HttpRequestData(HttpMethod.GET,
    "/v1/contacts/2c4436a4-13be-4581-99b2-69ed2781c7c9",
    queryString = query,
    headers = Map("X-Wix-Instance-Id" -> instance,
      "X-Wix-Application-Id" -> app,
      "X-Wix-Timestamp" -> "2014-10-08T10:20:52.320+03:00"))

  val dataWithNoBodySignature  = "l9exAT3viZ0HKGHREISVJl9OVGHKB85TENX9jewCHaA"

  val dataWithBody = HttpRequestData(HttpMethod.POST,
    "/v1/contacts",
    queryString = query,
    headers = headers,
    body = Some( """{"name":{"first":"Wix","last":"Cool"},"company":{},"emails":[{"email":"alext@wix.com","tag":"work"}],"phones":[{"phone":"123456789","tag":"work"}],"addresses":[],"urls":[],"dates":[],"notes":[],"custom":[]}"""))

  var dataWithBodySignature = "lZy2orr_9V05StuWgVfYxoMQfFtXV8iC02xFK8BoLhM"
  var dataWithBodyNoUrlSignature = "VkMKi7yuFifbO3wslpdiv68ZMQF-05NQGHaP8A7Xpi0"

  val provisioningData = Provision(instance, None)

  private val webhookParameters = WebhookParameters(app, new DateTime(ts))

  val provisioningWebhook = Webhook(instance, provisioningData, webhookParameters)

  val provisiningSignature = "54tx1k6LA-jMCSGN0kSde3nOa7oVupBzvSwn0YNCyRM"

  val provisioningWebhookRequest = HttpRequestData(HttpMethod.POST,
  "/callback-url",
  queryString = Map.empty,
  headers = headers.map { case(key,value) => key.toLowerCase -> value} + ("x-wix-signature" -> provisiningSignature) + ("x-wix-event-type" -> "/provision/provision"),
  body = Some(provisioningData))
}
