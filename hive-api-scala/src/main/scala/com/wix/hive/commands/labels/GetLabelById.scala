package com.wix.hive.commands.labels

import com.wix.hive.client.http.HttpMethod
import com.wix.hive.client.http.HttpMethod._
import com.wix.hive.model.labels.Label

case class GetLabelById(id: String) extends LabelsCommand[Label] {
  override val method: HttpMethod = HttpMethod.GET

  override val urlParams = s"/$id"
}
