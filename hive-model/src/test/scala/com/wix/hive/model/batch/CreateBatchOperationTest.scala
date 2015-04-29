package com.wix.hive.model.batch

import java.util.UUID

import com.wix.accord.{validate => aValidate, _}
import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.specification.Scope

/**
 * @author viliusl
 * @since 29/04/15
 */
class CreateBatchOperationTest extends SpecificationWithJUnit {

  trait Context extends Scope {
    def anOperation(
      id: String = UUID.randomUUID.toString,
      method: String = "GET",
      relativeUrl: String = "/v1/some/method?version=1.0.0",
      headers: Set[(String, String)] = Set.empty,
      body: Option[String] = None) = BatchOperation(id, method, relativeUrl, headers, body)

    def validate(ops: BatchOperation*) = aValidate(CreateBatchOperation(ops, None))
  }

  "CreateBatchOperation validation should fail for an" >> {

    "empty batch" in new Context {
      validate() must beAnInstanceOf[Failure]
    }

    "empty BatchOperation.id" in new Context {
      validate(anOperation(id = "")) must beAnInstanceOf[Failure]
    }

    "empty BatchOperation.method" in new Context {
      validate(anOperation(method = "")) must beAnInstanceOf[Failure]
    }

    "invalid BatchOperation.method" in new Context {
      validate(anOperation(method = "BOO")) must beAnInstanceOf[Failure]
    }

    "empty BatchOperation.relativeUrl" in new Context {
      validate(anOperation(relativeUrl = "")) must beAnInstanceOf[Failure]
    }

    "empty name in BatchOperation.headers" in new Context {
      validate(anOperation(headers = Set("" -> "value"))) must beAnInstanceOf[Failure]
    }

    "empty value in BatchOperation.headers" in new Context {
      validate(anOperation(headers = Set("name" -> ""))) must beAnInstanceOf[Failure]
    }
  }
}
