package com.wix.hive.model.batch

import com.fasterxml.jackson.module.scala.JsonScalaEnumeration
import com.wix.accord.dsl._
import com.wix.accord.Validator
import com.wix.hive.model.batch.FailurePolicy.{STOP_ON_FAILURE, FailurePolicy}
import org.joda.time.DateTime

/**
 * @author viliusl
 * @since 29/04/15
 */
case class CreateBatchOperation(
  operations: Seq[BatchOperation],
  modifiedAt: Option[DateTime] = None,
  @JsonScalaEnumeration(classOf[FailurePolicyType])
  failurePolicy: FailurePolicy = STOP_ON_FAILURE)

object CreateBatchOperation {
  implicit val CreateBatchOperationValidator: Validator[CreateBatchOperation] = validator[CreateBatchOperation] { c =>
    c.operations is notEmpty
    c.operations.each is valid
  }
}

