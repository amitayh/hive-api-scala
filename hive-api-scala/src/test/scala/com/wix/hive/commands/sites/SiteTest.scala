package com.wix.hive.commands.contacts

import com.wix.hive.client.http.HttpMethod._
import com.wix.hive.commands.sites.Site
import com.wix.hive.matchers.HiveMatchers
import org.specs2.mutable._
import org.specs2.specification.Scope

class SiteTest extends SpecificationWithJUnit with HiveMatchers {

  "createHttpRequestData" should {

    "create HttpRequestData with parameters" in new Context {
      val cmd = Site
      cmd.createHttpRequestData must httpRequestDataWith(
        method = be_==(GET),
        url = be_==("/sites/site"))
    }
  }


  class Context extends Scope {
  }

}