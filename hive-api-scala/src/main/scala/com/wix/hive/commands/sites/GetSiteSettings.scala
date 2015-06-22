package com.wix.hive.commands.sites

import com.wix.hive.client.http.HttpMethod._
import com.wix.hive.commands.HiveCommand
import com.wix.hive.model.sites.SiteSettings

case object GetSiteSettings extends HiveCommand[SiteSettings] {
  override val url: String = "/sites/site/settings"

  override val method: HttpMethod = GET
}
