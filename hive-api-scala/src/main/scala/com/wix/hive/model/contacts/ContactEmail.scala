package com.wix.hive.model.contacts

import com.wix.hive.model.contacts.EmailStatus._

case class ContactEmail(tag: String, email: String, emailStatus: EmailStatus, id: Option[Int] = None)
