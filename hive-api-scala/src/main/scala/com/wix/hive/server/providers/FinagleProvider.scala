package com.wix.hive.server.providers

import com.wix.hive.client.http.HttpRequestData
import org.jboss.netty.handler.codec.http.{HttpMethod, HttpRequest}

import scala.collection.JavaConversions._
import scala.language.implicitConversions

/**
 * User: maximn
 * Date: 12/1/14
 */
object FinagleProvider {
  implicit def finagleReq2myReq(req: HttpRequest): HttpRequestData = {
    if (req.getUri.contains("?"))
      throw new UnsupportedOperationException(s"Bad URL: ${req.getUri}. It shouldn't contain a '?' sign (Query string parameters)")

    val url = req.getUri
    val method = req.getMethod match {
      case HttpMethod.POST => com.wix.hive.client.http.HttpMethod.POST
      case unsupportedMethod => throw new UnsupportedOperationException(s"The method '$unsupportedMethod' is not supported. You can only use POST.")
    }

    val headers = req.headers().map(h => (h.getKey, h.getValue)).toMap

    val content = new String(req.getContent.array())
    val body = if (content.nonEmpty) new Some(content) else None

    HttpRequestData(method, url, headers = headers, body = body)
  }
}