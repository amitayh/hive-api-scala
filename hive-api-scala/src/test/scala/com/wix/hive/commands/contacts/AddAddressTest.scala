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
        url = contain(contactId),
        query = havePair("modifiedAt", modifiedAt.toString),
        body = beSome(be_==(address))
      )
    }
  }

  class Context extends Scope {
    val contactId = "0a6f6d66-e27c-48ce-9ad0-1fa30977954a"
    val modifiedAt = new DateTime(2010, 3, 2, 1, 2)
    val address = AddressDTO("tag")

    val cmd = AddAddress(contactId, modifiedAt, address)
  }

}