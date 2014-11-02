package com.wix.hive.commands.contacts

import com.wix.hive.commands.GetContacts
import com.wix.hive.model.PageSizes
import org.specs2.mutable._
import org.specs2.specification.Scope

class GetContactsTest extends SpecificationWithJUnit {

  "createHttpRequestData" should {

    "create HttpRequestData with query parameters" in new Context {
      val httpData = command.createHttpRequestData

      httpData.queryString must havePairs(
        "tag" -> "tag1,tag2",
        "email" -> "email1@google.com",
        "phone" -> "+972-54-5557726",
        "firstName" -> "Max",
        "lastName" -> "Nov",
        "cursor" -> "/cursor/",
        "pageSize" -> "25")


      httpData.queryString must haveSize(7)
    }

    "create HttpRequestData with no parameters" in new Context {
      val cmd = command.copy(Nil, None, None, None, None, None, None)

      val httpData = cmd.createHttpRequestData

      httpData.queryString must beEmpty
    }
  }


  class Context extends Scope {
    val command = GetContacts(Seq("tag1","tag2"), Some("email1@google.com"), Some("+972-54-5557726"), Some("Max"), Some("Nov"), Some("/cursor/"), Some(PageSizes.`25`))
  }

}
