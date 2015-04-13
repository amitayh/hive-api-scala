package com.wix.hive.model.activities

import com.fasterxml.jackson.core.`type`.TypeReference
import com.fasterxml.jackson.module.scala.JsonScalaEnumeration
import com.wix.hive.model.activities.EmailSubscriptionPolicy.EmailSubscriptionPolicy
import com.wix.hive.model.contacts._

case class ContactActivity(name: Option[ContactName] = None, picture: Option[String] = None, company: Option[ActivityCompany] = None,
                           emails: Seq[ActivityContactEmail] = Nil, phones: Seq[ActivityContactPhone] = Nil,
                           addresses: Seq[ActivityAddress] = Nil, dates: Seq[ActivityImportantDate] = Nil, urls: Seq[ActivityContactUrl] = Nil, @JsonScalaEnumeration(classOf[EmailSubscriptionPolicyRef])emailSubscriptionPolicy: Option[EmailSubscriptionPolicy] = None)

class EmailSubscriptionPolicyRef extends TypeReference[EmailSubscriptionPolicy.type]

object EmailSubscriptionPolicy extends Enumeration {
  type EmailSubscriptionPolicy = Value
  val Recurring = Value("RECURRING")
}
