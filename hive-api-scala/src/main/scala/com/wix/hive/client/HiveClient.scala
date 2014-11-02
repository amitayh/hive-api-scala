package com.wix.hive.client

import com.wix.hive.client.http._
import com.wix.hive.commands.HiveBaseCommand
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat

import scala.concurrent.Future
import scala.reflect.ClassTag
import com.typesafe.config._

import scalaz.std.set

private object DefaultHttpClientFactory {
  def create: AsyncHttpClient = new DispatchHttpClient()
}

private object DefaultConfigurations {
  val baseUrl = "https://openapi.wix.com"
}

class HiveClientSettings(config: Config) {
  def this() = this(ConfigFactory.load())


  config.checkValid(ConfigFactory.defaultReference(), "hive-client")
  val appId = config.getString("hive-client.credentials.appId")
  val appSecret = config.getString("hive-client.credentials.appSecret")
  val baseUrl = config.getString("hive-client.credentials.baseUrl")
}

class HiveClient(val appId: String, secretKey: String, val instanceId: String,
                 httpClient: AsyncHttpClient = DefaultHttpClientFactory.create,
                 val baseUrl: String = DefaultConfigurations.baseUrl) {


  def timestamp: String = new DateTime().toString(ISODateTimeFormat.dateTime())

  val version = "1.0.0"

  val versionForUrl = "/v1"

  val agent = s"Hive Scala v$version"

  lazy val signer = new HiveSigner(secretKey)


  def execute[TCommandResult: ClassTag](command: HiveBaseCommand[TCommandResult]): Future[TCommandResult] = {
    val httpDataFromCommand = command.createHttpRequestData

    val httpDataForRequest = (withClientData _ andThen withSignature andThen withBaseUrl)(httpDataFromCommand)

    httpClient.request(httpDataForRequest)
  }

  def withSignature(httpData: HttpRequestData): HttpRequestData = {
    val signature = signer.getSignature(httpData)
    httpData.copy(headers = httpData.headers + (HiveClient.SignatureKey -> signature))
  }

  def withClientData(httpData: HttpRequestData): HttpRequestData = {
    httpData.copy(
      url = s"$versionForUrl${httpData.url}",
      queryString = httpData.queryString + (HiveClient.VersionKey -> this.version),
      headers = httpData.headers +
        (HiveClient.InstanceIdKey -> this.instanceId) +
        (HiveClient.ApplicationIdKey -> this.appId) +
        (HiveClient.TimestampKey -> this.timestamp) +
        (HiveClient.UserAgentKey -> this.agent))
  }

  def withBaseUrl(httpData: HttpRequestData): HttpRequestData = httpData.copy(url = baseUrl + httpData.url)
}

object HiveClient {
  val InstanceIdKey = "x-wix-instance-id"
  val ApplicationIdKey = "x-wix-application-id"
  val TimestampKey = "x-wix-timestamp"
  val SignatureKey = "x-wix-signature"
  val UserAgentKey = "User-Agent"

  val VersionKey = "version"


  def apply(instanceId: String) = {
    val settings = new HiveClientSettings()

    new HiveClient(settings.appId, settings.appSecret, instanceId, baseUrl = settings.baseUrl)
  }
}