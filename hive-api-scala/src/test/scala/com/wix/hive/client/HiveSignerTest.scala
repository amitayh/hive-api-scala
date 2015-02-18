package com.wix.hive.client

import java.util.concurrent.atomic.AtomicInteger

import com.wix.hive.client.http.{HttpMethod, HttpRequestData}
import com.wix.hive.drivers.SigningTestSupport
import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.specification.Scope

class HiveSignerTest extends SpecificationWithJUnit {

  class ctx extends Scope with SigningTestSupport {
    val signer = new HiveSigner(key)
  }

  "Signer" should {

    "Sign HttpRequestData no body" in new ctx {
      signer.getSignature(dataWithNoBody) must beEqualTo(dataWithNoBodySignature)
    }

    "Sign HttpRequestData with body" in new ctx {
      signer.getSignature(dataWithBody) must beEqualTo(dataWithBodySignature)
    }

    "Sign HttpRequestData with non English characters" in new ctx {
      signer.getSignature(dataWithNonEnglishBody) must beEqualTo(dataWithNonEnglishBodySignature)
    }


    "properly filter-out generic wix headers" in new ctx {
      val data = dataWithBody.copy(headers = dataWithBody.headers + ("X-Wix-Country-Code" -> "NL"))

      signer.getSignature(data) must beEqualTo(dataWithBodySignature)
    }
  }

  class StringGeneratorCtx extends ctx {
    val signature = "random-signature"
    val httpDataWithSignature = HttpRequestData(HttpMethod.GET, "/", Map("signature" -> signature), Map("X-Wix-Signature" -> signature))
  }

  "create string to sign" should {
    "exclude wix signature from header and query param" in new StringGeneratorCtx {
      signer.generateStringToSign(httpDataWithSignature) must not(contain(signature))
    }
  }

  trait threadsCtx extends ctx {
    val times = 20
  }


  "Thread safety" should {
    "be observed" in new threadsCtx {
      (1 until times).par.map { _ =>
        signer.getSignature(dataWithBody) must beEqualTo(dataWithBodySignature)
      }
    }
  }
}
