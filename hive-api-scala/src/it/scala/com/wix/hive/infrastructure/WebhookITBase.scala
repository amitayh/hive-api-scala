package com.wix.hive.infrastructure

import com.wix.hive.server.webhooks.{WebhookBase, WebhookData}
import org.specs2.matcher.Matcher
import org.specs2.mutable.{After, SpecificationWithJUnit}

import scala.util.Try

/**
 * User: maximn
 * Date: 1/13/15
 */

trait WebhookITBase extends SpecificationWithJUnit {
  self: WebhookSimplicatorIT =>
  sequential

  trait ctx extends After {
    self: SimplicatorWebhooksDriver =>

    val path: String = webhookPath
    val secret: String = webhookSecret
    val port: Int = webhookPort


    val mockFunc = mock[Try[WebhookBase[_]] => Unit]
    subscribeFunc(mockFunc)

    override def after: Any = clearListener()


    def verifyWebhookWith[W <: WebhookBase[WebhookData]](matcher: Matcher[W]): Unit = {
      eventually {
        there was one(mockFunc).apply(beSuccessfulTry[W].withValue(matcher))
      }
    }
  }
}