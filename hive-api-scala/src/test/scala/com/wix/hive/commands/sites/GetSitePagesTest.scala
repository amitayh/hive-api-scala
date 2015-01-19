package com.wix.hive.commands.sites

import org.specs2.mutable.SpecificationWithJUnit
import com.wix.hive.matchers.HiveMatchers._
import com.wix.hive.client.http.HttpMethod._
/**
 * User: maximn
 * Date: 1/19/15
 */
class GetSitePagesTest extends SpecificationWithJUnit {

  "createHttpRequestData" should {

    "create HttpRequestData with parameters" >> {
      val cmd = GetSitePages
      cmd.createHttpRequestData must httpRequestDataWith(
        method = be_===(GET),
        url = be_===("/sites/site/pages"))
    }
  }
}
