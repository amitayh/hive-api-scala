package com.wix.hive.server.resolvers

import com.wix.hive.drivers.InstanceEncoderSupport
import com.wix.hive.server.instance.{InstanceDecoderScope, WixInstance}
import com.wix.hive.server.resolvers.Spring.{InstanceValidationError, WixInstanceArgumentResolver}
import org.joda.time.{DateTime, DateTimeZone}
import org.specs2.mock.Mockito
import org.specs2.mutable.SpecificationWithJUnit
import org.springframework.core.MethodParameter
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.{HandlerMethodArgumentResolver, ModelAndViewContainer}

class WixInstanceArgumentResolverTest extends SpecificationWithJUnit with Mockito {

  "WixInstanceArgumentResolver" should {

    trait Context extends InstanceDecoderScope with InstanceEncoderSupport {

      class ExampleController {
        def handle(string: String, instance: WixInstance) = true
      }

      val headerName = "X-Wix-Instance"

      val resolver = new WixInstanceArgumentResolver(decoder, headerName)

      val mavContainer = mock[ModelAndViewContainer]

      val binderFactory = mock[WebDataBinderFactory]

      val request = mock[NativeWebRequest]

      val instance = WixInstance(
        instanceId = instanceId,
        signedAt = new DateTime(signDate).withZone(DateTimeZone.UTC),
        userId = Some(uid),
        permissions = Set(permission),
        userIp = ipAndPort,
        premiumPackageId = Some(premiumPackage),
        demoMode = false,
        ownerId = ownerId)

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

    "resolve Wix instance from HTTP header" in new Context {
      val signedInstance = signAndEncodeInstance(instance, key)
      request.getHeader(headerName) returns signedInstance
      resolver.resolveArgument(wixInstanceParameter, mavContainer, request, binderFactory) must equalTo(instance)
    }

    "throw when instance header is missing" in new Context {
      resolver.resolveArgument(wixInstanceParameter, mavContainer, request, binderFactory) must
        throwA[InstanceValidationError]("Header 'X-Wix-Instance' is missing")
    }

    "throw when instance is signed with invalid key" in new Context {
      val signedInstance = signAndEncodeInstance(instance, "invalid key lol")
      request.getHeader(headerName) returns signedInstance
      resolver.resolveArgument(wixInstanceParameter, mavContainer, request, binderFactory) must
        throwA[InstanceValidationError]
    }

  }

}
