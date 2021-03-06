package com.wix.hive.commands.sites

import com.wix.hive.client.http.HttpMethod._
import com.wix.hive.matchers.HiveMatchers._
import org.specs2.mutable._

class SiteTest extends SpecificationWithJUnit {

  "createHttpRequestData" should {

    "create HttpRequestData with parameters" >> {
      val cmd = Site
      cmd.createHttpRequestData must httpRequestDataWith(
        method = be_===(GET),
        url = be_===("/sites/site"))
    }
  }
}

