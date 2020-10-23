package company.ryzhkov

sealed trait Trampoline[+A]

object Trampoline {
  final case class Done[A](result: A) extends Trampoline[A]
  final case class More[A](f: () => Trampoline[A]) extends Trampoline[A]
  final case class Cont[A, B](a: Trampoline[A], f: A => Trampoline[B]) extends Trampoline[B]

  def run[A](trampoline: Trampoline[A]): A = {

    var curr: Trampoline[Any]             = trampoline
    var res: Option[A]                    = None
    var stack: List[Any => Trampoline[A]] = List()

    while (res.isEmpty) {
      curr match {
        case Done(result) =>
          stack match {
            case Nil         =>
              res = Some(result.asInstanceOf[A])
            case ::(f, rest) =>
              stack = rest
              curr = f(result)
          }
        case More(k)      =>
          curr = k()
        case Cont(a, f)   =>
          curr = a
          stack = f.asInstanceOf[Any => Trampoline[A]] :: stack
      }
    }
    res.get
  }
}
