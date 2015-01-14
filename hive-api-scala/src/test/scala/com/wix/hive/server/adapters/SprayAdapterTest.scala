package com.wix.hive.server.adapters

import org.specs2.mutable.SpecificationWithJUnit
import spray.httpx.RequestBuilding._
/**
 * User: maximn
 * Date: 1/13/15
 */
class SprayAdapterTest extends SpecificationWithJUnit {

  trait ctx extends BaseAdapterContext {
      val req = Post(url, content) ~> addHeader("key", "val")
  }


  "convert the Servlet Req to Hive SDK request" in new ctx {
    val converted = com.wix.hive.server.adapters.Spray.RequestConverterFromSpray.convert(req)

    converted.url must be_===(url)
    converted.headers must havePair(header)
    converted.method must be_===(methodEnum)
    converted.body must beSome(content)
  }
}
