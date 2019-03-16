package src.main.scala.scroll.ext

import java.util

import scala.reflect.ClassTag
import de.tud.deussen.jastadd.gen._
import scroll.internal.errors.SCROLLErrors
import scroll.internal.errors.SCROLLErrors.{IllegalRoleInvocationDispatch, RoleNotFound, SCROLLError}

class JastAddGraph[N] { // extends MutableGraph[N] {
	var graph: Tree = new Tree()
	val playerObjectCache = new collection.mutable.HashMap[Object, Player]()
	val dispatchClassArgumentsCache = new collection.mutable.HashMap[String, Array[Class[_]]]() // als uncached attribute

	def printTree(): Unit = {
		println("Tree: ")
		this.graph.getNaturalList.forEach(r => {
			println("  natural: " + r.getObject.toString)
			this.printNode(r, 1)
		})
		println("  ")
		println("Cache")
		this.playerObjectCache.values.foreach(p => {
			println("  object: " + p.getObject.toString)
		})
	}

	def printNode(node: Player, level: Int): Unit = {
		node.getRoleList.forEach(r => {
			println("    " * level + "node: " + r.getObject.toString)
			this.printNode(r, level + 1)
		})
	}

	def findPlayerByObject(obj: Object): Player = {
		this.playerObjectCache.getOrElse(obj, null)
	}
	def storeInPlayerObjectCache(player: Player): Unit = {
		this.playerObjectCache.put(player.getObject, player)
	}

	def setDispatchQuery(playerObject: AnyRef, excludeClasses: Seq[Any], excludePlayers: Seq[Object], includeClasses: Seq[Any], includePlayers: Seq[Object]): Unit = {
		val player: Player = this.findPlayerByObject(playerObject)
		if(player == null) {
			println("player not found: " + playerObject)
			return
		}
		if(excludeClasses.isEmpty && excludePlayers.isEmpty && includeClasses.isEmpty && includePlayers.isEmpty) {
			return
		}
		if(player.hasDispatchQuery && player.getDispatchQuery != null
			&& compareListSeq(player.getDispatchQuery.getExcludes.getClasses, excludeClasses)
			&& compareListSeq(player.getDispatchQuery.getExcludes.getPlayers, excludePlayers)
		) {
			//System.out.println("dispatchquery already exists. returning!")
			return
		}

		val dispatchQuery = new DispatchQuery()
		val excludes = new DQFilter()
		excludes.setClasses(scala.collection.JavaConverters.seqAsJavaList(excludeClasses))
		excludes.setPlayers(scala.collection.JavaConverters.seqAsJavaList(excludePlayers))
		dispatchQuery.setExcludes(excludes)

		val includes = new DQFilter()
		includes.setClasses(scala.collection.JavaConverters.seqAsJavaList(includeClasses))
		includes.setPlayers(scala.collection.JavaConverters.seqAsJavaList(includePlayers))
		dispatchQuery.setIncludes(includes)

		player.setDispatchQuery(dispatchQuery)
		player.flushTreeCache()
	}

	def compareListSeq(list: util.List[_], seq: Seq[Any]): Boolean = {
		if(list.size() == seq.length) {
			return true
		}
		false
	}

	def findMethod[E](playerObject: Object, name: String, args: Seq[Any]): Either[SCROLLError, (AnyRef, java.lang.reflect.Method)] = {
		//printTree()
		val player: Player = this.findPlayerByObject(playerObject)
		if(player == null) {
			return Left(RoleNotFound(playerObject.toString, name, args))
		}
		var str = ""
		args.foreach(arg => {
			if(arg != null) str += ';' + arg.getClass.toString
			else str += ';'
		})
		val classArgs = this.dispatchClassArgumentsCache.getOrElseUpdate(str, args.map(arg => {
			if(arg != null) arg.getClass
			else null
		}).toArray)
		val ret = player.findMethod(name, classArgs)
		if(ret == null) {
			return Left(IllegalRoleInvocationDispatch(playerObject.toString, name, args))
		}
		Right(ret)
	}

