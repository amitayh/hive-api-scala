package com.wix.hive.commands

import com.wix.hive.commands.services.email._
import com.wix.hive.drivers.SendSingleTestSupport
import com.wix.hive.infrastructure.HiveSimplicatorIT
import org.specs2.matcher.Matcher

class SendSingleIT extends HiveSimplicatorIT with SendSingleTestSupport {

  class ClientContext extends HiveClientContext {

    val sendSingleCommand = SendSingle(
      correlationId = "correlation-id",
      to = ToMailRecipient("recipient.email@wix.com", Some("Recipient name"), RecipientType.To) +: Nil,
      mailHeaders = MailHeaders(replyTo = Some("reply.email@wix.com")),
      from = FromMailRecipient(email = "from.email@wix.com", Some("From name")),
      subject = "Email Subject",
      html = "<b>Html body</b>",
      text = "Text body"
    )


    def emailMatcher: Matcher[SendSingleData] = {
      subject(beEqualTo("Email Subject")) and
        html(contain("Html body")) and
        text(beEqualTo("Text body")) and
        having(ToMailRecipient("recipient.email@wix.com", Some("Recipient name"), RecipientType.To)) and
        having(FromMailRecipient("from.email@wix.com", Some("From name")))
    }
  }

  "SendSingle operations" should {
    "send single email" in new ClientContext {

      RecordHiveCommands[SendSingleData] {
        client.execute(instance, sendSingleCommand)
      } must eventually(contain(emailMatcher).atLeastOnce).await

    }
  }

}
