package src.main.scala.scroll.ext

import java.util

import scala.reflect.ClassTag
import de.tud.deussen.jastadd.gen._

import scala.collection.mutable

class JastAddGraph[N] { // extends MutableGraph[N] {
	var graph: Tree = new Tree()

	def putEdge(source: Object, target: Object): Boolean = {
		println("putEdge:" + source.toString + " " + target.toString)

		var targetPlayer = new Role()
		targetPlayer.setObject(target)

		var sourcePlayer: Player = this.graph.findPlayerByObject(source)
		val oldTargetPlayer = this.graph.findPlayerByObject(target)

		if(oldTargetPlayer != null) {
			if(sourcePlayer != null && this.graph.getPredecessor(oldTargetPlayer) == sourcePlayer) {
				return false
			}
			//targetPlayer.setRoleList(oldTargetPlayer.getRoleList)
			//oldTargetPlayer.setRoleList(null)
			this.removePlayer(target)
			//targetPlayer = oldTargetPlayer.asInstanceOf[Role]
		}

		if(sourcePlayer == null) {
			val newNatural = new Natural()
			newNatural.setObject(source)
			this.graph.addNatural(newNatural)
			sourcePlayer = newNatural
		}
		sourcePlayer.addRole(targetPlayer)

		true
	}

	def removeRole(playerObject: Object, role: Object): Unit = {
		println("removeRole: player "  + playerObject.toString + ", role " + role.toString)

		val playerRole: Player = this.graph.findPlayerByObject(role)
		val player: Player = this.graph.findPlayerByObject(playerObject)
		if(player == null || playerRole == null) {
			throw new Exception("playerPlayer == null || playerRole == null")
		}

		player.removeRole(playerRole)
		if(player.getNumRole == 0 && this.graph.getPredecessor(player) == null) {
			//this.removePlayer(player.getObject) // ?? todo or not todo
		}
	}

	def removePlayer[P <: AnyRef : ClassTag](playerObject: P): Unit = {
		println("removePlayer: " + playerObject.toString)
		val player: Player = this.graph.findPlayerByObject(playerObject)
		if(player == null) {
			return
		}
		val pred = this.graph.getPredecessor(player)
		if(pred == null) {
			this.graph.removeNatural(player)
		} else {
			pred.removeRole(player)
			if(pred.getNumRole == 0 && this.graph.getPredecessor(pred) == null) {
			//	this.removePlayer(pred.getObject) // ?? todo or not todo
			}
		}
	}

	def containsPlayer(playerObject: AnyRef): Boolean = {
		//println("containsPlayer:")

		val player = this.graph.findPlayerByObject(playerObject)
		if(player != null)
			return true
		false
	}


	def facets(playerObject: AnyRef): Seq[Enumeration#Value] = {
		println("facets:")
		throw new Exception("facets not supported!")
/*
		val p: Player = this.graph.findPlayerByObject(playerObject)
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
		*/
	}

	def allPlayers(): Seq[AnyRef] = {
		val ret = new util.HashSet[Object]
		this.graph.getNaturalList.forEach(n => {
			ret.add(n.getObject)
		})
		var lastLevel = ret
		do {
			val newLevel = new util.HashSet[Object]
			lastLevel.forEach(e => {
				newLevel.addAll(this.successors(e))
			})
			ret.addAll(newLevel)
			lastLevel = newLevel
		} while(!lastLevel.isEmpty)
		scala.collection.JavaConverters.asScalaIteratorConverter(ret.iterator()).asScala.toSeq
	}

	def successors(playerObject: AnyRef): util.Set[Object] = {
		val ret = new util.HashSet[Object]
		val player: Player = this.graph.findPlayerByObject(playerObject)
		if(player != null) {
			player.getRoleList.forEach(f => {
				ret.add(f.getObject)
			})
		}

		ret
	}

	def predecessors(playerObject: AnyRef): Seq[AnyRef]  = {
		//println("predecessors:")

		var player: Player = this.graph.findPlayerByObject(playerObject)

		val ret: util.Set[Object] = new util.HashSet[Object]
		while(player != null) {
			player = this.graph.getPredecessor(player)
			if(player != null) {
				ret.add(player.getObject)
			}
		}

		scala.collection.JavaConverters.asScalaIteratorConverter(ret.iterator()).asScala.toSeq
	}
}