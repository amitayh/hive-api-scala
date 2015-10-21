package com.wix.hive.drivers

import com.wix.hive.domain.SendSingleData
import com.wix.hive.commands.services.email.{FromMailRecipient, ToMailRecipient}
import org.specs2.matcher.{Matcher, Matchers}

trait SendSingleTestSupport extends Matchers {

  def subject(subject: Matcher[String]): Matcher[SendSingleData] = subject ^^ {
    (_: SendSingleData).subject
  }

  def html(html: Matcher[String]): Matcher[SendSingleData] = html ^^ {
    (_: SendSingleData).html
  }

  def text(text: Matcher[String]): Matcher[SendSingleData] = text ^^ {
    (_: SendSingleData).text
  }

  def having(recipient: ToMailRecipient): Matcher[SendSingleData] = contain(recipient).atLeastOnce ^^ {
    (_: SendSingleData).to
  }

  def having(from: FromMailRecipient): Matcher[SendSingleData] = beEqualTo(from) ^^ {
    (_: SendSingleData).from
  }
}
