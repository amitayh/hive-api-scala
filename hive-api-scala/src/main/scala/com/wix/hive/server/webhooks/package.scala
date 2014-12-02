package com.wix.hive.server

import com.wix.hive.client.http.HttpRequestData
import com.wix.hive.client.http.HttpRequestDataImplicits._
import com.wix.hive.json.JacksonObjectMapper
import com.wix.hive.server.webhooks.exceptions.{BadFormattedWebhookException, MissingHeaderException}

import scala.util.control.NonFatal
import scala.util.{Failure, Success, Try}

/**
 * User: maximn
 * Date: 12/1/14
 */
package object webhooks {
  trait HttpRequestHelpers {
    def tryHeader(req: HttpRequestData, name: String): Try[String] = req.headers.get(name).fold[Try[String]](Failure(new MissingHeaderException(name)))(Success.apply)

    def tryUnmarshal(req: HttpRequestData, clas: Class[_]): Try[WebhookData] = {
      Try {JacksonObjectMapper.mapper.readValue(req.bodyAsString, clas).asInstanceOf[WebhookData]} recoverWith {
        case NonFatal(cause) => Failure(new BadFormattedWebhookException(req.bodyAsString, cause))
      }
    }
  }
}
