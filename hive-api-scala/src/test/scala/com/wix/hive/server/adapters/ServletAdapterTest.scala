package com.wix.hive.server.adapters

import com.wix.hive.client.http.HttpMethod
import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.specification.Scope
import org.springframework.mock.web.MockHttpServletRequest



class ServletAdapterTest extends SpecificationWithJUnit {

  trait ctx extends BaseAdapterContext {
    val req = new MockHttpServletRequest(method, url)
    req.addHeader(header._1, header._2)
    req.setContent(content.getBytes)
  }


  "convert the Servlet Req to Hive SDK request" in new ctx {
    val converted = com.wix.hive.server.adapters.Servlet.RequestConverterFromServlet.convert(req)

    converted.url must be_===(url)
    converted.headers must havePair(header)
    converted.method must be_===(methodEnum)
    converted.body must beSome(content)
  }
}

