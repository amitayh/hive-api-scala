package com.wix.hive.commands.contacts

import com.wix.hive.client.http.HttpMethod
import com.wix.hive.matchers.HiveMatchers
import org.specs2.mutable.SpecificationWithJUnit

class UpdateAddressTest extends SpecificationWithJUnit with HiveMatchers {
  "createHttpRequestData" should {
    "work with parameters" in new Context {
      cmd.createHttpRequestData must httpRequestDataWith(
        method = be_===(HttpMethod.PUT),
        url = be_===(s"/contacts/$contactId/address/$addressId"),
        query = havePair("modifiedAt", modifiedAt.toString),
        body = beSome(address)
      )
    }
  }

  class Context extends ContextForModification {
    val addressId = "e2f5559b-74a0-4916-8e11-3c2da4b6bfe1"
    val address = AddressDTO("tag-address")

    val cmd = UpdateAddress(contactId, modifiedAt, addressId, address)
  }
}