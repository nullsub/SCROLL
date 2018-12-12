package src.main.scala.scroll.ext

import java.util

import scala.reflect.ClassTag
import de.tud.deussen.jastadd.gen._

import scala.collection.mutable

class JastAddGraph[N] { // extends MutableGraph[N] {
	var graph: Tree = new Tree()

	def putEdge(source: Object, target: Object): Boolean = {
		println("putEdge:" + source.toString + " " + target.toString)

		val targetPlayable = new Role()
		targetPlayable.setObject(target)

		var sourcePlayable: Playable = this.graph.findPlayableByObject(source)
		val oldTargetPlayable = this.graph.findPlayableByObject(target)

		if(oldTargetPlayable != null) {
			if(sourcePlayable != null && this.graph.getPredecessor(oldTargetPlayable) == sourcePlayable) {
				return false
			}
			targetPlayable.setRoleList(oldTargetPlayable.getRoleList)
			oldTargetPlayable.setRoleList(null)
			this.removePlayer(target)
		}

		if(sourcePlayable == null) {
			val newNatural = new Natural()
			newNatural.setObject(source)
			this.graph.addNatural(newNatural)
			sourcePlayable = newNatural
		}
		sourcePlayable.addRole(targetPlayable)

		true
	}

	def removeRole(player: Object, role: Object): Unit = {
		println("removeRole: player "  + player.toString + ", role " + role.toString)

		val playableRole: Playable = this.graph.findPlayableByObject(role)
		val playablePlayer: Playable = this.graph.findPlayableByObject(player)
		if(playablePlayer == null || playableRole == null) {
			throw new Exception("playablePlayer == null || playableRole == null")
		}

		playablePlayer.removeRole(playableRole)

		if(playablePlayer.getNumRole == 0 && this.graph.getPredecessor(playablePlayer) == null) {
			this.removePlayer(playablePlayer.getObject)
		}
	}

	def removePlayer[P <: AnyRef : ClassTag](player: P): Unit = {
		println("removePlayer: " + player.toString)
		val playable: Playable = this.graph.findPlayableByObject(player)
		if(playable == null) {
			return
		}
		val pred = this.graph.getPredecessor(playable)
		if(pred == null) {
			this.graph.removeNatural(playable)
		} else {
			pred.removeRole(playable)
			if(pred.getNumRole == 0 && this.graph.getPredecessor(pred) == null) {
				this.removePlayer(pred.getObject)
			}
		}
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
//		throw new Exception("facets not supported!")
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
		val ret = new util.HashSet[Object]
		this.graph.getNaturalList.forEach(n => {
			ret.add(n.getObject)
		})
		var lastLevel = ret
		var changed = false
		do {
			changed = false
			val newLevel = new util.HashSet[Object]
			lastLevel.forEach(e => {
				val successors = this.successors(e)
				newLevel.addAll(successors)
			})
			if(!newLevel.isEmpty) {
				changed = true
				ret.addAll(newLevel)
				lastLevel = newLevel
			}
		} while(changed)
		scala.collection.JavaConverters.asScalaIteratorConverter(ret.iterator()).asScala.toSeq
	}

	def successors(player: AnyRef): util.Set[Object] = {
		val ret = new util.HashSet[Object]
		val p: Playable = this.graph.findPlayableByObject(player)
		if(p != null) {
			p.getRoleList.iterator().forEachRemaining(f => {
				ret.add(f.getObject)
			})
		}

		return ret
	}

	def predecessors(player: AnyRef): Seq[AnyRef]  = {
		//println("predecessors:")

		var p: Playable = this.graph.findPlayableByObject(player)

		val ret: util.Set[Object] = new util.HashSet[Object]
		while(p != null) {
			p = this.graph.getPredecessor(p)
			if(p != null) {
				ret.add(p.getObject)
			}
		}
		//println("predecessors: " + player.toString + " equals: " + ret.toArray.toString)
		scala.collection.JavaConverters.asScalaIteratorConverter(ret.iterator()).asScala.toSeq
	}
}