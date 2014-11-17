package com.wix.hive.client

import com.wix.hive.client.infrastructure.SimplicatorHub


class HiveClientWithSimplicatorHubIT extends BaseHiveClientIT with SimplicatorHub {
  override val serverPort: Int = 8089
  override val baseUrl = s"http://localhost:$serverPort"

  val hive = new HiveTestkit(serverPort)

  override def initEnv(): Unit = hive.start()

  override def shutdownEnv() = hive.stop()

  override def beforeTest(): Unit = hive.resetMocks()
}