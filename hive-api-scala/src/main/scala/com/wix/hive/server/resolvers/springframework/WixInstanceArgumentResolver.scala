package com.wix.hive.server.resolvers.springframework

import com.wix.hive.server.instance.{InstanceDecoder, WixInstance}
import org.springframework.core.MethodParameter
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.{NativeWebRequest, WebRequest}
import org.springframework.web.method.support.{HandlerMethodArgumentResolver, ModelAndViewContainer}

import scala.util.{Failure, Try}

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

class UnableToExtractInstanceException(request: WebRequest)
  extends RuntimeException("Unable to resolve instance from request")

class InstanceValidationException(cause: Throwable)
  extends RuntimeException("Unable to decode instance", cause)
