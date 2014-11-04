package com.wix.hive.commands.contacts

import com.wix.hive.client.http.HttpMethod
import com.wix.hive.commands.GetContacts
import com.wix.hive.commands.common.PageSizes
import com.wix.hive.matchers.HiveMatchers
import org.jboss.netty.handler.codec.http.multipart.HttpData
import org.specs2.mutable._
import org.specs2.specification.Scope

class GetContactsTest extends SpecificationWithJUnit with HiveMatchers {

  "createHttpRequestData" should {

    "create HttpRequestData with query parameters" in new Context {
      command.createHttpRequestData must httpRequestDataWith(
        method = be_===(HttpMethod.GET),
        url = be_===("/contacts"),
        query = havePairs(
          "tag" -> "tag1,tag2",
          "email" -> "email1@google.com",
          "phone" -> "+972-54-5557726",
          "firstName" -> "Max",
          "lastName" -> "Nov",
          "cursor" -> "/cursor/",
          "pageSize" -> "25") and haveSize(7))
    }

    "create HttpRequestData with no parameters" in new Context {
      val cmd = command.copy(Nil, None, None, None, None, None, None)

      val httpData = cmd.createHttpRequestData

      httpData.queryString must beEmpty
    }
  }


  class Context extends Scope {
    val command = GetContacts(Seq("tag1", "tag2"), Some("email1@google.com"), Some("+972-54-5557726"), Some("Max"), Some("Nov"), Some("/cursor/"), Some(PageSizes.`25`))
  }
}
