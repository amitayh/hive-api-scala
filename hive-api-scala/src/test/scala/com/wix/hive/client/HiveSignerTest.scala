package com.wix.hive.client

import com.wix.hive.client.http.{HttpMethod, HttpRequestData}
import com.wix.hive.drivers.SigningTestSupport
import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.specification.Scope

class HiveSignerTest extends SpecificationWithJUnit {

  class Context extends Scope with SigningTestSupport {
    val signer = new HiveSigner(key)
  }

  "Signer" should {

    "Sign HttpRequestData no body" in new Context {
      signer.getSignature(dataWithNoBody) must beEqualTo(dataWithNoBodySignature)
    }

    "Sign HttpRequestData with body" in new Context {
      signer.getSignature(dataWithBody) must beEqualTo(dataWithBodySignature)
    }

    "properly filter-out generic wix headers" in new Context {
      val data = dataWithBody.copy(headers = dataWithBody.headers + ("X-Wix-Country-Code" -> "NL"))

      signer.getSignature(data) must beEqualTo(dataWithBodySignature)
    }
  }

  class StringGeneratorContext extends Context {
    val signature = "random-signature"
    val httpDataWithSignature = HttpRequestData(HttpMethod.GET, "/", Map("signature" -> signature), Map("X-Wix-Signature" -> signature))
  }

  "create string to sign" should {
    "exclude wix signature from header and query param" in new StringGeneratorContext {
      signer.generateStringToSign(httpDataWithSignature) must not(contain(signature))
    }
  }
}
