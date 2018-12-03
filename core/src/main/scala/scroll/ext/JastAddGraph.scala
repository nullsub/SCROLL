package src.main.scala.scroll.ext

import java.util

import de.tud.deussen.jastadd.gen._

import scala.collection.mutable
import scala.reflect.ClassTag

class JastAddGraph[N] { // extends MutableGraph[N] {
	var graph: Tree = new Tree()

	def putEdge(source: Object, target: Object): Boolean = {
		println("putEdge:" + source.toString + " " + target.toString)

		var targetPlayable = this.graph.findPlayableByObject(target)
		if(targetPlayable != null) {
			//throw new Exception("targetPlayable already exists!")
		}
		if(targetPlayable == null) {
			targetPlayable = new Role()
			targetPlayable.setObject(target)
		}

		var playable: Playable = this.graph.findPlayableByObject(source)
		if(playable == null) {
			playable = new Natural()
			playable.setObject(source)
			this.graph.addNatural(playable.asInstanceOf[Natural])
		}
		playable.addRole(targetPlayable.asInstanceOf[Role])
		true
	}

	def removeRole(player: Object, role: Object): Unit = {
		println("removeRole:")

		val playableRole: Playable = this.graph.findPlayableByObject(role)
		//var playablePlayer: Playable = this.graph.findPlayableByObject(role)

		val pred = this.graph.getPredecessor(playableRole)

		if(pred == null && pred.getObject == player) {
			var toDelete = -1
			this.graph.getNaturalList.forEach(n => {
				if(n == playableRole)
					toDelete = this.graph.getNaturalList.getIndexOfChild(n.asInstanceOf)
			})
			if(toDelete > -1) {
				this.graph.getNaturalList.removeChild(toDelete)
			}
		}
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
		if(p != null) {
			val returnSeq = new mutable.ListBuffer[Enumeration#Value]
			val objectSet = for (f <- p.successors()) yield f.getObject
			scala.collection.JavaConverters.seqAsJavaList[Object](objectSet).forEach {
				case e: Enumeration#Value => returnSeq += e
				case _ =>
			}
			return returnSeq
		}
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

/*
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
*/

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