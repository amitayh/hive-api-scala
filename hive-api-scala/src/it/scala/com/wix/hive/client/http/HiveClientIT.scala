package com.wix.hive.client.http

import java.util.UUID

import com.github.tomakehurst.wiremock.client.WireMock.{equalTo => equalToString, _}
import com.github.tomakehurst.wiremock.verification.LoggedRequest
import com.wix.hive.client.HiveClient
import com.wix.hive.client.http.HttpMethod.HttpMethod
import com.wix.hive.commands.HiveCommand
import com.wix.hive.infrastructure.WiremockEnvironment
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import org.specs2.matcher.Matcher
import org.specs2.mock.Mockito
import org.specs2.mutable.{Before, SpecificationWithJUnit}
import org.specs2.time.NoTimeConversions
import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, containing, equalTo, equalToJson, givenThat, matching => regexMatching, urlMatching, verify => wiremockVerify}

class HiveClientIT extends SpecificationWithJUnit with NoTimeConversions with Mockito {
  sequential

  step {
    WiremockEnvironment.start()
  }

  trait ctx extends Before {
    def before = WiremockEnvironment.resetMocks()

    val appId = "d6f07c22-cabc-4438-af94-6e31535dc8ae"
    val appSecret = "081b6070-6983-4d97-94b5-7cd0d6edfc9b"
    val appInstanceId = "081b6070-6983-4d97-94b5-7cd0d6edfc9b"

    val baseUrl = s"http://localhost:${WiremockEnvironment.serverPort}"
    val relativeTestUrl = "/v1/test?version=1.0.0"

    val client = new HiveClient(appId, appSecret, baseUrl)

    def aLoggedRequest = findAll(getRequestedFor(urlEqualTo(relativeTestUrl))).get(0)
    def expectARequest = givenThat(get(urlEqualTo(relativeTestUrl)).willReturn(aResponse().withBody("""{"data":"some info"}""")))

    def matchHeader(key: String, value: Matcher[String]) = value ^^ { (_:LoggedRequest).header(key).firstValue aka s"header: $key" }
    def validTimestamp: Matcher[String] = (beLessThan(DateTime.now.getMillis) and
      beGreaterThan(DateTime.now.minusMinutes(1).getMillis)) ^^ { ISODateTimeFormat.dateTime.parseDateTime((_:String)).getMillis }
  }

  "HiveClient.execute" should {

    "should execute a command and return a response" in new ctx {
      expectARequest

      client.execute(appInstanceId, DummyCommand) must be_==(DummyResponse("some info")).await

      aLoggedRequest must matchHeader("x-wix-application-id", ===(appId)) and
        matchHeader("x-wix-instance-id", ===(appInstanceId)) and
        matchHeader("x-wix-timestamp", validTimestamp)
    }
  }

  "HiveClient.executeForInstance" should {
    "should execute a command and return a response" in new ctx {
      expectARequest

      client.executeForInstance(appInstanceId).apply(DummyCommand) must be_==(DummyResponse("some info")).await

      aLoggedRequest must matchHeader("x-wix-application-id", ===(appId)) and
        matchHeader("x-wix-instance-id", ===(appInstanceId)) and
        matchHeader("x-wix-timestamp", validTimestamp)
    }
  }

}

case object DummyCommand extends HiveCommand[DummyResponse] {
  override def url: String = "/test"
  override def method: HttpMethod = HttpMethod.GET
}

case class DummyResponse(data: String)
