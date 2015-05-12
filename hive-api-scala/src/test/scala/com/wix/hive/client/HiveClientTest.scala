package com.wix.hive.client

import java.io.{ByteArrayInputStream, InputStream}

import com.fasterxml.jackson.databind.ObjectMapper
import com.wix.hive.client.http.HttpMethod.HttpMethod
import com.wix.hive.client.http.{AsyncHttpClient, HttpMethod, NamedParameters}
import com.wix.hive.commands.HiveCommand
import com.wix.hive.matchers.HiveMatchers
import com.wix.hive.model.{HiveClientException, WixAPIErrorException}
import org.specs2.concurrent.ExecutionEnv
import org.specs2.mock.Mockito
import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.specification.Scope

import scala.concurrent.Future

class HiveClientTest extends SpecificationWithJUnit with Mockito with HiveMatchers {

  class Context extends Scope {
    val httpClient = mock[AsyncHttpClient]

    implicit val executionEnv = ExecutionEnv.fromGlobalExecutionContext

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
      body = be_===(commandBody)))

    def givenAHttpClientReturnsAResponse = httpClient.request(any) returns Future.successful {
        new ByteArrayInputStream(new ObjectMapper().writeValueAsBytes(TestCommandResponse())).asInstanceOf[InputStream]
    }

    def givenAHttpClientFailsWith(anException: Throwable) = httpClient.request(any) returns Future.failed(anException)
  }

  "HiveClient" should {

    "wrap unhandled exceptions thrown from AsyncHttpClient" in new Context {
      givenAHttpClientFailsWith(new RuntimeException("an unhandled exception."))

      client.execute(instance, TestCommand()) must throwA[HiveClientException]((".*an unhandled exception.*")).await
    }

    "pass-through WixAPIErrorException thrown from AsyncHttpClient" in new Context {
      private val anException = new WixAPIErrorException(400, Some("bad request"), None)
      givenAHttpClientFailsWith(anException)

      client.execute(instance, TestCommand()) must throwAn(anException).await
    }

    "execute a command with correct parameters and return a response" in new Context {
      givenAHttpClientReturnsAResponse

      client.execute(instance, TestCommand()) must be_==(TestCommandResponse()).await

      verifyOneCallWithCorrectParams
    }

    "execute a command via executeForInstance with correct parameters and return a response" in new Context {
      givenAHttpClientReturnsAResponse

      client.executeForInstance(instance).apply(TestCommand())

      verifyOneCallWithCorrectParams
    }
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

}
  case class TestCommandResponse()

