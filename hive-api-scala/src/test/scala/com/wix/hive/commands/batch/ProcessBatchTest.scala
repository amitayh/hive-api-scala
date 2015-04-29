package com.wix.hive.commands.batch

import java.util.UUID

import com.wix.hive.client.http.HttpMethod._
import com.wix.hive.matchers.HiveMatchers._
import com.wix.hive.model.batch.{BatchOperation, CreateBatchOperation, FailurePolicy}
import org.joda.time.DateTime
import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.specification.Scope

/**
 * @author viliusl
 * @since 29/04/15
 */
class ProcessBatchTest extends SpecificationWithJUnit {

  trait Context extends Scope {
    val modifiedAt = DateTime.now
    val anOperation = BatchOperation(UUID.randomUUID().toString, "GET", "/some/command", Set.empty, Some("""{"key":"value"}"""))
  }

  "createHttpRequestData" should {

    "create HttpRequestData with parameters" in new Context {
      val cmd = ProcessBatch(Some(modifiedAt), operations = Seq(anOperation))

      cmd.createHttpRequestData must httpRequestDataWith(
        method = be_===(POST),
        url = be_===("/batch"),
        query = havePair("modifiedAt" -> modifiedAt.toString),
        body = beSome(CreateBatchOperation(Seq(anOperation), FailurePolicy.STOP_ON_FAILURE)))
    }

    "create HttpRequestData with 'modifiedAt' query parameter omitted" in new Context {
      val cmd = ProcessBatch(operations = Seq(anOperation))

      cmd.createHttpRequestData must httpRequestDataWith(
        method = be_===(POST),
        url = be_===("/batch"),
        query = not(haveKey("modifiedAt")),
        body = anything)
    }
  }
}
