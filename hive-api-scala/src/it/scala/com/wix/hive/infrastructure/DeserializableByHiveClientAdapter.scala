package com.wix.hive.infrastructure

import com.wix.hive.commands.activities.PagingActivitiesResult
import com.wix.hive.model.activities.{Activity, ActivityDetails, ActivityInfo}
import org.joda.time.DateTime

/**
 * User: maximn
 * Date: 1/29/15
 */
class DeserializableByHiveClientAdapter {
  // Had to create the "asInHubServer" to add the 'activityType' field in order to be able to generate JSON like in the Hub
  protected case class PagingActivitiesResultAsInHiveServer(pageSize: Int, previousCursor: Option[String], nextCursor: Option[String], results: Seq[ActivityAsInHiveServer])

  protected case class ActivityAsInHiveServer(id: String, createdAt: DateTime, activityLocationUrl: Option[String], activityDetails: Option[ActivityDetails], activityInfo: ActivityInfo) {
    def this(a: Activity) = this(a.id, a.createdAt, a.activityLocationUrl, a.activityDetails, a.activityInfo)

    val activityType = activityInfo.activityType.toString
  }

  private def pagingActivitiesResultConverter(r: PagingActivitiesResult): PagingActivitiesResultAsInHiveServer =
    PagingActivitiesResultAsInHiveServer(r.pageSize, r.previousCursor, r.nextCursor, r.results map (new ActivityAsInHiveServer(_)))

  def convertToDeserializableByHiveClient[T](originalResponse: T): Any =
    originalResponse match {
      case r: PagingActivitiesResult => pagingActivitiesResultConverter(r)
      case r: Activity => new ActivityAsInHiveServer(r)
      case r: Unit => ""
      case r => r
    }
}
