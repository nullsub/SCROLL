package src.main.scala.scroll.ext

import java.util

import de.tud.deussen.jastadd.gen.{ASTNode, Edge, Graph, Node}

class JastAddGraphOld[N] { // extends MutableGraph[N] {
	var graph = new Graph()

	def putEdge(source: Object, target: Object): Boolean = {
		//println("putEdge:" + source.toString + " " + target.toString)
		var graphChanged = false
		if(!this.nodes().contains(source)) {
			val node = new Node()
			node.setValue(source)
			this.graph.addNode(node)
			graphChanged = true
		}
		if(!this.nodes().contains(target)) {
			val node = new Node()
			node.setValue(target)
			this.graph.addNode(node)
			graphChanged = true
		}

		val it = this.graph.getEdgeList.iterator
		while(it.hasNext) {
			val edge = it.next()
			if(edge.getSource == source && edge.getTarget == target)
				return graphChanged
		}

		val newEdge = new Edge()
		newEdge.setSource(source)
		newEdge.setTarget(target)
		this.graph.addEdge(newEdge)

		true
	}

	def removeEdge(source: Object, target: Object): Boolean = {
		//println("removeEdge:" + source.toString + " " + target.toString)

		var graphChanged = false
		val edges = new util.HashSet[Edge]()
		this.graph.getEdgeList.forEach(edge => {
			if(edge.getSource == source && edge.getTarget == target) {
				edges.add(edge)
				graphChanged = true
			}
		})
		edges.forEach(edge => {
			this.graph.getEdgeList.removeChild(this.graph.getEdgeList.getIndexOfChild(edge.asInstanceOf[ASTNode[ASTNode[_ <: AnyRef]]]))
		})

		graphChanged
	}

	def removeNode(node: Object): Boolean = {
		//println("removeNode: " + node.toString)
		var graphChanged = false

		val edges = new util.HashSet[Edge]()
		this.graph.getEdgeList.forEach(edge => {
			if(edge.getSource == node || edge.getTarget == node) {
				edges.add(edge)
				graphChanged = true
			}
		})
		edges.forEach(edge => {
			this.graph.getEdgeList.removeChild(this.graph.getEdgeList.getIndexOfChild(edge.asInstanceOf[ASTNode[ASTNode[_ <: AnyRef]]]))
		})


		val nodes = new util.HashSet[Node]()
		this.graph.getNodeList.forEach(n => {
			if(n.getValue == node) {
				nodes.add(n)
				graphChanged = true
			}
		})
		nodes.forEach(n => {
			this.graph.getNodeList.removeChild(this.graph.getNodeList.getIndexOfChild(n.asInstanceOf[ASTNode[ASTNode[_ <: AnyRef]]]))
		})

		graphChanged
	}

	private def isCyclic(node: Object, visited: Array[Boolean], finished: Array[Boolean]): Boolean = {

		if(finished(nodeIndex(node)))
			return false
		if(visited(nodeIndex(node)))
			return true

		visited(nodeIndex(node)) = true

		val it = this.graph.getEdgeList.iterator
		while(it.hasNext) {
			val edge = it.next()
			if(edge.getSource == node) {
				if(isCyclic(edge.getTarget, visited, finished))
					return true
			}
		}
		finished(nodeIndex(node)) = true
		false
	}

	private def nodeIndex(node: Object): Integer = {
		nodes().toArray().indexOf(node)
	}

	def hasCycle: Boolean = {
		//println("hasCycle()")

		val visited: Array[Boolean] = (0 to this.graph.getNodeList.getNumChild map (_ => false)).toArray
		val finished: Array[Boolean] = (0 to this.graph.getNodeList.getNumChild map (_ => false)).toArray

		val it = this.graph.getNodeList.iterator
		while(it.hasNext) {
			val node = it.next()
			if(isCyclic(node.getValue, visited, finished))
				return true
		}
		false
	}

	def edges(): util.Set[Edge] = {
		val someSet = new util.LinkedHashSet[Edge]()
		this.graph.getEdgeList.forEach(edge => {
			someSet.add(edge)
		})
		someSet
	}

	def nodes(): util.Set[Object] = {
		val nodes = new util.LinkedHashSet[Object]()
		this.graph.getNodeList.forEach(node => {
			nodes.add(node.getValue)
		})
		nodes
	}

	def successors(node: Object): util.Set[Object] = {
		val values = new util.LinkedHashSet[Object]()
		edges().forEach(edge => {
			if(edge.getSource == node) {
				values.add(edge.getTarget)
			}
		})
		//println("successors: " + node.toString + " equals: " + values.toArray.toString)
		values
	}

	def predecessors(node: Object): util.Set[Object] = {
		val values = new util.LinkedHashSet[Object]()
		edges().forEach(edge => {
			if(edge.getTarget == node) {
				values.add(edge.getSource)
			}
		})
		//println("predecessors: " + node.toString + " equals: " + values.toArray.toString)
		values
	}
}