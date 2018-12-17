package scroll.internal.graph

import com.google.common.graph.GraphBuilder
import com.google.common.graph.Graphs
import com.google.common.graph.MutableGraph
import scroll.internal.errors.SCROLLErrors.{InvocationError, SCROLLError}

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.reflect.ClassTag

/**
  * Scala specific implementation of a [[scroll.internal.graph.RoleGraph]] using
  * a graph as underlying data model.
  *
  * @param checkForCycles set to true to forbid cyclic role playing relationships
  */
class ScalaRoleGraph(checkForCycles: Boolean = true) extends RoleGraph {

  private val root: MutableGraph[Object] = GraphBuilder.directed().build[Object]()

  override def addPart(other: RoleGraph): Boolean = {
    require(null != other)
    require(other.isInstanceOf[ScalaRoleGraph], MERGE_MESSAGE)

    val target = other.asInstanceOf[ScalaRoleGraph].root

    if (target.nodes().isEmpty) {
      return false
    }

    target.edges().forEach(p => {
      val _ = root.putEdge(p.source(), p.target())
    })
    checkCycles()
    true
  }

  override def detach(other: RoleGraph): Unit = {
    require(null != other)
    val target = other.asInstanceOf[ScalaRoleGraph].root
    target.edges().forEach(p => {
      val _ = removeBinding(p.source(), p.target())
    })
  }

  private[this] def checkCycles(): Unit = {
    if (checkForCycles) {
      if (Graphs.hasCycle(root)) {
        throw new RuntimeException(s"Cyclic role-playing relationship found!")
      }
    }
  }

  override def addBinding[P <: AnyRef : ClassTag, R <: AnyRef : ClassTag](player: P, role: R): Unit = {
    require(null != player)
    require(null != role)
    root.putEdge(player, role)
    if (checkForCycles && Graphs.hasCycle(root)) {
      throw new RuntimeException(s"Cyclic role-playing relationship for player '$player' found!")
    }
  }

  override def removeBinding[P <: AnyRef : ClassTag, R <: AnyRef : ClassTag](player: P, role: R): Unit = {
    require(null != player)
    require(null != role)
    val _ = root.removeEdge(player, role)
  }

  override def removePlayer[P <: AnyRef : ClassTag](player: P): Unit = {
    require(null != player)
    val _ = root.removeNode(player)
  }

  override def roles(player: AnyRef): Seq[AnyRef] = {
    require(null != player)
    if (containsPlayer(player)) {
      val returnSeq = new mutable.ListBuffer[Object]
      val processing = new mutable.Queue[Object]
      returnSeq += player.asInstanceOf[Object]
      root.successors(player.asInstanceOf[Object]).forEach(n => if (!n.isInstanceOf[Enumeration#Value]) processing.enqueue(n))
      while (processing.nonEmpty) {
        val next = processing.dequeue()
        if (!returnSeq.contains(next)) {
          returnSeq += next
        }
        root.successors(next).forEach(n => if (!n.isInstanceOf[Enumeration#Value]) processing.enqueue(n))
      }
      returnSeq
    } else {
      Seq.empty
    }
  }

  override def facets(player: AnyRef): Seq[Enumeration#Value] = {
    require(null != player)
    if (containsPlayer(player)) {
      val returnSeq = new mutable.ListBuffer[Enumeration#Value]
      root.successors(player.asInstanceOf[Object]).forEach {
        case e: Enumeration#Value => returnSeq += e
        case _ =>
      }
      returnSeq
    } else {
      Seq.empty
    }
  }

  override def containsPlayer(player: AnyRef): Boolean = root.nodes().contains(player)

  override def allPlayers: Seq[AnyRef] = root.nodes().asScala.toSeq

  override def predecessors(player: AnyRef): Seq[AnyRef] = {
    require(null != player)
    if (containsPlayer(player)) {
      val returnSeq = new mutable.ListBuffer[Object]
      val processing = new mutable.Queue[Object]
      root.predecessors(player.asInstanceOf[Object]).forEach(n => if (!n.isInstanceOf[Enumeration#Value]) processing.enqueue(n))
      while (processing.nonEmpty) {
        val next = processing.dequeue()
        if (!returnSeq.contains(next)) {
          returnSeq += next
        }
        root.predecessors(next).forEach(n => if (!n.isInstanceOf[Enumeration#Value]) processing.enqueue(n))
      }
      returnSeq
    } else {
      Seq.empty
    }
  }

	def doDispatch[E](core: AnyRef, name: String, args: Any*): Either[SCROLLError, E] = {
		throw new Exception("doing dispatch on ScalaRoleGraph not yet supported!")
	}

	def dispatchSelect[E](core: AnyRef, name: String): Either[SCROLLError, E] = {
		throw new Exception("doing dispatchSelect on ScalaRoleGraph not yet supported!")
	}

  def setDispatchQuery(from: AnyRef => Boolean, to: AnyRef => Boolean, through: AnyRef => Boolean, bypassing: AnyRef => Boolean) = {
    throw new Exception("setDispatchQuery on ScalaRoleGraph not yet supported!")
  }


}