package com.wix.hive.commands.services

/**
 * User: maximn
 * Date: 1/7/15
 */
case class SendEmail(providerId: String, redemptionToken: Option[String], correlationId: String, contacts: EmailContacts) extends ServicesCommand {
  override def url: String = super.url + "/email"

  override def body: Option[AnyRef] = Some(EmailServiceData(providerId, redemptionToken, correlationId, contacts))
}

case class EmailServiceData(providerId: String, redemptionToken: Option[String], correlationId: String, contacts: EmailContacts)

case class EmailContacts(method: String, contacts: Seq[String])