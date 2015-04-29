package com.wix.hive.commands

import java.util.UUID

import com.wix.hive.commands.batch.ProcessBatch
import com.wix.hive.infrastructure.HiveSimplicatorIT
import com.wix.hive.model.batch.{BatchOperationResult, BatchOperation, OperationResult}

/**
 * @author viliusl
 * @since 29/04/15
 */
class BatchIT extends HiveSimplicatorIT {

  class clientContext extends HiveClientContext {
    def anOperation(
      method: String = "GET",
      url: String = "/v1/some/method?version=1.0.0",
      headers: Set[(String, String)] = Set.empty,
      body: Option[String] = None) = BatchOperation(UUID.randomUUID.toString, method, url, headers, body)

    def aResult(
      op: BatchOperation,
      responseCode: Int = 200,
      payload: Option[String] = None) = OperationResult(op.id, op.method, op.relativeUrl, responseCode, payload)
  }

    "Batch API" should {
    "process a batch request" in new clientContext {
      val op1 = anOperation("GET", "/v1/method/one?version=1.0.0")
      val op2 = anOperation("POST", "/v1/method/two?version=1.0.0", Set("header1" -> "value1"), Some("""{"firstName":"John"}"""))
      val cmd = ProcessBatch(operations = Seq(op1, op2))

      val resp = BatchOperationResult(Seq(
        aResult(op1, payload = Some("""{"aName":"Doe"}""")),
        aResult(op2, 201)))

      expect(app, cmd)(resp)

      client.execute(instance, cmd) must be_===(resp).await
    }
  }
}


