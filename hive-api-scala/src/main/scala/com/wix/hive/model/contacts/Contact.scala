package com.wix.hive.model.contacts

import java.util.Date

import org.joda.time.DateTime

case class Contact(id: String, createdAt: DateTime, name: Option[ContactName] = None,
                   picture: Option[String] = None, company: Option[Company] = None,
                   tags: Seq[String] = Nil, emails: Seq[ContactEmail] = Nil, phones: Seq[ContactPhone] = Nil,
                   addresses: Seq[Address] = Nil, urls: Seq[ContactUrl] = Nil, dates: Seq[ImportantDate] = Nil,
                   notes: Seq[Note] = Nil, custom: Seq[CustomField] = Nil, links: Seq[StateLink] = Nil,
                   modifiedAt: Option[Date] = None)
