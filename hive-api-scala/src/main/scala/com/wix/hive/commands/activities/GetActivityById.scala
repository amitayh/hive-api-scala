package com.wix.hive.commands.activities

import com.wix.hive.client.http.HttpMethod._
import com.wix.hive.model.activities.Activity

case class GetActivityById(id: String) extends ActivityCommand[Activity] {
  override val method: HttpMethod = GET

  override val urlParams = s"/$id"
}
