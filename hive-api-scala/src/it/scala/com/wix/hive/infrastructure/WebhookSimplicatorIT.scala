package com.wix.hive.infrastructure

import java.util.UUID

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.http.{Request, Response}
import com.wix.hive.server.webhooks.{Webhook, WebhooksConverter}
import com.wix.hive.webhooks.WiremockRequestConverter
import org.specs2.matcher.Matchers
import org.specs2.mock.Mockito

import scala.util.Try

/**
 * User: maximn
 * Date: 1/28/15
 */
trait WebhookSimplicatorIT extends Mockito with Matchers {
  WiremockEnvironment.start()

  val webhookSecret = UUID.randomUUID().toString
  val webhookPath: String = "/localhost/webhook-url"
  val webhookPort = WiremockEnvironment.serverPort

  private val timeout = new org.specs2.time.Duration(3000)


  val converter = new WebhooksConverter {
    override def secret: String = webhookSecret
  }

  givenThat(WireMock.post(urlMatching(webhookPath)).willReturn(aResponse().withStatus(200)))

  def subscribeFunc(f: (Try[Webhook[_]]) => Unit) = {
    WiremockEnvironment.setListener((request: Request, response: Response) => {
      val webhook = converter.convert(request)(WiremockRequestConverter.RequestConverterFromWiremock)
      f(webhook)
    })
  }

  def clearListener(): Unit = WiremockEnvironment.removeListener()
}