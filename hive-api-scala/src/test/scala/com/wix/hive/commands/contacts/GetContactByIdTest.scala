package com.wix.hive.commands.contacts

import com.wix.hive.client.http.HttpMethod
import com.wix.hive.matchers.HiveMatchers
import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.specification.Scope

class GetContactByIdTest extends SpecificationWithJUnit with HiveMatchers {

    "createHttpRequestData" should {

        "create correct HttpRequestData" in new Context {
            command.createHttpRequestData must httpRequestDataWith(
            method = be_===(HttpMethod.GET),
            url = be_===(s"/contacts/$id")
            )
        }
    }


    class Context extends Scope {
        val id = "124132432"
        val command = GetContactById(id)
    }
}