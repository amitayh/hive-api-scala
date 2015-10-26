package com.wix.hive.server.resolvers.springframework

import javax.annotation.PostConstruct

import com.wix.hive.server.instance.InstanceDecoder
import org.springframework.context.annotation.Bean
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter

import scala.collection.JavaConversions._

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

class ArgumentResolversRegistrar(requestMappingAdapter: RequestMappingHandlerAdapter,
                                 newResolvers: HandlerMethodArgumentResolver*) {

  @PostConstruct
  def register(): Unit = {
    val currentResolvers = requestMappingAdapter.getArgumentResolvers
    requestMappingAdapter.setArgumentResolvers(newResolvers ++ currentResolvers)
  }

}
