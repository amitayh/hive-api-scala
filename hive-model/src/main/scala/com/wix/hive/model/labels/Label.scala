package com.wix.hive.model.labels

import com.fasterxml.jackson.module.scala.JsonScalaEnumeration
import com.wix.hive.model.labels.LabelTypes.LabelType
import org.joda.time.DateTime


case class Label(id: String, createdAt: Option[DateTime], name: String, description: Option[String], totalMembers: Int, @JsonScalaEnumeration(classOf[LabelTypesRef]) labelType: LabelType)
