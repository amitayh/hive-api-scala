package com.wix.hive.client

import com.wix.hive.client.http._
import com.wix.hive.commands.HiveBaseCommand
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat

import scala.concurrent.Future
import scala.reflect.ClassTag

private object DefaultHttpClientFactory {
  def create: AsyncHttpClient = new DispatchHttpClient()
}

private object DefaultConfigurations{
  val baseUrl = "https://openapi.wix.com"
}

class HiveClient(val appId: String, val secretKey: String, val instanceId: String,
                 httpClient: AsyncHttpClient = DefaultHttpClientFactory.create,
                  val baseUrl: String = DefaultConfigurations.baseUrl) {
  def timestamp: String = new DateTime().toString(ISODateTimeFormat.dateTime())

  val version = "1.0.0"

  val versionForUrl = "/v" + version.split('.').head

  val agent = s"Hive Scala v$version"

  object HeaderKeys {
    val instanceId = "x-wix-instance-id"
    val applicationId = "x-wix-application-id"
    val timestamp = "x-wix-timestamp"
    val signature = "x-wix-signature"
    val userAgent = "User-Agent"
  }

  object QueryKeys {
    val version = "version"
  }


  def execute[TCommandResult : ClassTag](command: HiveBaseCommand[TCommandResult]): Future[TCommandResult] = {
    val httpDataFromCommand = command.createHttpRequestData

    val httpDataForRequest = (withClientData _ andThen withSignature andThen withBaseUrl)(httpDataFromCommand)

    httpClient.request(httpDataForRequest)
  }

  def withSignature(httpData: HttpRequestData): HttpRequestData = {
    val signature = HiveSigner.getSignature(secretKey, httpData)
    httpData.copy(headers = httpData.headers + (HeaderKeys.signature -> signature))
  }

  def withClientData(httpData: HttpRequestData): HttpRequestData = {
    httpData.copy(
      url = s"$versionForUrl${httpData.url}",
      queryString = httpData.queryString + (QueryKeys.version -> this.version) ,
      headers = httpData.headers +
        (HeaderKeys.instanceId -> this.instanceId) +
        (HeaderKeys.applicationId -> this.appId) +
        (HeaderKeys.timestamp -> this.timestamp) +
        (HeaderKeys.userAgent -> this.agent))
  }

  def withBaseUrl(httpData: HttpRequestData): HttpRequestData = httpData.copy(url = baseUrl + httpData.url)
}