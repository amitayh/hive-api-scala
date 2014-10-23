package com.wix.hive.matchers

import com.wix.hive.client.HiveClient
import com.wix.hive.client.http.HttpMethod._
import com.wix.hive.client.http.{NamedParameters, HttpRequestData}
import org.joda.time.{Seconds, DateTime}
import org.specs2.matcher.{Matchers, Matcher, AlwaysMatcher}

trait HiveMatchers extends Matchers {

  def anything[T] = AlwaysMatcher[T]()

  def httpRequestDataWith(method: Matcher[HttpMethod] = anything,
                          url: Matcher[String] = anything,
                          query: Matcher[NamedParameters] = anything,
                          headers: Matcher[NamedParameters] = anything,
                          body: Matcher[Option[AnyRef]] = anything
                         ): Matcher[HttpRequestData] = {
      method ^^ { (_: HttpRequestData).method aka "method" } and
      url ^^ { (_: HttpRequestData).url aka "url" } and
      query ^^ { (_: HttpRequestData).queryString aka "query" } and
      headers ^^ { (_: HttpRequestData).headers aka "headers" } and
      body ^^ { (_: HttpRequestData).body aka "body" }
  }

  def headersFor(commandHeaders: NamedParameters, client: HiveClient) : Matcher[NamedParameters] = {
    havePairs(commandHeaders.toSeq :_*) and
    havePairs("x-wix-instance-id" -> client.instanceId, "x-wix-application-id" -> client.appId) and
    startWith("Hive Scala v") ^^ {(_: NamedParameters)("User-Agent") aka "User-Agent"} and
    almostNow ^^ {(_: NamedParameters)("x-wix-timestamp") aka "x-wix-timestamp"}
  }

  def almostNow : Matcher[String] = (x:String) => {
    val deviationSeconds = 2
    Math.abs(Seconds.secondsBetween(new DateTime(x), new DateTime()).getSeconds) < deviationSeconds
  }
}

object HiveMatchers extends HiveMatchers