package com.wix.hive.server.instance

import org.specs2.matcher.MatcherMacros
import org.specs2.mutable.Specification

import scala.language.experimental.macros

/**
 * User: maximn
 * Date: 7/13/15
 */
class InstanceDeserializerTest extends Specification with MatcherMacros {

  class ctx extends InstanceDecoderScope {
    val deserializer = new InstanceDeserializer
  }

  "WixInstance deserialization" should {
    "handle permissions `null`" in new ctx {
      val instance = generateInstance(permissions = "null").getBytes
      deserializer.deserialize(instance) must matchA[WixInstance].permissions(empty)
    }

    "handle userId is `null`" in new ctx {
      val instance = generateInstance(userId = None).getBytes
      deserializer.deserialize(instance) must matchA[WixInstance].userId(beNone)
    }

    "handle premiumPackage is `null`" in new ctx {
      val instance = generateInstance(premiumPackageId = None).getBytes
      deserializer.deserialize(instance) must matchA[WixInstance].premiumPackageId(beNone)
    }
  }

}
