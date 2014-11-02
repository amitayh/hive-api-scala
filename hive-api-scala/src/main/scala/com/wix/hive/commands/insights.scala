package com.wix.hive.commands

import com.wix.hive.client.http.HttpMethod.HttpMethod
import com.wix.hive.client.http.{HttpMethod, NamedParameters}
import com.wix.hive.commands.ActivityScope.ActivityScope
import com.wix.hive.model.ActivitySummary
import org.joda.time.DateTime

case class InsightActivitySummaryForContact(contactId: String, scope: ActivityScope = ActivityScope.app,
                                            from: Option[DateTime] = None, until: Option[DateTime] = None) extends HiveBaseCommand[ActivitySummary] {
  override def url: String = "/insights/contacts"

  override def urlParams: String = s"/$contactId/activities/summary"

  override def method: HttpMethod = HttpMethod.GET

  object QueryKeys {
    val scope = "scope"
    val from = "from"
    val until = "until"
  }

  override def query: NamedParameters = Map(
    QueryKeys.scope -> scope,
    QueryKeys.from -> from,
    QueryKeys.until -> until)
    .collect {
    case (k, Some(v)) => (k, v.toString)
    case (k, v) => (k, v.toString)
  }
}