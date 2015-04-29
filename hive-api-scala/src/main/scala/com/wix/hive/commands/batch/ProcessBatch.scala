package com.wix.hive.commands.batch

import com.wix.hive.client.http.HttpMethod._
import com.wix.hive.client.http.{HttpMethod, _}
import com.wix.hive.commands.HiveCommand
import com.wix.hive.model.batch.FailurePolicy.FailurePolicy
import com.wix.hive.model.batch._
import org.joda.time.DateTime

/**
 * @author viliusl
 * @since 29/04/15
 */
case class ProcessBatch(
  modifiedAt: Option[DateTime] = None,
  failurePolicy: FailurePolicy = FailurePolicy.STOP_ON_FAILURE,
  operations: Seq[BatchOperation]) extends HiveCommand[BatchOperationResult] {

  override def method: HttpMethod = HttpMethod.POST
  override def url: String = "/batch"
  override def body: Option[AnyRef] = Some(CreateBatchOperation(operations, failurePolicy))
  override def query: NamedParameters = modifiedAt map { mAt => Map("modifiedAt" -> mAt.toString) } getOrElse Map.empty
}