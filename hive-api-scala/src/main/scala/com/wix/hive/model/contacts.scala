package com.wix.hive.model

import java.util.Date

import com.wix.hive.commands.contacts.GetContacts
import com.wix.hive.model.EmailStatus.EmailStatus
import org.joda.time.DateTime


case class PagingContactsResult(total: Int, pageSize: Int, previous: Option[GetContacts], next: Option[GetContacts], results: Seq[Contact])

case class Address(id: Option[Int] = None, tag: String, address: Option[String] = None, neighborhood: Option[String] = None, city: Option[String] = None, region: Option[String] = None,
                    country: Option[String] = None, postalCode: Option[String] = None)

case class ContactName(prefix: Option[String] = None, first: Option[String] = None, middle: Option[String] = None, last: Option[String] = None, suffix: Option[String] = None)

case class Company(role: Option[String] = None, name: Option[String], middle: Option[String] = None)

object EmailStatus extends Enumeration{
  type EmailStatus = Value
  val OptOut, Transactional, Recurring = Value
}

case class ContactEmail(id: Option[Int] = None, tags: String, email: String, emailStatus : EmailStatus)

case class ContactPhone(id: Option[Int]= None, tag: String, phone: String, normalizedPhone: Option[String] = None)

case class ContactUrl(id: Option[Int], tag: String, url: String)

case class ImportantDate(id: Option[Int] = None, tag: String, date: DateTime)

case class Note(id: Option[Int] = None, modifiedAt: Option[DateTime], content: String)

case class CustomField(id: Option[Int] = None, field: String, value: String)

case class StateLink(href: String, rel: String)

//case class ContactData(name: Option[ContactName] = None, picture: Option[String] = None, company: Option[Company] = None,
//                       tags: Seq[String] = Nil, emails: Seq[ContactEmail] = Nil, phone: Seq[ContactPhone] = Nil,
//                       addresses: Seq[Address] = Nil, urls: Seq[ContactUrl] = Nil, dates: Seq[ImportantDate] = Nil,
//                       notes: Seq[Note] = Nil, custom: Seq[CustomField] = Nil)

case class Contact(id: String, name: Option[ContactName] = None, picture: Option[String] = None, company: Option[Company] = None,
                   tags: Seq[String] = Nil, emails: Seq[ContactEmail] = Nil, phone: Seq[ContactPhone] = Nil,
                   addresses: Seq[Address] = Nil, urls: Seq[ContactUrl] = Nil, dates: Seq[ImportantDate] = Nil,
                   notes: Seq[Note] = Nil, custom: Seq[CustomField] = Nil, createdAt: DateTime, links: Seq[StateLink] = Nil,
                   modifiedAt: Option[Date] = None)
