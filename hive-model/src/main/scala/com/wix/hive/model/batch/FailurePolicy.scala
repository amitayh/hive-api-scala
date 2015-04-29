package com.wix.hive.model.batch

import com.fasterxml.jackson.core.`type`.TypeReference

/**
 * @author viliusl
 * @since 29/04/15
 */
object FailurePolicy extends Enumeration {
  type FailurePolicy = Value
  val STOP_ON_FAILURE, IGNORE_FAILURE = Value
}

class FailurePolicyType extends TypeReference[FailurePolicy.type]

