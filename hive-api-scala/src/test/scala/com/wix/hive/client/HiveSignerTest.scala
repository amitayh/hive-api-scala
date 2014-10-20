package com.wix.hive.client

import com.wix.hive.client.http.{HttpMethod, HttpRequestData}
import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.specification.Scope

class HiveSignerTest extends SpecificationWithJUnit {
  "Signer" should {

    "Sign HttpRequestData no body" in new Context {
      val data = HttpRequestData(HttpMethod.GET,
        "/v1/contacts/2c4436a4-13be-4581-99b2-69ed2781c7c9",
        queryString = Map("version" -> "1.0.0"),
        headers = Map("X-Wix-Instance-Id" -> "13929ab6-4b6e-fd49-fb52-17c9c7e55794",
          "X-Wix-Application-Id" -> "13929a86-9df0-8706-0f53-3a0cae292a82",
          "X-Wix-Timestamp" -> "2014-10-08T10:20:52.320+03:00"))


      signer.getSignature(key, data) must beEqualTo("2_XEZFPsKxHGa3nXUbgizyYvxYLm6legumuzA5GUYkk")
    }

    "Sign HttpRequestData with body" in new Context {
      val data = HttpRequestData(HttpMethod.POST,
        "/v1/contacts",
        queryString = Map("version" -> "1.0.0"),
        headers = Map("X-Wix-Instance-Id" -> "13929ab6-4b6e-fd49-fb52-17c9c7e55794",
          "X-Wix-Application-Id" -> "13929a86-9df0-8706-0f53-3a0cae292a82",
          "X-Wix-Timestamp" -> "2014-10-08T10:20:51.036+03:00"),
        body = Some( """{"name":{"first":"Wix","last":"Cool"},"company":{},"emails":[{"email":"alext@wix.com","tag":"work"}],"phones":[{"phone":"123456789","tag":"work"}],"addresses":[],"urls":[],"dates":[],"notes":[],"custom":[]}"""))

      signer.getSignature(key, data) must beEqualTo("t_n4yQWIgQeTbCP9oOCKUa0NZpyiNX1nsfxHS3vjiH0")
    }
  }

  class Context extends Scope {
    val signer = HiveSigner
    val key = "e5f5250a-dbd0-42a1-baf9-c61ea20c401b"
  }

}