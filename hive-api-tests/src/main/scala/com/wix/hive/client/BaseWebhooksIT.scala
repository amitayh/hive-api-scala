package com.wix.hive.client

import com.twitter.finagle.{Service, Http}
import com.twitter.util.Duration
import com.wix.hive.client.http.HttpRequestData
import com.wix.hive.client.infrastructure.{AppDef, WebhooksDriver, HiveApiDrivers}
import com.wix.hive.server.FinagleWebServer
import com.wix.hive.server.webhooks.{WebhookParameters, Provision, Webhook, WebhooksProcessor}
import org.jboss.netty.handler.codec.http.{HttpRequest, HttpResponse}
import org.joda.time.DateTime
import org.specs2.mock.Mockito
import org.specs2.specification.Before
import org.specs2.time.NoTimeConversions
import org.mockito.Mockito._
import scala.util.{Success, Try}
import org.specs2.execute.AsResult._

/**
 * User: maximn
 * Date: 12/2/14
 */
abstract class BaseWebhooksIT extends BaseIT with Mockito {
  self: WebhooksDriver =>

  val port: Int = 8001
  val url: String = "localhost"

  override def initEnv(): Unit = srv.start()

  override def beforeTest(): Unit = reset(mockFunc)

  override def shutdownEnv(): Unit = srv.stop(Duration.fromMilliseconds(500))

  val mockFunc = mock[Try[Webhook] => Unit]

  val secret = "sdad"
  val processor = new WebhooksProcessor(secret)
  val srv = new FinagleWebServer(8001) {
    override def process[T <% HttpRequestData](data: HttpRequestData): Unit = {
      mockFunc(processor.convert(data))
    }
  }

  step(initEnv())


  trait ctx extends Before {
    override def before: Any = beforeTest()

    val client: Service[HttpRequest, HttpResponse] = Http.newService("localhost:8001")

    val appId = "31cafe00-517a-4e20-9955-a77ed8aa7b15"
    val instanceId = "79502740-ff16-4ff0-9d55-fbac00964b64"
    val secret = "sec"

    val appDef = AppDef(appId, instanceId, secret)

    val provision = Provision("aaaa", None)
  }


  "webhooks" should {
    "provision" in new ctx {
      callProvisionWebhook(appDef, provision)

      there was one(mockFunc).apply(Success(Webhook(instanceId, provision, WebhookParameters(appId, any))))
      //verifyProvisionWebhook(appDef, provision)
    }
  }


  step(shutdownEnv())
}
