package com.wix.hive.commands.labels

import com.wix.hive.client.http.HttpMethod
import com.wix.hive.commands.common.PageSizes
import com.wix.hive.matchers.HiveMatchers
import org.specs2.mutable._
import org.specs2.specification.Scope

/**
 * Created by karenc on 2/1/15.
 */
class GetLabelsTest extends SpecificationWithJUnit with HiveMatchers {

  "createHttpRequestData" should {

    "create HttpRequestData with query parameters" in new Context {
      command.createHttpRequestData must httpRequestDataWith(
        method = be_===(HttpMethod.GET),
        url = be_===("/labels"),
        query = havePairs(
          "cursor" -> "/cursor/",
          "pageSize" -> "25") and haveSize(2))
    }

    "create HttpRequestData with no parameters" in new Context {
      val cmd = command.copy(None, None)

      val httpData = cmd.createHttpRequestData

      httpData.queryString must beEmpty
    }
  }


  class Context extends Scope {
    val command = GetLabels(Some("/cursor/"), Some(PageSizes.`25`))
  }
}
