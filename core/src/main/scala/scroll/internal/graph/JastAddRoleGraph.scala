package src.main.scala.scroll.internal.graph

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.reflect.ClassTag
import src.main.scala.scroll.ext.JastAddGraph
import scroll.internal.graph.{RoleGraph, ScalaRoleGraph}

/**
  * Scala specific implementation of a [[scroll.internal.graph.RoleGraph]] using
  * a graph as underlying data model.
  *
  * @param checkForCycles set to true to forbid cyclic role playing relationships
  */
class JastAddRoleGraph(checkForCycles: Boolean = true) extends ScalaRoleGraph {

  private val root: JastAddGraph[Object] = new JastAddGraph[Object]()

  override def addPart(other: RoleGraph): Boolean = {
    require(null != other)
    require(other.isInstanceOf[JastAddRoleGraph], MERGE_MESSAGE)

    val target = other.asInstanceOf[JastAddRoleGraph].root

    if (target.nodes().isEmpty) {
      return false
    }

    target.edges().forEach(p => {
      val _ = root.putEdge(p.source, p.target)
    })
    checkCycles()
    true
  }

  override def detach(other: RoleGraph): Unit = {
    require(null != other)
    val target = other.asInstanceOf[JastAddRoleGraph].root
    target.edges().forEach(p => {
      val _ = removeBinding(p.source, p.target)
    })
  }

  private[this] def checkCycles(): Unit = {
    if (checkForCycles) {
      if (root.hasCycle) {
        throw new RuntimeException(s"Cyclic role-playing relationship found!")
      }
    }
  }

  override def addBinding[P <: AnyRef : ClassTag, R <: AnyRef : ClassTag](player: P, role: R): Unit = {
    require(null != player)
    require(null != role)
    root.putEdge(player, role)
    if (checkForCycles && root.hasCycle) {
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
}