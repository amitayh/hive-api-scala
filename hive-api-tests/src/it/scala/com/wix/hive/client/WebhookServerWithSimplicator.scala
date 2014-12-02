package com.wix.hive.client

import com.twitter.finagle.{Http, Service}
import com.twitter.util.Duration
import com.wix.hive.client.http.HttpRequestData
import com.wix.hive.client.infrastructure.{SimplicatorWebhooksDriver, AppDef, WebhooksDriver}
import com.wix.hive.server.FinagleWebServer
import com.wix.hive.server.webhooks.{Provision, WebhooksProcessor, Webhook}
import org.jboss.netty.handler.codec.http.{HttpResponse, HttpRequest}
import org.mockito.Mockito._
import org.specs2.specification.Before

import scala.util.Try

/**
 * User: maximn
 * Date: 12/2/14
 */
class WebhookServerWithSimplicator extends BaseWebhooksIT with SimplicatorWebhooksDriver {

}
