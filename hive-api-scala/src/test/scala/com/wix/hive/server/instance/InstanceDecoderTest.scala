package com.wix.hive.server.instance

import com.wix.hive.drivers.InstanceEncoderSupport
import org.joda.time.{DateTime, DateTimeZone}
import org.specs2.matcher.MatcherMacros
import org.specs2.mutable.SpecificationWithJUnit

import scala.language.experimental.macros

/**
 * User: maximn
 * Date: 7/13/15
 */
class InstanceDecoderTest
  extends SpecificationWithJUnit with MatcherMacros {

  class ctx extends InstanceDecoderScope with InstanceEncoderSupport {

    val instance = WixInstance(
      instanceId = instanceId,
      signedAt = new DateTime(signDate).withZone(DateTimeZone.UTC),
      userId = Some(uid),
      permissions = Set(permission),
      userIp = ipAndPort,
      premiumPackageId = Some(premiumPackage),
      demoMode = false,
      ownerId = ownerId)

    val signedInstance = signAndEncodeInstance(instance, key)

  }

  "decode" should {
    "resolve WixInstance" in new ctx {
      decoder.decode(signedInstance) must beSuccessfulTry(
        matchA[WixInstance]
          .instanceId(instanceId)
          .signedAt(new DateTime(signDate).toDateTime(DateTimeZone.UTC))
          .userId(beSome(uid))
          .permissions(contain(exactly(permission)))
          .premiumPackageId(beSome(premiumPackage))
          .userIp(ipAndPort)
          .demoMode(false)
          .ownerId(ownerId))
    }

    "explode on bad signature" in new ctx {
      new InstanceDecoder("invalid_key").decode(signedInstance) must
        beFailedTry.withThrowable[InvalidInstanceSignature]
    }

    "explode on expired instance" in new ctx {
      givenClock(new DateTime(signDate).plusMinutes(6))
      new InstanceDecoder(key, timeProvider = timeProvider)
          .decode(signedInstance) must beFailedTry.withThrowable[ExpiredInstanceException]
    }

    "handle malformed payload - no separator" in new ctx {
      decoder.decode("daskdhuiywwa") must beFailedTry.withThrowable[MalformedInstance]
    }

    "handle malformed payload - no signature" in new ctx {
      decoder.decode(".") must beFailedTry.withThrowable[MalformedInstance]
    }
  }
}
