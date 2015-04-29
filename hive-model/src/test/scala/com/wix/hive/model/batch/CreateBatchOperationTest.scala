package com.wix.hive.model.batch

import org.specs2.mutable.SpecificationWithJUnit

/**
 * @author viliusl
 * @since 29/04/15
 */
class CreateBatchOperationTest extends SpecificationWithJUnit {

  "fail to instantiate CreateBatchOperation with no operations" in {
    CreateBatchOperation(Seq.empty) must throwA[IllegalArgumentException]
  }
}
