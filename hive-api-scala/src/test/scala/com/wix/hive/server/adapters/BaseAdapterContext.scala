package com.wix.hive.server.adapters

import com.wix.hive.client.http.HttpMethod
import org.specs2.specification.Scope

/**
 * User: maximn
 * Date: 12/7/14
 */
class BaseAdapterContext extends Scope{
  val header = ("key", "val")
  val content = "some-data"
  val url = "http://wix.com/testurl"
  val method = "POST"
  val methodEnum = HttpMethod.POST
}