package com.wix.hive.model.batch

import com.fasterxml.jackson.module.scala.JsonScalaEnumeration
import com.wix.hive.model.batch.FailurePolicy.{STOP_ON_FAILURE, FailurePolicy}

/**
 * @author viliusl
 * @since 29/04/15
 */
case class CreateBatchOperation(
  operations: Seq[BatchOperation],
  @JsonScalaEnumeration(classOf[FailurePolicyType])
  failurePolicy: FailurePolicy = STOP_ON_FAILURE) {

  require(operations.nonEmpty, "At least one 'BatchOperation' has to be provided")
}
