package com.wix.hive.server.instance

import com.wix.hive.security.HiveSigner

import scala.util.{Failure, Try}
import org.apache.commons.net.util.Base64


/**
 * User: maximn
 * Date: 12/7/14
 */
class InstanceDecoder(key: String) {
  private val signer = new HiveSigner(key)
  private val deserializer = new InstanceDeserializer

  def decode(payload: String): Try[WixInstance] = {
    for {
      (signature, encodedJson) <- extractPayload(payload)
      calculatedSignature = signer.signString(encodedJson)
      _ <- validateSignature(signature, calculatedSignature)(encodedJson)
      base64 = new Base64
      decodedInstance = base64.decode(encodedJson)
      wixInstance <- Try {deserializer.deserialize(decodedInstance)}
    } yield wixInstance
  }

  private def validateSignature(signature: String, calculatedSignature: String)(encodedJson: String) =
    Try {if (signature != calculatedSignature) throw new InvalidInstanceSignature(signature, encodedJson)}

  private def extractPayload(instance: String): Try[(String, String)] = {
    Try {
      val separatorIndex = instance.indexOf(".")
      require(separatorIndex > 0)
      val signature = instance.substring(0, separatorIndex)
      val encodedJson = instance.substring(separatorIndex + 1)
      (signature, encodedJson)
    } recoverWith {
      case ex@(_: IllegalArgumentException | _: IndexOutOfBoundsException) =>
        Failure(new MalformedInstance(instance, ex))
    }
  }
}

class InvalidInstanceSignature(signature: String, json: String) extends RuntimeException(s"Invalid signature: '$signature' for instance: '$json'")

class MalformedInstance(payload: String, cause: Throwable) extends RuntimeException(s"Malformed instance : $payload", cause)