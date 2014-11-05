package com.wix.hive.commands.contacts

import com.wix.hive.client.http.HttpMethod
import com.wix.hive.matchers.HiveMatchers
import org.joda.time.DateTime
import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.specification.Scope

class AddAddressTest extends SpecificationWithJUnit with HiveMatchers {
  "createHttpRequestData" should {
    "work with parameters" in new Context {
      cmd.createHttpRequestData must httpRequestDataWith(
        method = be_===(HttpMethod.POST),
        url = contain(contactId) and contain("address"),
        query = havePair("modifiedAt", modifiedAt.toString),
        body = beSome(be_==(address))
      )
    }
  }

  class Context extends ContextForModification {
    val address = AddressDTO("tag")

    val cmd = AddAddress(contactId, modifiedAt, address)
  }

}