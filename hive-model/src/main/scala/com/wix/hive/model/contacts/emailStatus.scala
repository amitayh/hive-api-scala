package com.wix.hive.model.contacts

import com.fasterxml.jackson.core.`type`.TypeReference

class EmailStatusRef extends TypeReference[EmailStatus.type]

object EmailStatus extends Enumeration {
  type EmailStatus = Value
  val OptOut = Value("optOut")
  val Transactional = Value("transactional")
  val Recurring = Value("recurring")
}
