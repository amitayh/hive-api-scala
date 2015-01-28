package com.wix.hive.webhooks

import com.github.tomakehurst.wiremock.http.Request
import com.wix.hive.client.http.HttpRequestData
import com.wix.hive.server.adapters.RequestConverterFrom
import scala.collection.JavaConversions._

/**
 * User: maximn
 * Date: 1/28/15
 */
object WiremockRequestConverter {

  implicit object RequestConverterFromWiremock extends RequestConverterFrom[Request] {
    override def convert(req: Request): HttpRequestData = {
      val url = req.getAbsoluteUrl
      val method = req.getMethod match {
        case com.github.tomakehurst.wiremock.http.RequestMethod.POST => com.wix.hive.client.http.HttpMethod.POST
        case unsupportedMethod => throw new UnsupportedOperationException(s"The method '$unsupportedMethod' is not supported. You can only use POST.")
      }
        val headers = for {
          header <- req.getHeaders.all()
          value <- header.values()
        } yield header.key() -> value

        val body = Some(req.getBodyAsString)

        HttpRequestData(method, url, headers = headers.toMap, body = body)
    }

  }
}
