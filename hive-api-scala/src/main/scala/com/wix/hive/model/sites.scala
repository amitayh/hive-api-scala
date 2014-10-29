package com.wix.hive.model

import com.fasterxml.jackson.core.`type`.TypeReference
import com.fasterxml.jackson.module.scala.JsonScalaEnumeration
import com.wix.hive.model.SiteStatus.SiteStatus

case class SiteData(url: String, @JsonScalaEnumeration(classOf[SiteStatusType])status: SiteStatus)


class SiteStatusType extends TypeReference[SiteStatus.type]


object SiteStatus extends Enumeration {
  type SiteStatus = Value
  val published, unpublished = Value
}