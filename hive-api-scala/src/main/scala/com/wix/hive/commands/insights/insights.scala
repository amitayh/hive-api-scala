package com.wix.hive.commands.insights

import com.wix.hive.client.http.HttpMethod.HttpMethod
import com.wix.hive.client.http.{HttpMethod, NamedParameters}
import com.wix.hive.commands.HiveBaseCommand
import com.wix.hive.model.activities.ActivityScope
import com.wix.hive.model.activities.ActivityScope.ActivityScope
import com.wix.hive.model.insights.ActivitySummary
import org.joda.time.DateTime

case class InsightActivitySummary(contactId: Option[String] = None,
                                  scope: ActivityScope = ActivityScope.app,
                                  from: Option[DateTime] = None,
                                  until: Option[DateTime] = None) extends HiveBaseCommand[ActivitySummary] {
  override def url: String = contactId match {
    case Some(_) => "/insights/contacts"
    case None => "/insights"
  }

  override def urlParams: String = contactId match {
    case Some(id) => s"/$id/activities/summary"
    case None => "/activities/summary"
  }

  override def method: HttpMethod = HttpMethod.GET

  override def query: NamedParameters =
    super.removeOptionalParameters(
      Map(
        InsightActivitySummary.scope -> scope,
        InsightActivitySummary.from -> from,
        InsightActivitySummary.until -> until))
}

object InsightActivitySummary {
  val scope = "scope"
  val from = "from"
  val until = "until"
}