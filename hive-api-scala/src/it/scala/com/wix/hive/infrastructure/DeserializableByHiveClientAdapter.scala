package com.wix.hive.infrastructure

import com.wix.hive.commands.HiveCommand
import com.wix.hive.commands.activities.PagingActivitiesResult
import com.wix.hive.commands.batch.ProcessBatch.{BatchOperationResult, OperationResult}
import com.wix.hive.commands.batch.{ProcessBatch, WixAPIError}
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

  private def batchResultConverter(cmd: ProcessBatch, originalResponse: Seq[Either[WixAPIError, _]]) = {
    val res = originalResponse.zipWithIndex.map { case (item, index) =>
      val op = ProcessBatch.toBatchOperation(index.toString, cmd.operations(index))
      item match {
        case Right(r) => OperationResult(op.id, op.method, op.relativeUrl, 200, Some(JacksonObjectMapper.mapper.writeValueAsString(r)))
        case Left(e: WixAPIError) => OperationResult(op.id, op.method, op.relativeUrl, e.errorCode, Some(JacksonObjectMapper.mapper.writeValueAsString(e)))
      }
    }

    BatchOperationResult(res)
  }

  def convertToDeserializableByHiveClient[T, R](cmd: HiveCommand[T])(originalResponse: R): Any =
    (cmd, originalResponse) match {
      case (_, r: PagingActivitiesResult) => pagingActivitiesResultConverter(r)
      case (_, r: Activity) => new ActivityAsInHiveServer(r)
      case (cmd: ProcessBatch, r: Seq[_]) => batchResultConverter(cmd, r.asInstanceOf[Seq[Either[WixAPIError, _]]])
      case (_, r: Unit) => ""
      case (_, r) => r
  }
}
