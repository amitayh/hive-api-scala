package com.wix.hive.commands.batch

import com.fasterxml.jackson.module.scala.JsonScalaEnumeration
import com.wix.hive.client.HiveClient
import com.wix.hive.client.HiveClient.{version, VersionKey, versionForUrl}
import com.wix.hive.client.http.HttpMethod
import com.wix.hive.client.http.HttpMethod._
import com.wix.hive.client.http.HttpRequestDataImplicits._
import com.wix.hive.commands.HiveCommand
import com.wix.hive.commands.batch.ProcessBatch.{BatchOperationId, CreateBatchOperation}
import com.wix.hive.model.batch.FailurePolicy.{FailurePolicy, STOP_ON_FAILURE}
import com.wix.hive.model.batch._
import org.joda.time.DateTime
/**
 * @author viliusl
 * @since 29/04/15
 */
case class ProcessBatch(
  modifiedAt: Option[DateTime] = None,
  failurePolicy: FailurePolicy = STOP_ON_FAILURE,
  operations: Seq[(BatchOperationId, HiveCommand[_])]) extends HiveCommand[BatchOperationResult] {

  override def method: HttpMethod = HttpMethod.POST
  override def url: String = "/batch"
  override def body: Option[AnyRef] = {
    val ops = operations.map { case (id, op) => ProcessBatch.toBatchOperation(id, op) }
    Some(CreateBatchOperation(ops, modifiedAt, failurePolicy))
  }
}

object ProcessBatch {
  type BatchOperationId = String

  protected[batch] def toBatchOperation(id: String, cmd: HiveCommand[_]) = {
    val reqData = cmd.createHttpRequestData
    val queryParams = Map(VersionKey -> version) ++ reqData.queryString map { p => s"${p._1}=${p._2}"} mkString "&"
    val url = s"${versionForUrl}${reqData.url}?$queryParams"
    BatchOperation(id, cmd.method.toString, url, cmd.headers.toSet, cmd.body map {_ => reqData.bodyAsString })
  }

  protected[hive] case class BatchOperation(
    id: String,
    method: String,
    relativeUrl: String,
    headers: Set[(String, String)],
    body: Option[String])

  protected[batch] case class CreateBatchOperation(
    operations: Seq[BatchOperation],
    modifiedAt: Option[DateTime] = None,
    @JsonScalaEnumeration(classOf[FailurePolicyType])
    failurePolicy: FailurePolicy = STOP_ON_FAILURE)
}


