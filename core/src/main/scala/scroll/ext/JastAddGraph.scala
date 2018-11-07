package src.main.scala.scroll.ext

import java.util

import de.tud.deussen.jastadd.gen.{Edge, Graph, Node}

class JastAddGraph[N] { // extends MutableGraph[N] {

	var graph = new Graph()
	var visited: Array[Boolean] = _
	var stack: Array[Boolean] = _


	def putEdge(source: Object, target: Object): Boolean = {
		println("putEdge:" + source.toString + " " + target.toString)
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
		var graphChanged = false
		println("removeEdge:" + source.toString + " " + target.toString)

		val edges = new util.HashSet[Edge]()
		this.graph.getEdgeList.forEach(edge => {
			if(edge.getSource == source  && edge.getTarget == target) {
				edges.add(edge)
				graphChanged = true
			}
		})
		edges.forEach(edge => {
			this.graph.getEdgeList.removeChild(this.graph.getEdgeList.getIndexOfChild(edge))
		})

		graphChanged
	}

	def removeNode(node: Object): Boolean = {
		println("removeNode: " + node.toString)
		var graphChanged = false

		val nodes = new util.HashSet[Node]()
		this.graph.getNodeList.forEach(n => {
			if(n.getValue == node) {
				nodes.add(n)
				graphChanged = true
			}
		})
		nodes.forEach(n => {
			this.graph.getNodeList.removeChild(this.graph.getNodeList.getIndexOfChild(n))
		})

		val edges = new util.HashSet[Edge]()
		this.graph.getEdgeList.forEach(edge => {
			if(edge.getSource == node || edge.getTarget == node) {
				edges.add(edge)
				graphChanged = true
			}
		})
		edges.forEach(edge => {
			this.graph.getEdgeList.removeChild(this.graph.getEdgeList.getIndexOfChild(edge))
		})

		println("finished removeNode: " + node.toString)
		graphChanged
	}

	private def isCyclic(node: Object): Boolean = {
		if(!this.visited(nodeIndex(node))) {
			this.visited(nodeIndex(node)) = true
			this.stack(nodeIndex(node)) = true

			val it = this.graph.getEdgeList.iterator
			while(it.hasNext) {
				val edge = it.next()
				if(edge.getSource == node) {
					val targetIndex = nodeIndex(edge.getTarget)

					println("targetIndex is " + targetIndex)
					println("visited is " + this.visited.toString)
					println("stack is " + this.stack.toString)
					if(!this.visited(targetIndex) && isCyclic(edge.getTarget))
						return true
					if(this.stack(targetIndex))
						return true
				}
			}
		}
		this.stack(nodeIndex(node)) = false
		false
	}

	private def nodeIndex(node: Object): Integer = {
		this.nodes().toArray().indexOf(node)
	}


	def hasCycle: Boolean = {
		println("hasCycle(): checking cycle...")
		this.visited = (0 to this.graph.getNodeList.getNumChild map (_ => false)).toArray
		this.stack = (0 to this.graph.getNodeList.getNumChild map (_ => false)).toArray

		val it = this.graph.getNodeList.iterator
		while(it.hasNext) {
			val node = it.next()
			if(isCyclic(node.getValue))
				return true
		}
		false
	}

	def edges(): util.Set[Edge] = {
		val someSet = new util.HashSet[Edge]()
		this.graph.getEdgeList.forEach(edge => {
			someSet.add(edge)
			//println("edges(): " + edge.getSource.toString + " " + edge.getTarget.toString)
		})
		someSet
	}

	def nodes(): util.Set[Object] = {
		val nodes = new util.HashSet[Object]()
		this.graph.getNodeList.forEach(node => {
			nodes.add(node.getValue)
			//println("nodes(): " + node.getValue.toString)
		})
		nodes
	}

	def successors(node: Object): util.Set[Object] = {
		//println("successors: " + node.toString)
		val values = new util.HashSet[Object]()
		edges().forEach(edge => {
			if(edge.getSource == node) {
				values.add(edge.getTarget)
			}
		})
		values
	}

	def predecessors(node: Object): util.Set[Object] = {
		val values = new util.HashSet[Object]()
		edges().forEach(edge => {
			if(edge.getTarget == node) {
				values.add(edge.getSource)
			}
		})
		//println("predecessors: " + node.toString + " equals: " + values.toArray.toString)
		values
	}

}