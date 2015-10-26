package com.wix.hive.server.resolvers.springframework

import com.wix.hive.drivers.InstanceEncoderSupport
import com.wix.hive.server.instance.{InstanceDecoderScope, WixInstance}
import org.joda.time.{DateTime, DateTimeZone}
import org.specs2.mutable.SpecificationWithJUnit
import org.springframework.core.MethodParameter
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.web.context.request.ServletWebRequest
import org.springframework.web.method.support.{HandlerMethodArgumentResolver, ModelAndViewContainer}

class WixInstanceArgumentResolverTest extends SpecificationWithJUnit {

  "WixInstanceArgumentResolver" should {

    trait ctx extends InstanceDecoderScope {

      def handleMethodParameter(index: Int) = {
        val handleMethod = classOf[SampleController].getMethods.head
        new MethodParameter(handleMethod, index)
      }

      def stringParameter = handleMethodParameter(0)

      def wixInstanceParameter = handleMethodParameter(1)

    }

    "implement HandlerMethodArgumentResolver" in new ctx {
      val resolver = new WixInstanceArgumentResolver(decoder)

      resolver must beAnInstanceOf[HandlerMethodArgumentResolver]
    }

    "not support method parameters of types different than WixInstance" in new ctx {
      val resolver = new WixInstanceArgumentResolver(decoder)

      resolver.supportsParameter(stringParameter) must beFalse
    }

    "support method parameters of type WixInstance" in new ctx {
      val resolver = new WixInstanceArgumentResolver(decoder)

      resolver.supportsParameter(wixInstanceParameter) must beTrue
    }

    trait WebContext extends ctx with InstanceEncoderSupport {

      val mavContainer = new ModelAndViewContainer()

      val request = new ServletWebRequest(new MockHttpServletRequest())

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
      val invalidInstance = "invalid-instance"

      val failingRequestResolver: WixInstanceRequestResolver = {
        case _ if false => invalidInstance
      }

      val invalidInstanceRequestResolver: WixInstanceRequestResolver = {
        case _ => invalidInstance
      }

      val succeedingRequestResolver: WixInstanceRequestResolver = {
        case _ => signedInstance
      }

    }

    "resolve Wix instance from request" in new WebContext {
      val resolver = new WixInstanceArgumentResolver(decoder, succeedingRequestResolver)

      resolver.resolveArgument(wixInstanceParameter, mavContainer, request, null) must equalTo(instance)
    }

    "chain multiple request resolvers" in new WebContext {
      val resolver = new WixInstanceArgumentResolver(decoder, failingRequestResolver, succeedingRequestResolver)

      resolver.resolveArgument(wixInstanceParameter, mavContainer, request, null) must equalTo(instance)
    }

    "throw when unable to resolve instance from request" in new WebContext {
      val resolver = new WixInstanceArgumentResolver(decoder, failingRequestResolver)

      resolver.resolveArgument(wixInstanceParameter, mavContainer, request, null) must
        throwA[UnableToExtractInstanceException]
    }

    "throw when instance is invalid" in new WebContext {
      val resolver = new WixInstanceArgumentResolver(decoder, invalidInstanceRequestResolver)

      resolver.resolveArgument(wixInstanceParameter, mavContainer, request, null) must
        throwA[InstanceValidationException]
    }

  }

}

class SampleController {
  def handle(string: String, instance: WixInstance) = true
}

