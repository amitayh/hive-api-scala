package com.wix.hive.commands.batch

import com.wix.hive.client.http.HttpMethod._
import com.wix.hive.client.http.{HttpMethod, _}
import com.wix.hive.commands.HiveCommand
import com.wix.hive.commands.batch.ProcessBatch.{CreateBatchOperation, BatchOperation}
import com.wix.hive.matchers.HiveMatchers._
import com.wix.hive.model.batch.FailurePolicy
import org.joda.time.DateTime
import org.specs2.mutable.SpecificationWithJUnit

/**
 * @author viliusl
 * @since 29/04/15
 */
class ProcessBatchTest extends SpecificationWithJUnit {

  "createHttpRequestData" should {

    "create HttpRequestData with parameters" in {
      val modifiedAt = DateTime.now
      val cmd = ProcessBatch(Some(modifiedAt), operations = Seq("theId" -> aCommand(uri = "/sites/site/pages")))
      val anOperation = BatchOperation("theId", "GET", "/v1/sites/site/pages?version=1.0.0", Set.empty, None)

      cmd.createHttpRequestData must httpRequestDataWith(
        method = be_===(POST),
        url = be_===("/batch"),
        body = beSome(CreateBatchOperation(Seq(anOperation), Some(modifiedAt), FailurePolicy.STOP_ON_FAILURE)))
    }
  }

  "toBatchOperation should infer" >> {
    "id, method, url" in {
      val op = ProcessBatch.toBatchOperation("anId", aCommand(httpMethod = HttpMethod.PUT, uri = "/batch/method"))

      op.id mustEqual "anId"
      op.method mustEqual "PUT"
      op.relativeUrl must startWith("/v1/batch/method")
    }

    "default version parameter" in {
      val op = ProcessBatch.toBatchOperation("anId", aCommand(uri = "/batch/method"))

      op.relativeUrl mustEqual "/v1/batch/method?version=1.0.0"
    }

    "query parameters" in {
      val op = ProcessBatch.toBatchOperation("anId", aCommand(queryParams = Map("first" -> "firstVal", "second" -> "secondVal")))

      op.relativeUrl must (contain("first=firstVal") and contain("second=secondVal"))
    }

    "headers" in {
      val op = ProcessBatch.toBatchOperation("anId", aCommand(theHeaders = Map("first" -> "firstVal")))

      op.headers must contain("first" -> "firstVal")
    }

    "body" in {
      val op = ProcessBatch.toBatchOperation("anId", aCommand(payload = Some("payload")))

      op.body must beSome("payload")
    }
  }

  def aCommand(
    httpMethod: HttpMethod = HttpMethod.GET,
    uri: String = "/some/method",
    queryParams: NamedParameters = Map.empty,
    theHeaders: NamedParameters = Map.empty,
    payload: Option[AnyRef] = None) = new HiveCommand[AnyRef] {
    override def url: String = uri
    override def method: HttpMethod = httpMethod
    override def query: NamedParameters = queryParams
    override def headers: NamedParameters = theHeaders
    override def body: Option[AnyRef] = payload
  }
}
