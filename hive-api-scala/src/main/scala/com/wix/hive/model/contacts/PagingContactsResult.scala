package com.wix.hive.model.contacts

import com.wix.hive.commands.contacts.GetContacts

case class PagingContactsResult(total: Int, pageSize: Int, previous: Option[GetContacts], next: Option[GetContacts], results: Seq[Contact])
