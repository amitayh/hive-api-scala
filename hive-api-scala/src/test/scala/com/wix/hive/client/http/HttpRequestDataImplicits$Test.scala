package com.wix.hive.client.http

import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.specification.Scope
import com.wix.hive.client.http.HttpRequestDataImplicits._

class HttpRequestDataImplicitsTest extends SpecificationWithJUnit {

  class Context extends Scope {
    val httpData = HttpRequestData(HttpMethod.GET, "url")

    val data = "some data"

    val dataWithStringBody = httpData.copy(body = Some(data))
    val expectedJson = s"""{"data":"$data"}"""

    val dummyObject = new Dummy(data)
    val dataWithAnyRefBody = httpData.copy(body = Some(dummyObject))

    val dataWithNoneStringBody = httpData.copy(body = None)
  }

  case class Dummy(data: String)

  "HttpRequestDataStringify" should {

    "return string as itself" in new Context {
      dataWithStringBody.bodyAsString must beEqualTo(data)
    }

    "Convert AnyRef string representation of the object as JSON" in new Context {
      dataWithAnyRefBody.bodyAsString must beEqualTo(expectedJson)
    }

    "Convert None to empty String" in new Context {
      (dataWithNoneStringBody.bodyAsString must be).empty
    }
  }
}


