package com.wix.hive.model

import java.util.Date

import com.wix.hive.commands.GetContacts
import com.wix.hive.model.EmailStatus.EmailStatus
import org.joda.time.DateTime


case class PagingContactsResult(total: Int, pageSize: Int, previous: Option[GetContacts], next: Option[GetContacts], results: Seq[Contact])

case class Address(tag: String, id: Option[Int] = None, address: Option[String] = None, neighborhood: Option[String] = None, city: Option[String] = None, region: Option[String] = None,
                    country: Option[String] = None, postalCode: Option[String] = None)

case class ContactName(prefix: Option[String] = None, first: Option[String] = None, middle: Option[String] = None, last: Option[String] = None, suffix: Option[String] = None)

case class Company(role: Option[String] = None, name: Option[String], middle: Option[String] = None)

object EmailStatus extends Enumeration{
  type EmailStatus = Value
  val OptOut, Transactional, Recurring = Value
}

case class ContactEmail(tags: String, email: String, emailStatus : EmailStatus, id: Option[Int] = None)

case class ContactPhone(tag: String, phone: String, normalizedPhone: Option[String] = None, id: Option[Int]= None)

case class ContactUrl(tag: String, url: String, id: Option[Int])

case class ImportantDate(tag: String, date: DateTime, id: Option[Int])

case class Note(modifiedAt: Option[DateTime], content: String, id: Option[Int])

case class CustomField(field: String, value: String, id: Option[Int])

case class StateLink(href: String, rel: String)

case class Contact(id: String, name: Option[ContactName] = None,
                   picture: Option[String] = None, company: Option[Company] = None,
                   tags: Seq[String] = Nil, emails: Seq[ContactEmail] = Nil, phones: Seq[ContactPhone] = Nil,
                   addresses: Seq[Address] = Nil, urls: Seq[ContactUrl] = Nil, dates: Seq[ImportantDate] = Nil,
                   notes: Seq[Note] = Nil, custom: Seq[CustomField] = Nil, createdAt: DateTime, links: Seq[StateLink] = Nil,
                   modifiedAt: Option[Date] = None)
