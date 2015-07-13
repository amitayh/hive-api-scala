package com.wix.hive.server.webhooks

import com.wix.hive.client.http.HttpRequestData
import com.wix.hive.security.HiveRequestSigner
import com.wix.hive.server.webhooks.exceptions.InvalidSignatureException

import scala.util.{Failure, Success, Try}

/**
 * User: maximn
 * Date: 12/1/14
 */
class WebhookSignatureVerification(secret: String) {
  private lazy val signer = new HiveRequestSigner(secret)

  def verify(req: HttpRequestData): Try[HttpRequestData] = {
    tryHeader(req, "x-wix-signature") flatMap { sig =>
      val reqForSigning = req.copy(url = "")
      val calculatedSignature = signer.getSignature(reqForSigning)
      sig match {
        case `calculatedSignature` => Success(req)
        case badSignature => Failure(new InvalidSignatureException(req, badSignature, calculatedSignature))
      }
    }
  }
}
