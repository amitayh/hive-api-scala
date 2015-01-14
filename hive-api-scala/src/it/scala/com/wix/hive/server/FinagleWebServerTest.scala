package com.wix.hive.server

import com.twitter.finagle.{Http, Service}
import com.twitter.util.{Await, Duration}
import com.wix.hive.client._
import com.wix.hive.client.http._
import com.wix.hive.matchers.HiveMatchers
import org.jboss.netty.buffer.ChannelBuffers
import org.jboss.netty.handler.codec.http.{HttpMethod, _}
import org.specs2.matcher.Matcher
import org.specs2.mock.Mockito
import org.specs2.mutable.{Before, SpecificationWithJUnit}

/**
 * User: maximn
 * Date: 11/28/14
 */
class FinagleWebServerTest extends SpecificationWithJUnit with Mockito {
  sequential


  val func = mock[HttpRequestData => Unit]

  val srv = new FinagleWebServer(8000) {
    override def process(data: HttpRequestData): Unit = func(data)
  }

  srv.start()




  trait ctx extends Before
  with HiveMatchers {
    override def before: Any = org.mockito.Mockito.reset(func)

    val client: Service[HttpRequest, HttpResponse] = Http.newService("localhost:8000")



    def aWebReq(uri: String = "/", content: String = "", headers: Map[String, String] = Map.empty) = {
      val req = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, uri)
      req.setContent(ChannelBuffers.copiedBuffer(content, java.nio.charset.Charset.forName("UTF8")))
      headers.foreach { case (key, value) => req.headers.add(key, value)}

      req
    }

    def aDefaultMatcher(method: Matcher[http.HttpMethod.HttpMethod] = be_===(http.HttpMethod.POST),
                        url: Matcher[String] = be_===("/"),
                        query: Matcher[NamedParameters] = beEmpty,
                        headers: Matcher[NamedParameters] = beLike { case _:NamedParameters => ok},
                        body: Matcher[Option[AnyRef]] = beNone
                         ) = httpRequestDataWith(method, url, query, headers, body)
  }

  "server" should {
    "simple req" in new ctx {
      val req = aWebReq()

      Await.ready(client(req))

      there was one(func).apply(aDefaultMatcher())
    }

    "req with URI" in new ctx {
      val req = aWebReq(uri = "/someOther")

      Await.ready(client(req))

      there was one(func).apply(aDefaultMatcher(url = "/someOther"))
    }

    "req with content" in new ctx {
      val req = aWebReq(content = "test")

      Await.ready(client(req))

      there was one(func).apply(aDefaultMatcher(body = beSome("test")))
    }

    "req with headers" in new ctx {
      val req = aWebReq(headers = Map("key" -> "value"))

      Await.ready(client(req))

      there was one(func).apply(aDefaultMatcher(headers = havePair("key" -> "value")))
    }


  }

  step {
    Await.ready(srv.stop(Duration.fromMilliseconds(500)))
  }
}