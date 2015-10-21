package com.wix.hive.commands.services.email

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.`type`.TypeReference
import com.fasterxml.jackson.module.scala.JsonScalaEnumeration
import com.wix.hive.commands.services.ServicesCommand
import com.wix.hive.commands.services.email.RecipientType.RecipientType

/**
 * User: maximn
 * Date: 2/14/15
 */
case class SendSingle(correlationId: String,
                      to: Seq[ToMailRecipient],
                      mailHeaders: MailHeaders,
                      from: FromMailRecipient,
                      subject: String,
                      html: String,
                      text: String) extends ServicesCommand[Unit] {
  override def url: String = super.url + "/email/single"

  override def body: Option[AnyRef] = Some(SendSingleData(correlationId, to, mailHeaders, from, subject, html, text))
}

private[email] case class SendSingleData(correlationId: String,
                          to: Seq[ToMailRecipient],
                          @JsonProperty("headers")mailHeaders: MailHeaders,
                          from: FromMailRecipient,
                          subject: String,
                          html: String,
                          text: String)


trait MailRecipient {
  def email: String

  def name: Option[String]
}

case class ToMailRecipient(email: String, name: Option[String], @JsonScalaEnumeration(classOf[EventTypeRef]) `type`: RecipientType) extends MailRecipient

case class FromMailRecipient(email: String, name: Option[String]) extends MailRecipient


class EventTypeRef extends TypeReference[RecipientType.type]

object RecipientType extends Enumeration {
  type RecipientType = Value
  val To = Value("TO")
  val CC = Value("CC")
  val BCC = Value("BCC")
}

case class MailHeaders(replyTo: Option[String])