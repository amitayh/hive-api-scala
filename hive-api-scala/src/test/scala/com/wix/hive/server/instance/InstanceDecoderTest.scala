package com.wix.hive.server.instance

import com.wix.hive.drivers.InstanceEncoderSupport
import org.joda.time.{DateTime, DateTimeZone}
import org.specs2.matcher.{Matcher, MatcherMacros}
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

    val decoderWithSimpleKey = new InstanceDecoder("11111111-1111-1111-1111-111111111111")
    val instanceWihtoutSignetAt = "7_MQI28JN_XAAwU_6QPgENbpJzFlrJMcgq6n-rGSqac.eyJpbnN0YW5jZUlkIjoiNDQwYmNjZWYtNTFjNi00NTY3LTliZmUtZGUxYWZiMzk5MTg4IiwidWlkIjoiNTE1MTJjYjktZDUzOC00ZmJiLWFmY2ItNmY2MTc1NmU3N2Q5IiwicGVybWlzc2lvbnMiOiJPV05FUiIsInZlbmRvclByb2R1Y3RJZCI6IlByZW1pdW0xIiwiZGVtb01vZGUiOmZhbHNlLCJvcmlnaW5JbnN0YW5jZUlkIjoiYzdhM2M4NjktN2Y5Ny00NjFjLWEwZGUtODcwNTViZGY4ZTJmIiwiYWlkIjoiYzEwYzYxNTctYWZjYS00MmFhLWE0ZWYtMjNkNTM2ZDEzODhhIiwic2l0ZU93bmVySWQiOiJmNjIyYzgwZi04NGY2LTRjODMtOTZiYS1jOGYyZTk4ZDZlYjUifQ"


    def beEmptySet: Matcher[Set[String]] = beEmpty
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

    "resolve no permissions" in new ctx {
      val noPermissionsInstance = signAndEncodeInstance(instance.copy(permissions = Set.empty), key)

      decoder.decode(noPermissionsInstance) must beSuccessfulTry(matchA[WixInstance].permissions(beEmptySet))
    }

    "explode on bad signature" in new ctx {
      new InstanceDecoder("invalid_key").decode(signedInstance) must
        beFailedTry.withThrowable[InvalidInstanceSignature]
    }

    "explode on expired instance" in new ctx {
      givenClock(new DateTime(signDate).plusMinutes(61))
      new InstanceDecoder(key, timeProvider = timeProvider)
        .decode(signedInstance) must beFailedTry.withThrowable[ExpiredInstanceException]
    }

    "handle malformed payload - no separator" in new ctx {
      decoder.decode("daskdhuiywwa") must beFailedTry.withThrowable[MalformedInstance]
    }

    "handle malformed payload - no signature" in new ctx {
      decoder.decode(".") must beFailedTry.withThrowable[MalformedInstance]
    }

    "meaningful error when no signedAt field" in new ctx {
      decoderWithSimpleKey.decode(instanceWihtoutSignetAt) must beFailedTry.withThrowable[ExpiredInstanceException]
    }
  }
}
