package reactivedatomic

case class Namespace(name: String) {
  override def toString = name
}

object Namespace {
  val DB = new Namespace("db") {
    val PART = Namespace("db.part")
    val TYPE = Namespace("db.type")
    val CARDINALITY = Namespace("db.cardinality")
    val INSTALL = Namespace("db.install")
    val UNIQUE = Namespace("db.unique")
    val FN = Namespace("db.fn")
  } 
}

trait Namespaceable {
  def name: String
  def ns: Option[Namespace] = None

  override def toString = ns.toString + "/" + name
}

/* DATOMIC TYPES */
sealed trait DatomicData

case class DString(value: String) extends DatomicData {
  override def toString = "\""+ value + "\""
}

case class DBoolean(value: Boolean) extends DatomicData {
  override def toString = value.toString
}

case class DInt(value: Int) extends DatomicData {
  override def toString = value.toString
}

case class DLong(value: Long) extends DatomicData {
  override def toString = value.toString
}

case class DFloat(value: Float) extends DatomicData {
  override def toString = value.toString
}

case class DDouble(value: Double) extends DatomicData {
  override def toString = value.toString
}

case class DBigDec(value: BigDecimal) extends DatomicData {
  override def toString = value.toString
}

case class DInstant(value: java.util.Date) extends DatomicData {
  override def toString = value.toString
}

case class DUuid(value: java.util.UUID) extends DatomicData {
  override def toString = value.toString
}

case class DUri(value: java.net.URI) extends DatomicData {
  override def toString = value.toString
}

case class DRef(value: Keyword) extends DatomicData {
  override def toString = value.toString
}

case class DDatabase(value: datomic.Database) extends DatomicData {
  def entity(e: DLong) = value.entity(e.value)

  override def toString = value.toString
}

object DatomicData {

  def toDatomicData(v: Any): DatomicData = v match {
    case s: String => DString(s)
    case b: Boolean => DBoolean(b)
    case i: Int => DInt(i)
    case l: Long => DLong(l)
    case f: Float => DFloat(f)
    case d: Double => DDouble(d)
    case bd: BigDecimal => DBigDec(bd)
    case d: java.util.Date => DInstant(d)
    case u: java.util.UUID => DUuid(u)
    case u: java.net.URI => DUri(u)
    // REF???
    case _ => throw new RuntimeException("Unknown Datomic Value")
  }

  import scala.collection.JavaConverters._
  def toDatomicNative(d: DatomicData): java.lang.Object = {
    d match {
      case DString(s) => s
      case DBoolean(b) => new java.lang.Boolean(b)
      case DInt(i) => new java.lang.Integer(i)
      case DLong(l) => new java.lang.Long(l)
      case DFloat(f) => new java.lang.Float(f)
      case DDouble(d) => new java.lang.Double(d)
      case DDatabase(db) => db
      //case DBigDec(bd) => new java.lang.BigDecimal(bd)
      //case d: java.util.Date => DInstant(d)
      //case u: java.util.UUID => DUuid(u)
      //case u: java.net.URI => DUri(u)
      // REF???
      case _ => throw new RuntimeException("Can't convert Datomic Data to Native")
    }
  }

}


/* DATOMIC TERMS */
sealed trait Term

case class Var(name: String) extends Term {
  override def toString = "?" + name
}

case class Keyword(override val name: String, override val ns: Option[Namespace] = None) extends Term with Namespaceable {
  override def toString = ":" + ( if(ns.isDefined) {ns.get + "/"} else "" ) + name
}

case class Const(value: DatomicData) extends Term {
  override def toString = value.toString
}

case object Empty extends Term {
  override def toString = "_"
}

trait DataSource extends Term {
  def name: String

  override def toString = "$" + name
}

case class ExternalDS(override val name: String) extends DataSource
case object ImplicitDS extends DataSource {
  def name = ""
}