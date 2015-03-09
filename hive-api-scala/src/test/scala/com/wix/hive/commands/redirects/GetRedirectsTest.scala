package com.wix.hive.commands.redirects

import com.wix.hive.client.http.HttpMethod._
import com.wix.hive.matchers.HiveMatchers
import org.specs2.mutable.SpecificationWithJUnit

/**
 * Created by karolisb on 3/9/15.
 */
class GetRedirectsTest extends SpecificationWithJUnit with HiveMatchers {

  "createHttpRequestData" should {

    "create HttpRequestData with parameters" >> {
      val cmd = GetRedirects
      cmd.createHttpRequestData must httpRequestDataWith(
        method = be_===(GET),
        url = be_===("/redirects"))
    }
  }
}
