package src.main.scala.scroll.internal.graph

import scala.collection.mutable
import scala.reflect.ClassTag
import src.main.scala.scroll.ext.JastAddGraph
import scroll.internal.graph.RoleGraph

/**
  * Scala specific implementation of a [[scroll.internal.graph.RoleGraph]] using
  * a graph as underlying data model.
  *
  * @param checkForCycles set to true to forbid cyclic role playing relationships
  */
class JastAddRoleGraph(checkForCycles: Boolean = true) extends RoleGraph {

	private val root: JastAddGraph[Object] = new JastAddGraph[Object]()

	override def addPart(other: RoleGraph): Boolean = {
		require(null != other)
		var ret = false
		val target = other.asInstanceOf[JastAddRoleGraph].root

		target.allPlayers().foreach(p => {
			val pred = target.graph.findPlayerByObject(p)
			if(pred != null) {
				val source = target.graph.getPredecessor(pred)
				if(source != null && p != null) {
					addBinding(source, p)
					ret = true
				}
			}
		})
		ret
	}

	override def detach(other: RoleGraph): Unit = {
		require(null != other)
		val target = other.asInstanceOf[JastAddRoleGraph].root
		target.allPlayers().foreach(p => {
			val pred = target.graph.findPlayerByObject(p)
			if(pred != null) {
				val source = target.graph.getPredecessor(pred)
				removeBinding(source, p)
			}
		})
	}

	override def addBinding[P <: AnyRef : ClassTag, R <: AnyRef : ClassTag](player: P, role: R): Unit = {
		require(null != player)
		require(null != role)
		root.putEdge(player, role)
	}

	override def removeBinding[P <: AnyRef : ClassTag, R <: AnyRef : ClassTag](player: P, role: R): Unit = {
		require(null != player)
		require(null != role)
		val _ = this.root.removeRole(player, role)
	}

	override def removePlayer[P <: AnyRef : ClassTag](player: P): Unit = {
		require(null != player)
		val _ = root.removePlayer(player)
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
		throw new Exception("facets not supported!")

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

	override def containsPlayer(player: AnyRef): Boolean = {
		this.root.containsPlayer(player)
	}

	//nodes() Returns all nodes in this graph, in the order specified by nodeOrder().
	override def allPlayers: Seq[AnyRef] = this.root.allPlayers() //root.nodes().asScala.toSeq

	override def predecessors(player: AnyRef): Seq[AnyRef] = {
		require(null != player)
		return this.root.predecessors(player)
	}
}