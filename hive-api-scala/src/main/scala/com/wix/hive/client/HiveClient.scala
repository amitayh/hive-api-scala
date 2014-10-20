package com.wix.hive.client

import com.wix.hive.Configuration
import com.wix.hive.client.http._
import com.wix.hive.commands.{HiveBaseCommand, HiveBaseCommandResponse}
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat

import scala.concurrent.Future
import scala.reflect.ClassTag

private object DefaultHttpClientFactory {
  def create: AsyncHttpClient = new DispatchHttpClient(Configuration.baseUrl)
}

private object DefaultConfigurations{
  val baseUrl = "https://openapi.wix.com/v1/"
}

class HiveClient(val appId: String, val secretKey: String, val instanceId: String,
                 httpClient: AsyncHttpClient = DefaultHttpClientFactory.create,
                  val baseUrl: String = DefaultConfigurations.baseUrl) {
  def timestamp: String = new DateTime().toString(ISODateTimeFormat.dateTime())

  val version = "1.0.0"

  val agent = s"Hive Scala v$version"

  object HeaderKeys {
    val instanceId = "X-Wix-Instance-Id"
    val applicationId = "X-Wix-Application-Id"
    val timestamp = "X-Wix-Timestamp"
    val signature = "X-Wix-Signature"
    val userAgent = "User-Agent"
  }


  def execute[TCommandResult <: HiveBaseCommandResponse : ClassTag](command: HiveBaseCommand[TCommandResult]): Future[TCommandResult] = {
    val httpDataFromCommand = command.createHttpRequestData

    val httpDataForRequest = (withClientData _ andThen withSignature _)(httpDataFromCommand)

    httpClient.request(httpDataForRequest)
  }

  def withSignature(httpData: HttpRequestData): HttpRequestData = {
    val signature = HiveSigner.getSignature(secretKey, httpData)
    httpData.copy(headers = httpData.headers + (HeaderKeys.signature -> signature))
  }

  def withClientData(httpData: HttpRequestData): HttpRequestData = {
    httpData.copy(
      url = baseUrl + httpData.url,
      headers = httpData.headers +
        (HeaderKeys.instanceId -> instanceId) +
        (HeaderKeys.applicationId -> appId) +
        (HeaderKeys.timestamp -> timestamp) +
        (HeaderKeys.userAgent -> agent))
  }
}