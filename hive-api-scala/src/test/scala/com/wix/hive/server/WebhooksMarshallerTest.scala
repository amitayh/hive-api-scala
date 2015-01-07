package com.wix.hive.server

import com.wix.hive.client.http.{HttpMethod, HttpRequestData}
import com.wix.hive.server.webhooks._
import com.wix.hive.server.webhooks.exceptions.UnkownWebhookTypeException
import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.specification.Scope

/**
 * User: maximn
 * Date: 11/28/14
 */
class WebhooksMarshallerTest extends SpecificationWithJUnit {

  trait ctx extends Scope {
  }

  val marshaller = new WebhooksMarshaller

  def aReq(eventType: String, item: WebhookData) = {
    val req = HttpRequestData(HttpMethod.GET, headers = Map("x-wix-event-type" -> eventType), url = "/notImportantForMarshalling", body = Some(item))
    val expected = item
    (req, expected)
  }

  val types = Seq(
    aReq("/provision/provision", Provision("fc900546-460d-45d3-ac7a-dea7a3c27a94", None)),
    aReq("/provision/disabled", ProvisionDisabled("123456-460d-45d3-ac7a-dea7a3c27a94", None)),
    //    aReq("/billing/upgrade", BillingUpgrade("9457e93c-d62b-41c3-8450-3eae66a060f2")),
    //    aReq("/billing/cancel", BillingCancel()),
    //    aReq("/contacnts/created", ContactsCreated("7d9079d8-a386-4626-9d17-25bd3c875e2c")),
    //    aReq("/contacts/updated", ContactsUpdated("4b02ca1e-4d7f-49b2-b505-0f9050cd575f")),
    aReq("/activities/posted", ActivitiesPosted("45ed130c-10a7-48c1-b32b-76949b1d59b3", "activity-type")),
    aReq("/services/done", ServiceResult("6f8e4d8e-13ee-42fa-9584-a29d28decf70", "6aae078a-63ab-472d-af39-b5280b9b08e8", ServiceRunData("success", None, None)))
  )


  "deserialize webhook object" >> {
    for ((r, i) <- types) {
      ("for " + r) >> {
        marshaller.unmarshal(r) must beSuccessfulTry(i)
      }
    }

    "unmarshall" should {
      "None for unknown URL" in new ctx {
        val (req, _) = aReq("/unknown", Provision("xxxx", None))
        marshaller.unmarshal(req) must beFailedTry[WebhookData].withThrowable[UnkownWebhookTypeException]
      }
    }
  }
}
