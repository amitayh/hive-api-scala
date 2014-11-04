package com.wix.hive.commands.sites

import com.wix.hive.client.http.HttpMethod._
import com.wix.hive.commands.HiveBaseCommand
import com.wix.hive.model.sites.SiteData

case object Site extends HiveBaseCommand[SiteData] {
  override val method: HttpMethod = GET
  override val url: String = "/sites"
  override val urlParams = "/site"
}
