package src.main.scala.scroll.ext

import java.util

import de.tud.deussen.jastadd.gen._

import scala.reflect.ClassTag

class JastAddGraph[N] { // extends MutableGraph[N] {
	var graph: Tree = new Tree()

	def putEdge(source: Object, target: Object): Boolean = {
		println("putEdge:" + source.toString + " " + target.toString)

		var playable: Playable = this.graph.findPlayableByObject(source)
		if(playable == null) {
			playable = new Natural()
			playable.setObject(source)
			this.graph.addNatural(playable.asInstanceOf[Natural])
		}
		val role = new Role()
		role.setObject(target)
		playable.addRole(role)
		true
	}

	def removeRole(player: Object, role: Object): Unit = {
		var playableRole: Playable = this.graph.findPlayableByObject(role)
		//var playablePlayer: Playable = this.graph.findPlayableByObject(role)
//todo fixme, only remove role when it is child from playablePlayer
		this.removePlayer(playableRole.getObject)
	}

	def removePlayer[P <: AnyRef : ClassTag](player: P): Unit = {
		println("removePlayer:")
		val playable: Playable = this.graph.findPlayableByObject(player)
		if(playable == null) {
			return
		}
		val pred = this.graph.getPredecessor(playable)
		if(pred == null) {
			var toDelete = -1
			this.graph.getNaturalList.forEach(n => {
				if(n == playable)
					toDelete = this.graph.getNaturalList.getIndexOfChild(n.asInstanceOf)
			})
			if(toDelete > -1) {
				this.graph.getNaturalList.removeChild(toDelete)
			}
			return
		}
		pred.removeRole(playable)
	}

	def containsPlayer(player: AnyRef): Boolean = {
		//println("containsPlayer:")

		val playable = this.graph.findPlayableByObject(player)
		if(playable != null)
			return true
		false
	}


	def facets(player: AnyRef): Seq[Enumeration#Value] = {
		println("facets:")

		val p: Playable = this.graph.findPlayableByObject(player)
		if(p != null)
			p.getPlayers
		Seq.empty
	}

	def allPlayers(): Seq[AnyRef] = {
		println("allPlayers:")

		val ret: util.List[Playable] = new util.LinkedList
		this.graph.getNaturals.forEach(n => {
			ret.addAll(scala.collection.JavaConverters.seqAsJavaList[Playable](n.getPlayers))
		})
		val retSeq: Seq[Playable] = scala.collection.JavaConverters.asScalaIteratorConverter(ret.iterator()).asScala.toSeq
		for (f <- retSeq) yield f.getObject
	}


	def roles(player: AnyRef): Seq[AnyRef] = {
		println("roles:")

		val p: Playable = this.graph.findPlayableByObject(player)
		if(p != null)
			return for (f <- p.getPlayers) yield f.getObject
		Seq.empty
	}

	def predecessors(player: AnyRef): util.Set[Object] = {
		println("predecessors:")

		var p: Playable = this.graph.findPlayableByObject(player)

		val ret: util.Set[Object] = new util.HashSet[Object]
		do {
			p = this.graph.getPredecessor(p)
			if(p != null) {
				ret.add(p.getObject)
			}
		} while(p != null)

		//println("predecessors: " + node.toString + " equals: " + values.toArray.toString)
		ret
	}
}