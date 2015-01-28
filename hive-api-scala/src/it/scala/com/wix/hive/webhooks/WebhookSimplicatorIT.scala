package com.wix.hive.webhooks

import java.util.UUID

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.http.{Response, Request, RequestListener}
import com.wix.hive.infrastructure.WiremockSimplicator
import com.wix.hive.server.webhooks.{WebhookData, WebhooksConverter, Webhook}
import org.specs2.matcher.{Matchers, Matcher}
import org.specs2.mock.Mockito

import scala.util.Try

/**
 * User: maximn
 * Date: 1/28/15
 */
trait WebhookSimplicatorIT extends Mockito with Matchers {
  WiremockSimplicator.start()

  val webhookSecret = UUID.randomUUID().toString
  val webhookPath: String = "/localhost/webhook-url"
  val webhookPort = WiremockSimplicator.serverPort
  private val timeout = new org.specs2.time.Duration(3000)


  val converter = new WebhooksConverter {
    override def secret: String = webhookSecret
  }

  givenThat(WireMock.post(urlMatching(webhookPath)).willReturn(aResponse().withStatus(200)))

  def subscribeFunc(f: (Try[Webhook[_]]) => Unit) = {
    WiremockSimplicator.server.addMockServiceRequestListener(new RequestListener {
      override def requestReceived(request: Request, response: Response): Unit = {
        val webhook = converter.convert(request)(WiremockRequestConverter.RequestConverterFromWiremock)
        f(webhook)
      }
    })
  }


}