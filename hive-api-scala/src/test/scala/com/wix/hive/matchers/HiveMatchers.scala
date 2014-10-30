package com.wix.hive.matchers

import com.wix.hive.client.HiveClient
import com.wix.hive.client.http.HttpMethod._
import com.wix.hive.client.http.{NamedParameters, HttpRequestData}
import org.joda.time.{Seconds, DateTime}
import org.specs2.matcher.{MustExpectations, Matchers, Matcher, AlwaysMatcher}

trait HiveMatchers extends Matchers with MustExpectations {

  def anything[T] = AlwaysMatcher[T]()

  def httpRequestDataWith(method: Matcher[HttpMethod] = be_===(GET),
                          url: Matcher[String] = be(empty),
                          query: Matcher[NamedParameters] = beEmpty,
                          headers: Matcher[NamedParameters] = beEmpty,
                          body: Matcher[Option[AnyRef]] = beNone
                         ): Matcher[HttpRequestData] = {
      method ^^ { (_: HttpRequestData).method aka "method" } and
      url ^^ { (_: HttpRequestData).url aka "url" } and
      query ^^ { (_: HttpRequestData).queryString aka "query" } and
      headers ^^ { (_: HttpRequestData).headers aka "headers" } and
      body ^^ { (_: HttpRequestData).body aka "body" }
  }

  def headersFor(commandHeaders: NamedParameters, client: HiveClient) : Matcher[NamedParameters] = {
    havePairs(commandHeaders.toSeq :_*) and
    havePairs("x-wix-instance-id" -> client.instanceId,
      "x-wix-application-id" -> client.appId) and
    haveKey("x-wix-signature") and
    startWith("Hive Scala v") ^^ {(_: NamedParameters)("User-Agent") aka "User-Agent"} and
    almostNow ^^ {(_: NamedParameters)("x-wix-timestamp") aka "x-wix-timestamp"}
  }

  def almostNow : Matcher[String] = (x:String) => {
    new DateTime(x).getMillis must beCloseTo(new DateTime().getMillis, 2000)
  }
}

object HiveMatchers extends HiveMatchers