	def findProperty(playerObject: Object, name: String): Either[SCROLLError, AnyRef] = {
		val player: Player = this.findPlayerByObject(playerObject)
		if(player == null) {
			return Left(RoleNotFound(playerObject.toString, name, null))
		}
		val ret = player.findProperty(name)
		if(ret == null) {
			return Left(SCROLLErrors.IllegalRoleInvocationDispatch(playerObject.toString, name, null))
		}
		Right(ret)
	}

	def putEdge(source: Object, target: Object): Boolean = {
		//println("putEdge:" + source.toString + " " + target.toString)

		var sourcePlayer: Player = this.findPlayerByObject(source)
		val oldTargetPlayer = this.findPlayerByObject(target)

		if(sourcePlayer == null) {
			val newNatural = new Natural()
			newNatural.setObject(source)
			this.graph.addNatural(newNatural)
			this.storeInPlayerObjectCache(newNatural)
			sourcePlayer = newNatural
		}

		val targetPlayer = new Role()
		targetPlayer.setObject(target)

		if(oldTargetPlayer != null) {
			if(sourcePlayer != null && oldTargetPlayer.predecessor == sourcePlayer) {
				return false
			}
			targetPlayer.setRoleList(oldTargetPlayer.getRoleList)
			this.deletePlayer(oldTargetPlayer)
		}

		sourcePlayer.addRole(targetPlayer)
		this.storeInPlayerObjectCache(targetPlayer)
		this.graph.flushTreeCache()
		true
	}

	def removeRole(playerObject: Object, roleObject: Object): Unit = {
		//println("removeRole: player " + playerObject.toString + ", role " + roleObject.toString)
		val player: Player = this.findPlayerByObject(playerObject)
		val playerRole: Player = this.findPlayerByObject(roleObject) // needs to be successor of player. thus playerObjectCache can not be used
		if(player == null || playerRole == null) {
			throw new Exception("playerPlayer == null || playerRole == null")
		}

		//adds unlinked role to root, such that it can still be accessed
		val oldPlayer: Natural = new Natural
		oldPlayer.setObject(playerRole.getObject)
		oldPlayer.setRoleList(playerRole.getRoleList)
		this.graph.addNatural(oldPlayer)
		this.storeInPlayerObjectCache(oldPlayer)

		player.removeRole(playerRole)
		this.graph.flushTreeCache()
	}

	private def deletePlayer(player: Player): Unit = {
		this.playerObjectCache.remove(player.getObject)

		val pred = player.predecessor
		if(pred == null) {
			this.graph.removeNatural(player)
			this.graph.flushAttrCache()
		} else {
			pred.removeRole(player)
			this.graph.flushTreeCache()
		}
	}

	def removePlayer[P <: AnyRef : ClassTag](playerObject: P): Unit = {
		//println("removePlayer: " + playerObject.toString)
		val player: Player = this.findPlayerByObject(playerObject)
		if(player == null) {
			return
		}

		player.getRoleList.forEach(r => {
			val oldPlayer: Natural = new Natural
			oldPlayer.setObject(r.getObject)
			oldPlayer.setRoleList(r.getRoleList)
			this.graph.addNatural(oldPlayer)
			this.storeInPlayerObjectCache(oldPlayer)
		})

		this.deletePlayer(player)
	}

	def containsPlayer(playerObject: AnyRef): Boolean = {
		val player = this.findPlayerByObject(playerObject)
		player != null
	}

	def allPlayers(): Seq[AnyRef] = {
		this.graph.allPlayers()
	}

	def successors(playerObject: AnyRef): util.Set[Object] = {
		val ret = new util.LinkedHashSet[Object]
		val player: Player = this.findPlayerByObject(playerObject)
		if(player != null) {
			player.getRoleList.forEach(f => {
				ret.add(f.getObject)
			})
		}
		ret
	}

	def predecessors(playerObject: AnyRef): Seq[AnyRef] = {
		val player: Player = this.findPlayerByObject(playerObject)
		player.predecessors()
	}
}