package com.wix.hive.server.instance

import com.wix.hive.infrastructure.{SystemTimeProvider, TimeProvider}
import com.wix.hive.security.HiveSigner
import org.apache.commons.net.util.Base64
import org.joda.time.{DateTime, Duration}

import scala.util.{Failure, Try}


/**
 * User: maximn
 * Date: 12/7/14
 */

/**
 * Decodes the signed WixInstance
 * Validates that the signature is right and that timestamp is within the allowed duration
 *
 * @param considerExpiredAfter This is the allowed duration from timestamp until now
 */
class InstanceDecoder(key: String,
                      considerExpiredAfter: Duration = InstanceDecoder.DefaultExpirationDuration,
                      timeProvider: TimeProvider = new SystemTimeProvider) {
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
      _ <- validateNonExpired(wixInstance)
    } yield wixInstance
  }

  private def validateSignature(signature: String, calculatedSignature: String)(encodedJson: String) =
    Try {if (signature != calculatedSignature) throw new InvalidInstanceSignature(signature, encodedJson)}

  private def validateNonExpired(wixInstance: WixInstance) = {
    val now = timeProvider.now
    Try {
      val instanceSignedAt = Option(wixInstance.signedAt).getOrElse(throw new ExpiredInstanceException(now, null))
      if (now.getMillis - instanceSignedAt.getMillis > considerExpiredAfter.getMillis) throw new ExpiredInstanceException(now, instanceSignedAt)
    }
  }

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

object InstanceDecoder {
  val DefaultExpirationDuration = Duration.standardHours(1)
}

class InvalidInstanceSignature(signature: String, json: String) extends RuntimeException(s"Invalid signature: '$signature' for instance: '$json'")

class MalformedInstance(payload: String, cause: Throwable) extends RuntimeException(s"Malformed instance : $payload", cause)

class ExpiredInstanceException(now: DateTime, instanceSignedAt: DateTime) extends RuntimeException(s"The instance is expired. Was signed at $instanceSignedAt but the time now is $now.")