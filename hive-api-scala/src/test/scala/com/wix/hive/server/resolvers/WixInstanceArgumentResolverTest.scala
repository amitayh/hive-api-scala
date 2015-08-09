package com.wix.hive.server.resolvers

import com.wix.hive.drivers.InstanceEncoderSupport
import com.wix.hive.server.instance.{InstanceDecoderScope, WixInstance}
import com.wix.hive.server.resolvers.Spring.{InstanceValidationException, WixInstanceArgumentResolver}
import org.joda.time.{DateTime, DateTimeZone}
import org.specs2.mutable.SpecificationWithJUnit
import org.springframework.core.MethodParameter
import org.springframework.mock.web.{MockHttpServletRequest, MockHttpServletResponse}
import org.springframework.web.context.request.ServletWebRequest
import org.springframework.web.method.support.{HandlerMethodArgumentResolver, ModelAndViewContainer}

class WixInstanceArgumentResolverTest extends SpecificationWithJUnit {

  "WixInstanceArgumentResolver" should {

    trait Context extends InstanceDecoderScope {

      class ExampleController {
        def handle(string: String, instance: WixInstance) = true
      }

      val headerName = "X-Wix-Instance"

      val resolver = new WixInstanceArgumentResolver(decoder, headerName)

      def handleMethodParameter(index: Int) = {
        val handleMethod = classOf[ExampleController].getMethods.head
        new MethodParameter(handleMethod, index)
      }

      def stringParameter = handleMethodParameter(0)

      def wixInstanceParameter = handleMethodParameter(1)

    }

    "implement HandlerMethodArgumentResolver" in new Context {
      resolver must beAnInstanceOf[HandlerMethodArgumentResolver]
    }

    "not support method parameters of types different than WixInstance" in new Context {
      resolver.supportsParameter(stringParameter) must beFalse
    }

    "support method parameters of type WixInstance" in new Context {
      resolver.supportsParameter(wixInstanceParameter) must beTrue
    }

    trait WebContext extends Context with InstanceEncoderSupport {

      val mavContainer = new ModelAndViewContainer()

      val request = new MockHttpServletRequest()

      val instance = WixInstance(
        instanceId = instanceId,
        signedAt = new DateTime(signDate).withZone(DateTimeZone.UTC),
        userId = Some(uid),
        permissions = Set(permission),
        userIp = ipAndPort,
        premiumPackageId = Some(premiumPackage),
        demoMode = false,
        ownerId = ownerId)

      def webRequest = new ServletWebRequest(request, new MockHttpServletResponse())

    }

    "resolve Wix instance from HTTP header" in new WebContext {
      val signedInstance = signAndEncodeInstance(instance, key)
      request.addHeader(headerName, signedInstance)
      resolver.resolveArgument(wixInstanceParameter, mavContainer, webRequest, null) must equalTo(instance)
    }

    "throw when instance header is missing" in new WebContext {
      resolver.resolveArgument(wixInstanceParameter, mavContainer, webRequest, null) must
        throwA[InstanceValidationException]("Header 'X-Wix-Instance' is missing")
    }

    "throw when instance is signed with invalid key" in new WebContext {
      val signedInstance = signAndEncodeInstance(instance, "invalid key lol")
      request.addHeader(headerName, signedInstance)
      resolver.resolveArgument(wixInstanceParameter, mavContainer, webRequest, null) must
        throwA[InstanceValidationException]
    }

  }

}
