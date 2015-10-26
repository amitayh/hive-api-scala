package com.wix.hive.model.activities

import com.wix.hive.model.activities.ActivityType._

/**
 * Created by Uri_Keinan on 10/20/15.
 */
trait SocialActivityInfo extends ActivityInfo {

}

case class SocialTrackActivityInfo(`type`: SocialType, tracker: Option[SocialTracker], metadata: Option[ItemMetadata]) extends SocialActivityInfo {
  override val activityType = `social/track`
}

case class SocialCommentActivityInfo(text: String, channel: Option[SocialChannel], metadata: Option[Seq[ItemMetadata]], commenter: SocialTracker) extends SocialActivityInfo {
  override val activityType = `social/comment`
}

case class SocialShareUrlActivityInfo(url: String, text: Option[String], channel: SocialChannel, sharer: Option[Name], metadata:Option[Seq[ItemMetadata]]) extends SocialActivityInfo {
  override val activityType = `social/share-url`
}

class SocialType

object SocialType extends Enumeration {
  type SocialType = Value
  val LIKE, FOLLOW, SUBSCRIBE, PIN_IT, FAVORITE, OTHER = Value
}

case class SocialTracker(openId: SocialChannel, name: Option[Name], email: Option[String])

class SocialChannel

object SocialChannel extends Enumeration {
  type SocialChannel = Value
  val FACEBOOK, TWITTER, LINKEDIN, GOOGLE_PLUS, PINTEREST, TUMBLR, BLOGGER, WORDPRESS, OTHER = Value
}
