package com.wix.hive.client

import com.wix.hive.client.http.HttpMethod.HttpMethod
import com.wix.hive.client.http.{AsyncHttpClient, HttpMethod, NamedParameters}
import com.wix.hive.commands.HiveBaseCommand
import com.wix.hive.matchers.HiveMatchers
import org.specs2.mock.Mockito
import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.specification.Scope


class HiveClientTest extends SpecificationWithJUnit with Mockito with HiveMatchers {

  class Context extends Scope {
    val httpClient = mock[AsyncHttpClient]

    val id = "appId"
    val key = "appKey"
    val instance = "websiteInstance"

    val client = new HiveClient(id, key, instance, httpClient)
  }


  "execute" should {

    "call the http client with the correct parameters" in new Context {
      client.execute(TestCommand())

      there was one(httpClient).request(httpRequestDataWith(
        method = be_===(HttpMethod.GET),
        url = be_==(client.baseUrl + client.versionForUrl + commandUrl + commandParams),
        query = havePairs(commandQuery.toSeq :_*),
        headers = headersFor(commandHeaders, client),
        body = be_===(commandBody)))(any)
    }
  }


  "apply" should {
    "load with configuration from conf file" >> {
      val client = HiveClient("instance")

      client.appId must be_===("application-id")
      client.baseUrl must be_==("http://base-url.com/something")
    }
  }


  // move into context
  val commandUrl = "/tst"
  val commandParams = "/param"
  val commandQuery = Map("q" -> "query")
  val commandHeaders = Map("h" -> "header")
  val commandBody = Some(AnyRef)

  case class TestCommand() extends HiveBaseCommand[TestCommandResponse] {
    override def url: String = commandUrl

    override def urlParams: String = commandParams

    override def query: NamedParameters = commandQuery

    override def headers: NamedParameters = commandHeaders

    override def body: Option[AnyRef] = commandBody

    override def method: HttpMethod = HttpMethod.GET
  }

  case class TestCommandResponse()
}

