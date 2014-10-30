package com.wix.hive.commands

import com.wix.hive.client.http.HttpMethod._
import com.wix.hive.model.{SiteData, Activity}

case object Site extends HiveBaseCommand[SiteData] {
  override val method: HttpMethod = GET
  override val url: String = "/sites"
  override val urlParams = "/site"
}