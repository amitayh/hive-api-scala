package com.wix.hive.commands.batch

import java.io.{ByteArrayInputStream, InputStream}

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.module.scala.JsonScalaEnumeration
import com.wix.hive.client.HiveClient.{VersionKey, version, versionForUrl}
import com.wix.hive.client.http.HttpMethod
import com.wix.hive.client.http.HttpMethod.HttpMethod
import com.wix.hive.client.http.HttpRequestDataImplicits._
import com.wix.hive.commands.HiveCommand
import com.wix.hive.commands.batch.ProcessBatch.CreateBatchOperation
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
  operations: Seq[HiveCommand[_]]) extends HiveCommand[Seq[Either[WixAPIError, _]]] {

  override def method: HttpMethod = HttpMethod.POST

  override def url: String = "/batch"

  override def body: Option[AnyRef] = {
    val ops = operations.view.zipWithIndex.map { case (op, id) =>
      ProcessBatch.toBatchOperation(id.toString, op)
    }

    Some(CreateBatchOperation(ops, modifiedAt, failurePolicy))
  }

  override def decode(r: InputStream): Seq[Either[WixAPIError, _]] = {
    val zipped = operations zip asR[BatchOperationResult](r).operations

    zipped map { case (op, res) =>

      res.responseCode / 100 match {
        case 2 => {
          val response = res.body map { c => op.decode(new ByteArrayInputStream(c.getBytes))  }
          Right(response.getOrElse(response))
        }
        case _ => {
          val errorResponse = res.body map { c => asR[WixAPIError](new ByteArrayInputStream(c.getBytes))  }
          Left(errorResponse.getOrElse(
            WixAPIError(res.responseCode, Some("Server returned error response, but error payload is missing."), None)))
        }
      }
    }
  }
}

object ProcessBatch {

  protected[batch] def toBatchOperation(id: String, cmd: HiveCommand[_]) = {
    val reqData = cmd.createHttpRequestData
    val queryParams = Map(VersionKey -> version) ++ reqData.queryString map { p => s"${p._1}=${p._2}"} mkString "&"
    val url = s"${versionForUrl}${reqData.url}?$queryParams"
    BatchOperation(id, cmd.method.toString, url, cmd.headers.toSet, cmd.body map {_ => reqData.bodyAsString })
  }

  protected[batch] case class BatchOperation(
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

@JsonIgnoreProperties(ignoreUnknown = true)
case class WixAPIError(errorCode: Int, message: Option[String], wixErrorCode: Option[Int])