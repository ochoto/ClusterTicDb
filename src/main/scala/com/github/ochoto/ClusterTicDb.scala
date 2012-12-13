package com.github.ochoto

import java.io.File
import org.jsoup.Jsoup
import org.jsoup.nodes.{Document, Element, Node}
import scala.collection.JavaConverters._
// Implicit
/* import scala.collection.JavaConversions._ */

import java.sql.{Connection, DriverManager, ResultSet}

object ClusterTicDb  {
	def main(args:Array[String]): Unit = {
		val dir = args(0)
		val htmlFiles = new File(dir).listFiles.filter(_.getName.endsWith(".html"))
		val numHtmlFiles = htmlFiles.size
		val filesToParse = if ( args.size > 1)
								args(1).toInt
							else
								numHtmlFiles
		val fichas = htmlFiles take(filesToParse) map ( parseFile(_) )

		val fichasConDatos = fichas filter { case (i,t,l) => !t.isEmpty }
		println("Encontradas [" + fichasConDatos.size + "] fichas.\n")
		
		//fichasConDatos map { case (i,t,l) => viewCompany(i,t,l) }

		fichasConDatos map { case (i,t,l) => csvCompany(i,t,l) }

		//DbManager.query("select * from Empresa")
	}

	def cleanKey(s: String) = {
		s.toLowerCase.filterNot(": -%()".contains(_)).map { c => ("áéíóúñ" zip "aeioun").toMap.getOrElse(c,c) }
	}

	def parseFile(file: File): (Int,String, List[Tabla]) = {
		val n = file.getName.replace(".html","").toInt
		
		val doc = Jsoup.parse(file, "ISO-8859-1", "")
		
		val ficha = doc.select("div.fichas").first
		val empresa = ficha.select("h1").first.text.trim

		if (empresa.isEmpty) 
			return (-1,"",Nil)
		
		// Descomento los comentarios, tienen información interesante
		// No puedo hacerlo mediante map por excepción de modificación concurrente

		revelaComentarios(ficha)

		val tablas = ficha.select("table.FichaTabla").asScala
		val fichaParseada = for {
				t <- tablas
				pt = processTable(t)
				if (!pt._2.isEmpty)
			}
			yield pt

		(n, empresa, fichaParseada.toList)
	}


	//Modifica el arbol DOM

	def revelaComentarios(ficha: Node): Unit = {
		for (i <- 0 to (ficha.childNodes.size-1)) {
			val n = ficha.childNode(i)
			if ( n.nodeName == "#comment") {
				val strippedComments = n.outerHtml.replace("<!--","").replace("-->","")
				n.before( strippedComments ) 
				n.remove
			}
			else {
				revelaComentarios(n)
			}
		}
	}


	//	Titulo, Lista de pares clave/valor
	type Tabla = (String, Map[String,String])

	//TODO: Los TD con COLSPAN de "Servicio de software" definen una nueva jerarquia
	//TODO: Al repetirse los nombres de claves se recoge únicamente el último valor
	//TODO: Ejemplo: Ficha Anasinf (99)
	//TODO: Además hay una ficha específica de software

	def processTable(t: Element): Tabla = {
		val tds = t.select("td")
		val titulo = tds.first.text

		val tdsClean = tds.not("td[colspan]").asScala

		val kv = for {
			e <- tdsClean grouped(2)
			if (e.size == 2)
			ck = cleanKey(e(0).text)
			p = (ck, e(1).text)
		} yield p

		val kvMap = kv filter { case (k,v) => !v.isEmpty } toMap

		(titulo, kvMap)
	}

	def viewCompany(id: Int, company: String, data: List[Tabla]) {
		println("vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv")
		println(id.toString + ": " + company + "\n")
		data map viewTable
		println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^")
	}

	def viewTable(t: Tabla) = t match { case (tname, tmap) =>
		println("############ " + tname + " ###########")
		tmap map { case (k,v) => println("\t" + k + ": " + v ) }
	}


	def csvCompany(id: Int, company: String, data: List[Tabla]) {
		println(csvTable(data.head))
	}

	def csvTable(t: Tabla) = t match { 
		case (tname, tmap) => {
			def m(s: String) = tmap.getOrElse(s,"#" + s.toUpperCase + "#").replace("\"","'")
			val l = List(m("razonsocial"),m("cif"),m("denominacioncomercial"),m("email"),
						 m("personadecontactocomercial"),m("paginaweb"),m("telefono"),m("poblacion"),
						 m("direccion"),m("codigopostal"),m("anodeconstitucion"),
						 m("descripciondelaactividaddelaempresa"),m("numerodetrabajadores"),
						 m("asociacionesoentidadesalasquepertenece")
					 );

			l.map('"' + _ + '"').mkString(",")
		}
	}
}


/*
object DbManager {
	classOf[com.mysql.jdbc.Driver]	// Load the driver
	val conn_str = "jdbc:mysql://localhost:3306/ClusterTic?user=clustertic&password=clustertic"
	val conn = DriverManager.getConnection(conn_str)
	val queryStmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)
	val dmlStmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)

	def query(sql: String) {
		val rs = queryStmt.executeQuery(sql)
	}

	def dml(sql: String) {
		val rs = dmlStmt.executeQuery(sql)
	}
}

*/