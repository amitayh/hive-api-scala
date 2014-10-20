package com.wix.hive.client

import com.wix.hive.client.http.HttpMethod.HttpMethod
import com.wix.hive.client.http.http.NamedParameters
import com.wix.hive.client.http.{HttpMethod, HttpRequestData, AsyncHttpClient}
import com.wix.hive.commands.{HiveBaseCommandResponse, HiveBaseCommand}
import com.wix.hive.commands.contacts.GetContacts
import com.wix.hive.matchers.HiveMatchers
import org.specs2.matcher.{Matcher, AlwaysMatcher}
import org.specs2.mock.Mockito
import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.specification.Scope


class HiveClientTest extends SpecificationWithJUnit with Mockito with HiveMatchers{

  "execute" should {

    "call the http client with the correct parameters" in new Context {
      client.execute(TestCommand())

      there was one(httpClient).request(httpRequestDataWith(
        method = be_==(HttpMethod.GET),
        url = be_==(client.baseUrl + commandUrl),
        query = havePairs(commandQuery.toSeq :_*),
        headers = headersFor(commandHeaders, client),
        body = be_==(commandBody)))(any)
    }
  }

  class Context extends Scope {
    val httpClient = mock[AsyncHttpClient]

    val id = "appId"
    val key = "appKey"
    val instance = "websiteInstance"

    val client = new HiveClient(id, key, instance, httpClient)
  }


  val commandUrl = "/tst"
  val commandQuery = Map("q" -> "query")
  val commandHeaders = Map("h" -> "header")
  val commandBody = Some(AnyRef)

  case class TestCommand() extends HiveBaseCommand[TestCommandResponse] {
    override def url: String = commandUrl

    override def createHttpRequestData: HttpRequestData = HttpRequestData(method, url, commandQuery, commandHeaders, commandBody)

    override def method: HttpMethod = HttpMethod.GET
  }

  case class TestCommandResponse() extends HiveBaseCommandResponse
}