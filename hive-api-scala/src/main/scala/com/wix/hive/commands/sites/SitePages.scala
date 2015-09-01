package com.wix.hive.commands.sites

import com.wix.hive.client.http.HttpMethod._
import com.wix.hive.commands.HiveCommand
import com.wix.hive.model.sites.SiteData

/**
 * User: maximn
 * Date: 1/19/15
 */
case object GetSitePages extends HiveCommand[SitePages]{
  override def url: String = "/sites/site/pages"

  override def method: HttpMethod = GET
}

case class SitePages(siteUrl: SiteData, pages: Seq[Page])
case class Page(path: String, wixPageId: Option[String], appPageId: Option[String], title: String)