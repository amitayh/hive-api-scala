package com.wix.hive.commands.batch

import java.io.{ByteArrayInputStream, InputStream}

import com.wix.hive.client.http.HttpMethod._
import com.wix.hive.client.http.{HttpMethod, _}
import com.wix.hive.commands.HiveCommand
import com.wix.hive.commands.batch.ProcessBatch.{OperationResult, BatchOperationResult, BatchOperation, CreateBatchOperation}
import com.wix.hive.json.JacksonObjectMapper.mapper
import com.wix.hive.matchers.HiveMatchers._
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
      val cmd = ProcessBatch(Some(modifiedAt), operations = Seq(aCommand(uri = "/sites/site/pages")))
      val anOperation = BatchOperation("0", "GET", "/v1/sites/site/pages?version=1.0.0", Set.empty, None)

      cmd.createHttpRequestData must httpRequestDataWith(
        method = be_===(POST),
        url = be_===("/batch"),
        body = beSome(CreateBatchOperation(Seq(anOperation), Some(modifiedAt), FailurePolicy.STOP_ON_FAILURE)))
    }
  }

  "toBatchOperation should infer" >> {
    "id, method, url" in {
      val op = ProcessBatch.toBatchOperation("0", aCommand(httpMethod = HttpMethod.PUT, uri = "/batch/method"))

      op.id mustEqual "0"
      op.method mustEqual "PUT"
      op.relativeUrl must startWith("/v1/batch/method")
    }

    "default version parameter" in {
      val op = ProcessBatch.toBatchOperation("0", aCommand(uri = "/batch/method"))

      op.relativeUrl mustEqual "/v1/batch/method?version=1.0.0"
    }

    "query parameters" in {
      val op = ProcessBatch.toBatchOperation("0", aCommand(queryParams = Map("first" -> "firstVal", "second" -> "secondVal")))

      op.relativeUrl must (contain("first=firstVal") and contain("second=secondVal"))
    }

    "headers" in {
      val op = ProcessBatch.toBatchOperation("0", aCommand(theHeaders = Map("first" -> "firstVal")))

      op.headers must contain("first" -> "firstVal")
    }

    "body" in {
      val op = ProcessBatch.toBatchOperation("0", aCommand(payload = Some("payload")))

      op.body must beSome("payload")
    }
  }

  "decode" should {
    "decode an ok response" in {
      val cmd = ProcessBatch(operations = Seq(aCommand(uri = "/sites/site/pages")))
      val responseFromServer = BatchOperationResult(Seq(
        OperationResult("0", "GET", "/v1/method/one?version=1.0.0", 200, Some("""{"data": "some data"}"""))))

      cmd.decode(asJsonIs(responseFromServer)) must contain(Right(AResponse("some data")))
    }

    "decode a None response for Option response type" in {
      val cmd = ProcessBatch(operations = Seq(aCommand(uri = "/sites/site/pages")))
      val responseFromServer = BatchOperationResult(Seq(
        OperationResult("0", "GET", "/v1/method/one?version=1.0.0", 200, None)))

      cmd.decode(asJsonIs(responseFromServer)) must contain(Right(None))
    }

    "decode a error response" in {
      val cmd = ProcessBatch(operations = Seq(aCommand(uri = "/sites/site/pages")))
      val responseFromServer = BatchOperationResult(Seq(
        OperationResult("0", "GET", "/v1/method/one?version=1.0.0", 401, Some("""{"errorCode": 401, "message": "err msg", "wixErrorCode": -12333}"""))))

      cmd.decode(asJsonIs(responseFromServer)) must contain(Left(WixAPIError(401, Some("err msg"), Some(-12333))))
    }

    "handle an error response when error payload is missing" in {
      val cmd = ProcessBatch(operations = Seq(aCommand(uri = "/sites/site/pages")))
      val responseFromServer = BatchOperationResult(Seq(
        OperationResult("0", "GET", "/v1/method/one?version=1.0.0", 401, None)))

      cmd.decode(asJsonIs(responseFromServer)) must contain(anInstanceOf[Left[WixAPIError, _]])
    }
  }

  def asJsonIs(entity: AnyRef): InputStream = new ByteArrayInputStream(mapper.writeValueAsBytes(entity))

  def aCommand(
    httpMethod: HttpMethod = HttpMethod.GET,
    uri: String = "/some/method",
    queryParams: NamedParameters = Map.empty,
    theHeaders: NamedParameters = Map.empty,
    payload: Option[AnyRef] = None) = new HiveCommand[AResponse] {
    override def url: String = uri
    override def method: HttpMethod = httpMethod
    override def query: NamedParameters = queryParams
    override def headers: NamedParameters = theHeaders
    override def body: Option[AnyRef] = payload
  }
}

case class AResponse(data: String)