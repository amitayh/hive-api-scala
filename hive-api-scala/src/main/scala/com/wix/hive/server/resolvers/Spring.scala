package com.wix.hive.server.resolvers

import javax.annotation.PostConstruct

import com.wix.hive.server.instance.{InstanceDecoder, WixInstance}
import org.springframework.context.annotation.Bean
import org.springframework.core.MethodParameter
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.{NativeWebRequest, WebRequest}
import org.springframework.web.method.support.{HandlerMethodArgumentResolver, ModelAndViewContainer}
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter

import scala.collection.JavaConversions._
import scala.util.{Failure, Try}

object Spring {

  type WixInstanceRequestResolver = PartialFunction[WebRequest, String]

  class HeaderRequestResolver(headerName: String) extends WixInstanceRequestResolver {
    override def isDefinedAt(request: WebRequest): Boolean = request.getHeader(headerName) != null
    override def apply(request: WebRequest): String = request.getHeader(headerName)
  }

  class QueryParamRequestResolver(paramName: String) extends WixInstanceRequestResolver {
    override def isDefinedAt(request: WebRequest): Boolean = request.getParameter(paramName) != null
    override def apply(request: WebRequest): String = request.getParameter(paramName)
  }

  class WixInstanceArgumentResolver(instanceDecoder: InstanceDecoder,
                                    requestResolvers: WixInstanceRequestResolver*) extends HandlerMethodArgumentResolver {

    lazy val requestResolver = requestResolvers.reduce(_ orElse _)

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
      val default: WebRequest => String = r => throw new UnableToExtractInstanceException(r)
      Try(requestResolver.applyOrElse(webRequest, default))
    }

    private def decodeSignedInstance(signedInstance: String): Try[WixInstance] = {
      instanceDecoder.decode(signedInstance) recoverWith {
        case e => Failure(new InstanceValidationException(e))
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

    def wixInstanceHeaderName = "X-Wix-Instance"

    def wixInstanceQueryParamName = "instance"

    @Bean
    def instanceDecoder = new InstanceDecoder(wixInstanceSecretKey)

    @Bean
    def instanceArgumentResolver = new WixInstanceArgumentResolver(
      instanceDecoder,
      new HeaderRequestResolver(wixInstanceHeaderName),
      new QueryParamRequestResolver(wixInstanceQueryParamName))

    @Bean
    def argumentResolversRegistrar(requestMappingAdapter: RequestMappingHandlerAdapter) =
      new ArgumentResolversRegistrar(requestMappingAdapter, instanceArgumentResolver)

  }

  class UnableToExtractInstanceException(request: WebRequest)
    extends RuntimeException("Unable to resolve instance from request")

  class InstanceValidationException(cause: Throwable)
    extends RuntimeException("Unable to decode instance", cause)

}
