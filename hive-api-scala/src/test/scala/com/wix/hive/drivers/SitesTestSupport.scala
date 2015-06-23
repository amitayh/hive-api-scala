package com.wix.hive.drivers

import com.wix.hive.commands.sites.{Page, SitePages}
import com.wix.hive.model.sites._
import org.specs2.matcher.{Matcher, Matchers}

/**
 * User: maximn
 * Date: 1/19/15
 */
trait SitesTestSupport extends Matchers {
  def haveSiteUrl(url: String): Matcher[SiteData] = ((_: SiteData).url) ^^ be_==(url)

  val sitePages = SitePages(SiteData("http://somesite.com", SiteStatus.published), Seq(Page("/path", None, None)))
  val url = "http://wix.com/somesite"
  val siteData = SiteData(url, SiteStatus.published)
  val siteSettings = SiteSettings(title = "site")
}
