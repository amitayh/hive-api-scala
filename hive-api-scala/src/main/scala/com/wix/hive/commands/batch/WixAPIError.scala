package com.wix.hive.commands.batch

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

/**
 * @author viliusl
 * @since 12/05/15
 */
@JsonIgnoreProperties(ignoreUnknown = true)
case class WixAPIError(errorCode: Int, message: Option[String], wixErrorCode: Option[Int])
