package com.wix.hive.server.resolvers.springframework

import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.specification.Scope
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.web.context.request.ServletWebRequest

class WixInstanceRequestResolversTest extends SpecificationWithJUnit {

  trait WebContext extends Scope {
    val servletRequest = new MockHttpServletRequest()
    def webRequest = new ServletWebRequest(servletRequest)
  }

  "HeaderRequestResolver" should {

    trait ctx extends WebContext {
      val headerName = "X-Wix-Instance"
      val resolver = new HeaderRequestResolver(headerName)
    }

    "extract instance from header" in new ctx {
      val headerValue = "signed-instance"
      servletRequest.addHeader(headerName, headerValue)

      resolver(webRequest) must equalTo(headerValue)
    }

    "should not be defined if header is missing" in new ctx {
      resolver.isDefinedAt(webRequest) must beFalse
    }
  }

  "QueryParamRequestResolver" should {

    trait ctx extends WebContext {
      val paramName = "instance"
      val resolver = new QueryParamRequestResolver(paramName)
    }

    "extract instance from query param" in new ctx {
      val value = "signed-header"
      servletRequest.addParameter(paramName, value)

      resolver(webRequest) must equalTo(value)
    }

    "should not be defined if param is missing" in new ctx {
      resolver.isDefinedAt(webRequest) must beFalse
    }
  }

}



