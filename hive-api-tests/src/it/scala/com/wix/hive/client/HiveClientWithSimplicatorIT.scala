package com.wix.hive.client

/**
  * Created with IntelliJ IDEA.
  * User: daniels
  * Date: 10/20/14
  */
class HiveClientWithSimplicatorIT extends BaseHiveClientIT with HubSimplicator {

  skipAll
}

trait HubSimplicator extends HiveApiDrivers {


  //TODO
  override def givenContactExists: Unit = ???
}
