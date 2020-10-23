package company.ryzhkov

import scala.annotation.tailrec

object Application extends App {
  val list: RecursiveList[Int] = listGenerator(0, 1000000)

  val list2 = list.map(_.toString + "!")

  printList(list2)

  def listGenerator(start: Int, last: Int): RecursiveList[Int] = {
    var result: RecursiveList[Int] = RecursiveList.Empty

    for (i <- start to last) {
      result = result.add(i)
    }

    result
  }

  @tailrec
  def printList[A](list: RecursiveList[A]): Unit = {
    list match {
      case RecursiveList.Cons(h, t) =>
        print(h)
        print(", ")
        printList(t)
      case RecursiveList.Empty      => print("")
    }
  }
}
