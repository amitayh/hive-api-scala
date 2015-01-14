package com.wix.hive.server.adapters

import java.io.BufferedReader
import javax.servlet.http.HttpServletRequest

import com.wix.hive.client.http.{HttpRequestData, NamedParameters}

import scala.collection.mutable
import scala.language.implicitConversions

/**
 * User: maximn
 * Date: 12/7/14
 */

object Servlet {

  implicit object RequestConverterFromServlet extends RequestConverterFrom[HttpServletRequest] {

    override def convert(req: HttpServletRequest): HttpRequestData = {
      if (req.getRequestURI.contains("?"))
        throw new UnsupportedOperationException(s"Bad URL: ${req.getRequestURI}. It shouldn't contain a '?' sign (Query string parameters)")

      val url = req.getRequestURI
      val method = req.getMethod match {
        case "POST" => com.wix.hive.client.http.HttpMethod.POST
        case unsupportedMethod => throw new UnsupportedOperationException(s"The method '$unsupportedMethod' is not supported. You can only use POST.")
      }

      val headers = getHeaders(req)

      val content = getContentAsString(req.getReader)
      val body = if (content.nonEmpty) new Some(content) else None

      HttpRequestData(method, url, headers = headers, body = body)
    }

    private def getContentAsString(reader: BufferedReader): String = {
      val buffer = new mutable.StringBuilder()
      var line: String = ""
      while ( {line = reader.readLine(); line != null}) {
        buffer.append(line)
      }
      buffer.toString()
    }

    private def getHeaders(req: HttpServletRequest): NamedParameters = {
      import scala.collection.mutable

      val headers = new mutable.HashMap[String, String]()

      val headerNames = req.getHeaderNames
      while (headerNames.hasMoreElements) {
        val key = headerNames.nextElement
        val value = req.getHeader(key)
        headers.put(key, value)
      }

      collection.immutable.Map(headers.toSeq: _*)
    }
  }
}
