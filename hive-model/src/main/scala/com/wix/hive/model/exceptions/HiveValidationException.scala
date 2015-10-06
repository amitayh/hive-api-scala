package com.wix.hive.model.exceptions

/**
 * Created by Uri_Keinan on 10/6/15.
 */
class HiveValidationException(msg: String, cause: Throwable) extends RuntimeException(msg, cause)
