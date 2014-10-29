package com.wix.hive.commands

import com.wix.hive.client.http.HttpMethod._
import com.wix.hive.model.{SiteData, Activity}

case class Site() extends HiveBaseCommand[SiteData] {
  override def method: HttpMethod = GET
  override def url: String = "/sites"
  override def urlParams = "/site"
}