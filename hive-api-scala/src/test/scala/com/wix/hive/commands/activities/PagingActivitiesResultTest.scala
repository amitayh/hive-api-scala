package com.wix.hive.commands.activities

import com.wix.hive.commands.{GetActivities, PagingActivitiesResult}
import org.specs2.matcher.Matcher
import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.specification.Scope

class PagingActivitiesResultTest extends SpecificationWithJUnit {

  class Context extends Scope {
    val prevCursor = "prev"
    val nextCursor = "next"
    val resultWithPrevNext = PagingActivitiesResult(25, Some(prevCursor), Some(nextCursor), Nil)
    val resultSinglePage = PagingActivitiesResult(25, None, None, Nil)

    def haveCursor(cursor: String): Matcher[GetActivities] = (command: GetActivities) => command.cursor.get == cursor
  }


  "previousPageCommand" should {

    "with previous page" in new Context {
      resultWithPrevNext.previousPageCommand must beSome(haveCursor(prevCursor))
    }

    "without previous page" in new Context {
      resultSinglePage.previousPageCommand must beNone
    }
  }

  "nextPageCommand" should {

    "with next page" in new Context {
      resultWithPrevNext.nextPageCommand must beSome(haveCursor(nextCursor))

    }

    "without next page" in new Context {
      resultSinglePage.nextPageCommand must beNone
    }
  }
}