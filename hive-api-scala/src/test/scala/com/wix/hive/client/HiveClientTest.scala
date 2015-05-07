package com.wix.hive.client

import com.wix.hive.client.http.HttpMethod.HttpMethod
import com.wix.hive.client.http.{AsyncHttpClient, HttpMethod, NamedParameters}
import com.wix.hive.commands.HiveCommand
import com.wix.hive.matchers.HiveMatchers
import com.wix.hive.model.{HiveClientException, WixAPIErrorException}
import org.specs2.mock.Mockito
import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.specification.Scope

import scala.concurrent.Future


class HiveClientTest extends SpecificationWithJUnit with Mockito with HiveMatchers {

  class Context extends Scope {
    val httpClient = mock[AsyncHttpClient]

    val id = "appId"
    val key = "appKey"
    val instance = "websiteInstance"

    val baseUrl = "http://wix.com/"
    val client = HiveClient(Some(id), Some(key), httpClient = Some(httpClient), baseUrl = Some(baseUrl))

    def verifyOneCallWithCorrectParams = there was one(httpClient).request(httpRequestDataWith(
      method = be_===(HttpMethod.GET),
      url = be_===(client.baseUrl + HiveClient.versionForUrl + commandUrl + commandParams),
      query = havePairs(commandQuery.toSeq: _*),
      headers = headersFor(commandHeaders, client, instance),
      body = be_===(commandBody)))(any)

    def givenAHttpClientReturnsAResponse =
      httpClient.request(any)(===(scala.reflect.classTag[TestCommandResponse])) returns Future.successful(TestCommandResponse())

    def givenAHttpClientFailsWith(anException: Throwable) =
      httpClient.request(any)(===(scala.reflect.classTag[TestCommandResponse])) returns Future.failed(anException)
  }

  "HiveClient" should {
    "wrap unhandled exceptions thrown from AsyncHttpClient" in new Context {
      givenAHttpClientFailsWith(new RuntimeException("an unhandled exception."))

      client.execute(instance, TestCommand()) must throwA[HiveClientException]((".*an unhandled exception.*")).await
    }

    "pass-through WixAPIErrorException thrown from AsyncHttpClient" in new Context {
      givenAHttpClientFailsWith(new WixAPIErrorException(400, Some("bad request"), None))

      client.execute(instance, TestCommand()) must throwA[WixAPIErrorException].await
    }
  }

  "execute" should {

    "call the http client with the correct parameters" in new Context {
      givenAHttpClientReturnsAResponse

      client.execute(instance, TestCommand()) must be_==(TestCommandResponse()).await

      verifyOneCallWithCorrectParams
    }
  }


  "executeForInstance" should {

    "call the http client with the correct parameters" in new Context {
      givenAHttpClientReturnsAResponse

      val executor = client.executeForInstance(instance)

      executor(TestCommand())

      verifyOneCallWithCorrectParams
    }.pendingUntilFixed("command does not infer acutal command response type")
  }


  "apply" should {
    "load with configuration from conf file" >> {
      val client = HiveClient()

      client.appId must be_===("your app-id here")
      client.baseUrl must be_===("https://openapi.wix.com")
    }
  }


  "object" should {
    "compose with no slashes" >> {
      HiveClient.compose("a", "b") must be_===("a/b")
    }

    "compose with base slashes" >> {
      HiveClient.compose("a/", "b") must be_===("a/b")
    }

    "compose with suffix slashes" >> {
      HiveClient.compose("a", "/b") must be_===("a/b")
    }

    "compose with both slashes" >> {
      HiveClient.compose("a/", "/b") must be_===("a/b")
    }
  }

  val commandUrl = "/tst"
  val commandParams = "/param"
  val commandQuery = Map("q" -> "query")
  val commandHeaders = Map("h" -> "header")
  val commandBody = Some(AnyRef)

  case class TestCommand() extends HiveCommand[TestCommandResponse] {
    override def url: String = commandUrl

    override def urlParams: String = commandParams

    override def query: NamedParameters = commandQuery

    override def headers: NamedParameters = commandHeaders

    override def body: Option[AnyRef] = commandBody

    override def method: HttpMethod = HttpMethod.GET
  }

  case class TestCommandResponse()

}

