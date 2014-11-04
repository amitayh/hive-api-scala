package com.wix.hive.commands.activities

import org.specs2.matcher.Matcher
import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.specification.Scope

class PagingActivitiesResultTest extends SpecificationWithJUnit {

  class Context extends Scope {
    val prevCursor = "prev"
    val nextCursor = "next"
    val resultWithPrev = PagingActivitiesResult(25, Some(prevCursor), None, Nil)
    val resultWithNext = PagingActivitiesResult(25, None, Some(nextCursor), Nil)
    val resultSinglePage = PagingActivitiesResult(25, None, None, Nil)

    def beSomeWithCursor(cursor: Matcher[Option[String]]): Matcher[Option[GetActivities]] = {
      beSome(cursor ^^ { (_: GetActivities).cursor aka "cursor" })
    }
  }


  "previousPageCommand" should {

    "with previous page" in new Context {
      resultWithPrev.previousPageCommand must beSomeWithCursor(beSome(prevCursor))
    }

    "without previous page" in new Context {
      resultSinglePage.previousPageCommand must beNone
    }
  }

  "nextPageCommand" should {

    "with next page" in new Context {
      resultWithNext.nextPageCommand must beSomeWithCursor(beSome(nextCursor))
    }

    "without next page" in new Context {
      resultSinglePage.nextPageCommand must beNone
    }
  }
}