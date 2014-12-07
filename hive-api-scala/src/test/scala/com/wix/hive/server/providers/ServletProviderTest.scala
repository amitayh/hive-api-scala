package com.wix.hive.server.providers

import java.net.URI
import javax.servlet.ServletRequest

import com.wix.hive.client.http.HttpMethod
import org.specs2.mutable.{SpecificationWithJUnit, Specification}
import org.specs2.specification.Scope
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder

/**
 * User: maximn
 * Date: 12/7/14
 */
class ServletProviderTest extends SpecificationWithJUnit {

  trait ctx extends Scope {
    val header = ("key", "val")
    val content = "some-data"
    val url = "http://wix.com/testurl"
    val method = "POST"
    val methodEnum = HttpMethod.POST
    val req = new MockHttpServletRequest(method, url)
    req.addHeader(header._1, header._2)
    req.setContent(content.getBytes)
  }


  "convert the Servlet Req to Hive SDK request" in new ctx {
    val converted = com.wix.hive.server.providers.ServletProvider.servletReq2myReq(req)

    converted.url must be_===(url)
    converted.headers must havePair(header)
    converted.method must be_===(methodEnum)
    converted.body must beSome(content)
  }
}
