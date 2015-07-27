package com.fortysevendeg.ninecardslauncher.commons

import com.fortysevendeg.ninecardslauncher.commons.exceptions.Exceptions.NineCardsException
import rapture.core._

import scala.language.implicitConversions
import scala.reflect.ClassTag
import scala.util.Try
import scala.util.control.NonFatal
import scalaz._
import scalaz.concurrent.Task
import Scalaz._

object NineCardExtensions {

  def toEnsureAttemptRun[A](f: Task[NineCardsException \/ A]): NineCardsException \/ A = f.attemptRun match {
    case -\/(ex) => -\/(NineCardsException(msg = ex.getMessage, cause = ex.some))
    case \/-(d) => d
  }

  def fromTryCatchNineCardsException[T](a: => T): NineCardsException \/ T = try {
    \/-(a)
  } catch {
    case e: NineCardsException => -\/(e)
    case NonFatal(t) => -\/(NineCardsException(t.getMessage, Some(t)))
  }

  def resultCatchingNineCardsException[T](a: => T): Result[T, NineCardsException] = try {
    Answer[T, NineCardsException](a)
  } catch {
    case e: NineCardsException => Result.errata[T, NineCardsException](e)
    case NonFatal(t) => Result.errata[T, NineCardsException](NineCardsException(t.getMessage, Some(t)))
  }

  implicit def toResult[A, E <: Exception : ClassTag](disj: E \/ A): Result[A, E] = disj match {
    case -\/(e) => Result.errata[A, E](e)
    case \/-(a) => Result.answer[A, E](a)
  }

  implicit def toDisjunction[A, E <: Exception : ClassTag](res: Result[A, E]): E \/ A = res match {
    case Answer(a) => a.right[E]
    case e@Errata(_) => e.errata.head.left[A]
    case Unforeseen(e) => throw e
  }

  implicit def toTaskDisjuntionFromResult[A, E <: Exception : ClassTag](t: Task[Result[A, E]]): Task[E \/ A] =
    t map (r => toDisjunction(r))

  object CatchAll {

    def apply[E <: Exception] = new CatchingAll[E]()

    class CatchingAll[E <: Exception]() {
      def apply[A](blk: => A)(implicit classTag: ClassTag[E], cv: Throwable => E): Result[A, E] =
        \/.fromTryCatchNonFatal(blk) match {
          case \/-(x) => Result.answer[A, E](x)
          case -\/(e) => Result.errata[A, E](cv(e))
        }
    }

  }


}
