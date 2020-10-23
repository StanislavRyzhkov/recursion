package company.ryzhkov

import company.ryzhkov.Trampoline.{Cont, Done, More}

sealed abstract class RecursiveList[+A] {
  import RecursiveList._

  def add[B >: A](head: B): RecursiveList[B] = {
    Cons(head, this)
  }

  def head: A

  def map[B](f: A => B): RecursiveList[B] = Trampoline.run(map0(f))

  private def map0[B](f: A => B): Trampoline[RecursiveList[B]] = {
    this match {
      case Empty      =>
        Done(Empty)
      case Cons(h, t) =>
        Cont(More(() => t.map0(f)), (res: RecursiveList[B]) => Done(res.add(f(h))))
    }
  }

  def isEmpty: Boolean =
    this match {
      case Cons(_, _) => false
      case Empty      => true
    }

  final def size: Int =
    this match {
      case Cons(_, t) => 1 + t.size
      case Empty      => 0
    }

  def goodSize: Int =
    Trampoline.run(size0)

  def size0: Trampoline[Int] = {
    this match {
      case Cons(_, t) => Cont[Int, Int](More(() => t.size0), res => Done(1 + res))
      case Empty      => Done(0)
    }
  }
}

object RecursiveList {
  case class Cons[A](override val head: A, tail: RecursiveList[A]) extends RecursiveList[A]
  object Empty extends RecursiveList[Nothing] {
    override def head: Nothing = throw new RuntimeException("List is empty")
  }
}
