package com.wix.hive.model.activities

import com.wix.hive.model.contacts._

case class ContactActivity(name: Option[ContactName] = None, picture: Option[String] = None, company: Option[ActivityCompany] = None,
                           emails: Seq[ActivityContactEmail] = Nil, phones: Seq[ActivityContactPhone] = Nil,
                           addresses: Seq[Address] = Nil, dates: Seq[ActivityImportantDate] = Nil, urls: Seq[ActivityContactUrl] = Nil)
