package com.wix.hive.model.activities

import com.wix.hive.model.contacts._

case class ContactActivity(name: Option[ContactName] = None, picture: Option[String] = None, company: Option[Company] = None,
                           emails: Seq[ContactEmail] = Nil, phones: Seq[ContactPhone] = Nil,
                           addresses: Seq[Address] = Nil, dates: Seq[ImportantDate] = Nil, urls: Seq[ContactUrl] = Nil)
