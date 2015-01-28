package com.wix.hive.matchers

import java.util.UUID

import com.wix.hive.client.HiveClient
import com.wix.hive.client.http.HttpMethod._
import com.wix.hive.client.http.{HttpRequestData, NamedParameters}
import org.joda.time.DateTime
import org.specs2.matcher._

trait HiveMatchers extends Matchers with MustThrownExpectations {
  val appId = UUID.randomUUID().toString
  val instanceId = UUID.randomUUID().toString
  val timestamp = new DateTime(2014, 2, 11, 1, 2)


  implicit def type2Matcher[T](x: T): BeTypedEqualTo[T] = be_===(x)

  def anything[T] = AlwaysMatcher[T]()

  def httpRequestDataWith(method: Matcher[HttpMethod] = be_===(GET),
                          url: Matcher[String] = be(empty),
                          query: Matcher[NamedParameters] = beEmpty,
                          headers: Matcher[NamedParameters] = beEmpty,
                          body: Matcher[Option[AnyRef]] = beNone
                           ): Matcher[HttpRequestData] = {
    method ^^ {(_: HttpRequestData).method aka "method"} and
      url ^^ {(_: HttpRequestData).url aka "url"} and
      query ^^ {(_: HttpRequestData).queryString aka "query"} and
      headers ^^ {(_: HttpRequestData).headers aka "headers"} and
      body ^^ {(_: HttpRequestData).body aka "body"}
  }

  def headersFor(commandHeaders: NamedParameters, client: HiveClient, instanceId: String): Matcher[NamedParameters] = {
    havePairs(commandHeaders.toSeq: _*) and
      havePairs("x-wix-instance-id" -> instanceId,
        "x-wix-application-id" -> client.appId) and
      haveKey("x-wix-signature") and
      startWith("Hive Scala v") ^^ {(_: NamedParameters)("User-Agent") aka "User-Agent"} and
      almostNow ^^ {(_: NamedParameters)("x-wix-timestamp") aka "x-wix-timestamp"}
  }

  val s: (String => Boolean) = (x: String) => true

  def almostNow: Matcher[String] = new Matcher[String] {
    override def apply[S <: String](t: Expectable[S]): MatchResult[S] = {
      val res = new DateTime(t.value).getMillis must beCloseTo(new DateTime().getMillis, 2000)
      result(res, t)
    }
  }
}

object HiveMatchers extends HiveMatchers