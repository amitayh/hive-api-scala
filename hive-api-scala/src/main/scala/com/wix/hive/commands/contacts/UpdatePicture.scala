package com.wix.hive.commands.contacts

import com.wix.hive.model.contacts.Contact
import org.joda.time.DateTime

case class UpdatePicture(contactId: String, picture: PictureDTO) extends UpdateContactCommand[Contact] {
  override val modifiedAtOption: Option[DateTime] = None

  override def urlParams: String = super.urlParams + "/picture"

  override def body: Option[AnyRef] = Some(picture)
}

object UpdatePicture {
  def apply(contactId: String, modifiedAt: DateTime, picture: PictureDTO): UpdatePicture =
    new UpdatePicture(contactId, picture) { override val modifiedAtOption: Option[DateTime] = Some(modifiedAt) }
}

case class PictureDTO(picture: String)