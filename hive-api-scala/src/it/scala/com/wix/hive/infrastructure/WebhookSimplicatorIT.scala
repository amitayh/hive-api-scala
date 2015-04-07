package com.wix.hive.infrastructure

import java.util.UUID

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.http.{Request, Response}
import com.wix.hive.infrastructure.WiremockRequestConverter.RequestConverterFromWiremock
import com.wix.hive.server.webhooks._
import org.specs2.matcher.Matchers
import org.specs2.mock.Mockito

import scala.util.Try

/**
 * User: maximn
 * Date: 1/28/15
 */
trait WebhookSimplicatorIT extends Mockito with Matchers {
  WiremockEnvironment.start()
  WiremockEnvironment.resetMocks()

  val webhookSecret = UUID.randomUUID().toString
  val webhookPath: String = "/localhost/webhook-url"
  val webhookPort = WiremockEnvironment.serverPort

  protected val converter: GenericWebhooksConverter[WebhookBase[WebhookData]] = new WebhooksConverter(webhookSecret)

  givenThat(WireMock.post(urlMatching(webhookPath)).willReturn(aResponse().withStatus(200)))

  def subscribeFunc(f: (Try[WebhookBase[_]]) => Unit) = {
    WiremockEnvironment.setListener((request: Request, response: Response) => {
      val webhook = converter.convert(request)
      f(webhook)
    })
  }

  def clearListener(): Unit = WiremockEnvironment.removeListener()
}