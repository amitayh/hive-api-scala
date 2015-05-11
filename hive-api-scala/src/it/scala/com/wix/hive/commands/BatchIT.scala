package com.wix.hive.commands

import com.wix.hive.client.http.HttpMethod
import com.wix.hive.client.http.HttpMethod._
import com.wix.hive.commands.batch.{WixAPIError, ProcessBatch}
import com.wix.hive.infrastructure.HiveSimplicatorIT
import com.wix.hive.json.JacksonObjectMapper.mapper
import com.wix.hive.model.batch.{BatchOperationResult, OperationResult}

/**
 * @author viliusl
 * @since 29/04/15
 */
class BatchIT extends HiveSimplicatorIT {

  class clientContext extends HiveClientContext {

    def aValidResponseFor(entity: AnyRef): Either[WixAPIError, _] = Right(entity)

    def aCommand(httpMethod: HttpMethod, uri: String) = new HiveCommand[AResponse] {
      override def url: String = uri
      override def method: HttpMethod = httpMethod
    }
  }

  "Batch API" should {

    "process a batch request" in new clientContext {
      val op1 = aCommand(HttpMethod.GET, "/v1/method/one")
      val op2 = aCommand(HttpMethod.POST, "/v1/method/two")
      val cmd = ProcessBatch(operations = Seq(op1, op2))

      val resp = BatchOperationResult(Seq(
        OperationResult("0", "GET", "/v1/method/one?version=1.0.0", 200, Some("""{"data": "some data"}""")),
        OperationResult("1", "POST", "/v1/method/two?version=1.0.0", 201, None)))

      expectCustom(app, cmd)(mapper.writeValueAsString(resp))

      client.execute(instance, cmd) must be_===(Seq(aValidResponseFor(AResponse("some data")), aValidResponseFor(None))).await
    }
  }
}

case class AResponse(data: String)