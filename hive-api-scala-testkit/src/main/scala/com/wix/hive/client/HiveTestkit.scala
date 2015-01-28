package com.wix.hive.client

import com.wix.hive.infrastructure.SimplicatorHive


/**
 * User: maximn
 * Date: 11/17/14
 */
trait HiveTestkit extends SimplicatorHive {
  val serverPort: Int = 8089
}