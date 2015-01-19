package com.wix.hive.drivers

import com.wix.hive.commands.sites.{Page, SitePages}
import com.wix.hive.model.sites.{SiteData, SiteStatus}

/**
 * User: maximn
 * Date: 1/19/15
 */
trait SitesTestSupport {
  val sitePages = SitePages(SiteData("http://somesite.com", SiteStatus.published), Seq(Page("/path", None, None)))
}
