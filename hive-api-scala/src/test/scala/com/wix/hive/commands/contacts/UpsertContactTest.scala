package com.wix.hive.commands.contacts

import com.wix.hive.client.http.{NamedParameters, HttpMethod}
import com.wix.hive.commands.{UpsertContact, GetContacts}
import com.wix.hive.commands.common.PageSizes
import com.wix.hive.matchers.HiveMatchers
import org.specs2.mutable.{SpecificationWithJUnit, Specification}
import org.specs2.specification.Scope

class UpsertContactTest extends SpecificationWithJUnit with HiveMatchers {

  "createHttpRequestData" should {

    "create HttpRequestData with declared parameters" in new Context {
      command.createHttpRequestData must httpRequestDataWith(
        method = be_===(HttpMethod.PUT),
        url = be_===("/contacts"),
        body = beSome(be_==(Map(
          "phone" -> "972-54-5551234",
          "email" -> "email@wix.com")))
      )
    }

    "create HttpRequestData with default parameters" in new Context {
      commandWithDefaults.createHttpRequestData must httpRequestDataWith(
        method = be_===(HttpMethod.PUT),
        url = be_===("/contacts"),
        body = beNone)
    }


    class Context extends Scope {
      val commandWithDefaults = UpsertContact()
      val command = UpsertContact(Some("972-54-5551234"), Some("email@wix.com"))
    }
  }
}