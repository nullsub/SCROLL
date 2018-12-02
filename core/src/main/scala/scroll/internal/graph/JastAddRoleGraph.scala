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
			val roles = target.roles(p)
			roles.foreach(role => {
				addBinding(p, role)
				ret  = true
			})
		})
		ret
	}

	override def detach(other: RoleGraph): Unit = {
		require(null != other)
		val target = other.asInstanceOf[JastAddRoleGraph].root
		target.allPlayers().foreach(p => {
			val roles = target.roles(p)
			roles.foreach(role => {
				val _ = removeBinding(p, role)
			})
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
		val _ = root.removeRole(player, role)
	}

	override def removePlayer[P <: AnyRef : ClassTag](player: P): Unit = {
		require(null != player)
		val _ = root.removePlayer(player)
	}

	override def roles(player: AnyRef): Seq[AnyRef] = {
		require(null != player)
		this.root.roles(player)
	}

	override def facets(player: AnyRef): Seq[Enumeration#Value] = {
		require(null != player)
		this.root.facets(player)
	}

	override def containsPlayer(player: AnyRef): Boolean = {
		this.root.containsPlayer(player)
	}

	override def allPlayers(): Seq[AnyRef] = {
		this.root.allPlayers()
	}

	override def predecessors(player: AnyRef): Seq[AnyRef] = {
		require(null != player)
		if(containsPlayer(player)) {
			val returnSeq = new mutable.ListBuffer[Object]
			val processing = new mutable.Queue[Object]
			root.predecessors(player.asInstanceOf[Object]).forEach(n => if(!n.isInstanceOf[Enumeration#Value]) processing.enqueue(n))
			while(processing.nonEmpty) {
				val next = processing.dequeue()
				if(!returnSeq.contains(next)) {
					returnSeq += next
				}
				root.predecessors(next).forEach(n => if(!n.isInstanceOf[Enumeration#Value]) processing.enqueue(n))
			}
			returnSeq
		} else {
			Seq.empty
		}
	}
}