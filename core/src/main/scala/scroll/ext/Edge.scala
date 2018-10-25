package src.main.scala.scroll.ext

import java.util
import java.util.{HashSet, Set}

import com.google.common.graph.EndpointPair;

//import de.tud.deussen.jastadd.gen.{AddExp, MulExp, Number, Root, Var}

/*
class test123 {
	def createAst: Root = {
		println("I am creating AST...")
		val root = new Root()
		val addExp = new AddExp()
		addExp.setA(new Number(0.5f))
		addExp.setB(new Number(1))

		val someVar = new Var("varName")
		val mulExp = new MulExp()
		mulExp.setA(addExp)
		mulExp.setB(someVar)

		root.setExp(mulExp)
		return root
	}
}
*/

class Edge[N]  {
	var source: N = _
	var target: N = _
}

