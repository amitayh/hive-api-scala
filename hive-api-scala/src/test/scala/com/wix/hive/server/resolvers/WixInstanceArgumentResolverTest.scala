package com.wix.hive.server.resolvers

import com.wix.hive.drivers.InstanceEncoderSupport
import com.wix.hive.server.instance.{InstanceDecoderScope, WixInstance}
import com.wix.hive.server.resolvers.Spring._
import org.joda.time.{DateTime, DateTimeZone}
import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.specification.Scope
import org.springframework.core.MethodParameter
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.web.context.request.ServletWebRequest
import org.springframework.web.method.support.{HandlerMethodArgumentResolver, ModelAndViewContainer}

class WixInstanceArgumentResolverTest extends SpecificationWithJUnit {

  "WixInstanceRequestResolver" should {

    "HeaderRequestResolver" should {

      trait Context extends Scope {
        val headerName = "X-Wix-Instance"
        val resolver = new HeaderRequestResolver(headerName)
        val servletRequest = new MockHttpServletRequest()
      }

      "extract instance from header" in new Context {
        val headerValue = "signed-instance"

        servletRequest.addHeader(headerName, headerValue)
        val request = new ServletWebRequest(servletRequest)

        resolver(request) must equalTo(headerValue)
      }

      "should not be defined if header is missing" in new Context {
        val request = new ServletWebRequest(servletRequest)

        resolver.isDefinedAt(request) must beFalse
      }
    }

    "QueryParamRequestResolver" should {

      trait Context extends Scope {
        val paramName = "instance"
        val resolver = new QueryParamRequestResolver(paramName)
        val servletRequest = new MockHttpServletRequest()
      }

      "extract instance from query param" in new Context {
        val value = "signed-header"

        servletRequest.addParameter(paramName, value)
        val request = new ServletWebRequest(servletRequest)

        resolver(request) must equalTo(value)
      }

      "should not be defined if param is missing" in new Context {
        val request = new ServletWebRequest(servletRequest)

        resolver.isDefinedAt(request) must beFalse
      }
    }

  }

  "WixInstanceArgumentResolver" should {

    trait Context extends InstanceDecoderScope {

      def handleMethodParameter(index: Int) = {
        val handleMethod = classOf[ExampleController].getMethods.head
        new MethodParameter(handleMethod, index)
      }

      def stringParameter = handleMethodParameter(0)

      def wixInstanceParameter = handleMethodParameter(1)

    }

    "implement HandlerMethodArgumentResolver" in new Context {
      val resolver = new WixInstanceArgumentResolver(decoder)

      resolver must beAnInstanceOf[HandlerMethodArgumentResolver]
    }

    "not support method parameters of types different than WixInstance" in new Context {
      val resolver = new WixInstanceArgumentResolver(decoder)

      resolver.supportsParameter(stringParameter) must beFalse
    }

    "support method parameters of type WixInstance" in new Context {
      val resolver = new WixInstanceArgumentResolver(decoder)

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

      val signedInstance = signAndEncodeInstance(instance, key)

      val failingRequestResolver: WixInstanceRequestResolver = {
        case _ if false => "invalid-instance"
      }

      val succeedingRequestResolver: WixInstanceRequestResolver = {
        case _ => signedInstance
      }

      def webRequest = new ServletWebRequest(request)

    }

    "resolve Wix instance from request" in new WebContext {
      val resolver = new WixInstanceArgumentResolver(decoder, succeedingRequestResolver)

      resolver.resolveArgument(wixInstanceParameter, mavContainer, webRequest, null) must equalTo(instance)
    }

    "chain multiple request resolvers" in new WebContext {
      val resolver = new WixInstanceArgumentResolver(decoder, failingRequestResolver, succeedingRequestResolver)

      resolver.resolveArgument(wixInstanceParameter, mavContainer, webRequest, null) must equalTo(instance)
    }

    "throw when unable to resolve instance from request" in new WebContext {
      val resolver = new WixInstanceArgumentResolver(decoder, failingRequestResolver)

      resolver.resolveArgument(wixInstanceParameter, mavContainer, webRequest, null) must
        throwA[UnableToExtractInstanceException]
    }

    "throw when instance is invalid" in new WebContext {
      val requestResolver: WixInstanceRequestResolver = {
        case _ => "invalid-instance"
      }
      val resolver = new WixInstanceArgumentResolver(decoder, requestResolver)

      resolver.resolveArgument(wixInstanceParameter, mavContainer, webRequest, null) must
        throwA[InstanceValidationException]
    }

  }

}

class ExampleController {
  def handle(string: String, instance: WixInstance) = true
}

