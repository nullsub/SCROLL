package src.main.scala.scroll.internal.graph

import scroll.internal.errors.SCROLLErrors.{SCROLLError}

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
			val pred = target.findPlayerByObject(p)
			if(pred != null) {
				val source = pred.predecessor()
				if(source != null) {
					addBinding(source.getObject, p)
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
			val pred = target.findPlayerByObject(p)
			if(pred != null) {
				val source = pred.predecessor()
				removeBinding(source.getObject, p)
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
		if(containsPlayer(player)) {
			val returnSeq = new mutable.ListBuffer[Object]
			val processing = new mutable.Queue[Object]
			returnSeq += player.asInstanceOf[Object]
			root.successors(player.asInstanceOf[Object]).forEach(n => if(!n.isInstanceOf[Enumeration#Value]) processing.enqueue(n))
			while(processing.nonEmpty) {
				val next = processing.dequeue()
				if(!returnSeq.contains(next)) {
					returnSeq += next
				}
				root.successors(next).forEach(n => if(!n.isInstanceOf[Enumeration#Value]) processing.enqueue(n))
			}
			returnSeq
		} else {
			Seq.empty
		}
	}

	override def facets(player: AnyRef): Seq[Enumeration#Value] = {
		throw new Exception("facets not supported!")
		/*
		require(null != player)
		if(containsPlayer(player)) {
			val returnSeq = new mutable.ListBuffer[Enumeration#Value]
			root.successors(player.asInstanceOf[Object]).forEach{
				case e: Enumeration#Value => returnSeq += e
				case _ =>
			}
			returnSeq
		} else {
			Seq.empty
		}
		*/
	}

	override def containsPlayer(player: AnyRef): Boolean = {
		this.root.containsPlayer(player)
	}

	//nodes() Returns all nodes in this graph, in the order specified by nodeOrder().
	override def allPlayers: Seq[AnyRef] = this.root.allPlayers()

	override def predecessors(player: AnyRef): Seq[AnyRef] = {
		require(null != player)
		this.root.predecessors(player)
	}

	def setDispatchQuery(playerObject: AnyRef, excludeClasses: Seq[Any], excludePlayers: Seq[Object], includeClasses: Seq[Any], includePlayers: Seq[Object]) = {
		this.root.setDispatchQuery(playerObject, excludeClasses, excludePlayers, includeClasses, includePlayers)
	}

	def findMethod(core: AnyRef, name: String, args: Seq[Any]): Either[SCROLLError, (AnyRef, java.lang.reflect.Method)] = {
		this.root.findMethod(core, name, args)
	}

	def findProperty(core: AnyRef, name: String): Either[SCROLLError, AnyRef] = {
		this.root.findProperty(core, name)
	}

}