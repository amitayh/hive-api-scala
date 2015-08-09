package com.wix.hive.server.resolvers

import javax.annotation.PostConstruct

import com.wix.hive.server.instance.{InstanceDecoder, WixInstance}
import org.springframework.context.annotation.Bean
import org.springframework.core.MethodParameter
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.{HandlerMethodArgumentResolver, ModelAndViewContainer}
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter

import scala.collection.JavaConversions._
import scala.util.{Failure, Success, Try}

object Spring {

  class WixInstanceArgumentResolver(instanceDecoder: InstanceDecoder,
                                    headerName: String) extends HandlerMethodArgumentResolver {

    override def resolveArgument(parameter: MethodParameter,
                                 mavContainer: ModelAndViewContainer,
                                 webRequest: NativeWebRequest,
                                 binderFactory: WebDataBinderFactory): WixInstance = {
      val result = for {
        signedInstance <- extractSignedInstance(webRequest)
        instance <- decodeSignedInstance(signedInstance)
      } yield instance
      result.get
    }

    override def supportsParameter(parameter: MethodParameter): Boolean = {
      parameter.getParameterType.equals(classOf[WixInstance])
    }

    private def extractSignedInstance(webRequest: NativeWebRequest): Try[String] = {
      webRequest.getHeader(headerName) match {
        case signedInstance: String => Success(signedInstance)
        case null => Failure(new InstanceValidationError(s"Header '$headerName' is missing"))
      }
    }

    private def decodeSignedInstance(signedInstance: String): Try[WixInstance] = {
      instanceDecoder.decode(signedInstance) recoverWith {
        case e => Failure(new InstanceValidationError("Unable to decode instance", e))
      }
    }

  }

  class ArgumentResolversRegistrar(requestMappingAdapter: RequestMappingHandlerAdapter,
                                   newResolvers: HandlerMethodArgumentResolver*) {

    @PostConstruct
    def register(): Unit = {
      val currentResolvers = requestMappingAdapter.getArgumentResolvers
      requestMappingAdapter.setArgumentResolvers(newResolvers ++ currentResolvers)
    }

  }

  trait WixInstanceDecoderSupport {

    def wixInstanceSecretKey: String

    def wixInstanceHeaderName: String

    @Bean
    def instanceDecoder = new InstanceDecoder(wixInstanceSecretKey)

    @Bean
    def instanceArgumentResolver = new WixInstanceArgumentResolver(instanceDecoder, wixInstanceHeaderName)

    @Bean
    def argumentResolversRegistrar(requestMappingAdapter: RequestMappingHandlerAdapter) =
      new ArgumentResolversRegistrar(requestMappingAdapter, instanceArgumentResolver)

  }

  class InstanceValidationError(message: String, cause: Throwable) extends RuntimeException(message, cause) {
    def this(message: String) = this(message, null)
  }

}
