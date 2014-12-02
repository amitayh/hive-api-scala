package com.wix.hive.client

import com.wix.hive.drivers.SigningTestSupport
import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.specification.Scope

class HiveSignerTest extends SpecificationWithJUnit {

  class Context extends Scope
  with SigningTestSupport{
    val signer = new HiveSigner(key)
  }

  "Signer" should {

    "Sign HttpRequestData no body" in new Context {
      signer.getSignature(dataWithNoBody) must beEqualTo(dataWithNoBodySignature)
    }

    "Sign HttpRequestData with body" in new Context {
      signer.getSignature(dataWithBody) must beEqualTo(dataWithBodySignature)
    }
  }
}
