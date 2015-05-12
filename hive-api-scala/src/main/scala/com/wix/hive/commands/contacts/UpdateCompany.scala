package com.wix.hive.commands.contacts

import com.wix.hive.model.contacts.Contact
import org.joda.time.DateTime

case class UpdateCompany(contactId: String, company: CompanyDTO) extends UpdateContactCommand[Contact] {
  override val modifiedAtOption: Option[DateTime] = None

  override def urlParams: String = super.urlParams + "/company"

  override def body: Option[AnyRef] = Some(company)
}

object UpdateCompany {
  def apply(contactId: String, modifiedAt: DateTime, company: CompanyDTO): UpdateCompany =
    new UpdateCompany(contactId, company) { override val modifiedAtOption: Option[DateTime] = Some(modifiedAt) }
}


case class CompanyDTO(role: Option[String], name: Option[String])