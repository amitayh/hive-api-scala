package com.wix.hive.commands.contacts

import com.wix.hive.model.contacts.Contact
import org.joda.time.DateTime

case class UpdateCompany(contactId: String, company: CompanyDTO, modifiedAt: DateTime) extends UpdateContactCommand[Contact] {
  override def urlParams: String = super.urlParams + "/company"

  override def body: Option[AnyRef] = Some(company)
}


case class CompanyDTO(role: Option[String], name: Option[String])