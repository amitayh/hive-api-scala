package com.wix.hive.model.sites

import com.fasterxml.jackson.module.scala.JsonScalaEnumeration
import com.wix.hive.model.sites.SiteStatus.SiteStatus

case class SiteData(url: String, @JsonScalaEnumeration(classOf[SiteStatusType])status: SiteStatus)
