package com.wix.hive.commands.services

import com.fasterxml.jackson.core.`type`.TypeReference
import com.fasterxml.jackson.module.scala.JsonScalaEnumeration


/**
 * User: maximn
 * Date: 1/7/15
 */
case class SendEmail(providerId: String, redemptionToken: Option[String], correlationId: String, contacts: EmailContacts) extends ServicesCommand[Unit] {
  override def url: String = super.url + "/email"

  override def body: Option[AnyRef] = Some(EmailServiceData(providerId, redemptionToken, correlationId, contacts))
}

case class EmailServiceData(providerId: String, redemptionToken: Option[String], correlationId: String, contacts: EmailContacts)

case class EmailContacts(@JsonScalaEnumeration(classOf[EmailContactMethodRef]) method: EmailContactMethod.Value, contacts: Seq[String])

class EmailContactMethodRef extends TypeReference[EmailContactMethod.type]

object EmailContactMethod extends Enumeration {
  type EmailContactMethod = Value
  val Id = Value("ID")
  val Label = Value("LABEL")
}
