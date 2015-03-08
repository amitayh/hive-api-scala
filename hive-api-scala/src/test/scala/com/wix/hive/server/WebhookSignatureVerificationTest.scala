package com.wix.hive.server

import com.wix.hive.client.http.HttpRequestData
import com.wix.hive.drivers.SigningTestSupport
import com.wix.hive.server.webhooks.WebhookSignatureVerification
import com.wix.hive.server.webhooks.exceptions.{InvalidSignatureException, MissingHeaderException}
import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.specification.Scope

/**
 * User: maximn
 * Date: 11/30/14
 */
class WebhookSignatureVerificationTest extends SpecificationWithJUnit {

  trait ctx extends Scope with SigningTestSupport {
    val verifier = new WebhookSignatureVerification(key)
  }

  "verify" should {

    "be true for value req" in new ctx {
      val signedRequest = dataWithBody.copy(headers = dataWithBody.headers + ("x-wix-signature" -> dataWithBodyNoUrlSignature))

      verifier.verify(signedRequest) must beSuccessfulTry(signedRequest)
    }

    "be false if no signature" in new ctx {
      verifier.verify(dataWithBody) must beFailedTry[HttpRequestData].withThrowable[MissingHeaderException]
    }

    "be false if wrong signature" in new ctx {
      val badlySignedRequest = dataWithBody.copy(headers = dataWithBody.headers + ("x-wix-signature" -> "xxxx"))

      verifier.verify(badlySignedRequest) must beFailedTry[HttpRequestData].withThrowable[InvalidSignatureException]
    }
  }
}
