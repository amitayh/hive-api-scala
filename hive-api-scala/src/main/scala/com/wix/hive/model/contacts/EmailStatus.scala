package com.wix.hive.model.contacts

object EmailStatus extends Enumeration {
  type EmailStatus = Value
  val OptOut = Value("optOut")
  val Transactional = Value("transactional")
  val Recurring = Value("recurring")
}
