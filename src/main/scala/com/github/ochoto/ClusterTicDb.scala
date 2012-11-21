package com.github.ochoto

import java.io.File

import org.jsoup.Jsoup
import org.jsoup.nodes.{Document, Element}

import scala.collection.JavaConverters._

// Implicit
/* import scala.collection.JavaConversions._ */

object ClusterTicDb  {
	def main(args:Array[String]): Unit = {
		val dir = args(0)
		val htmlFiles = new File(dir).listFiles.filter(_.getName.endsWith("*.html"))

		htmlFiles map ( parseFile(_) ) map ( println(_) )
	}

	def parseFile(file: File) {
		println("Procesando: [" + file.getName + "]")
		
		val doc = Jsoup.parse(file, "UTF-8", "")
		
		val ficha = doc.select("div.fichas").first
		val empresa = ficha.select("h1").first.text.trim
		println("Encontrada empresa: " + empresa)

		val tablas = ficha.select("table.FichaTabla").asScala
		
		val fichaParseada = for {
			t <- tablas
		}
		yield processTable(t)

		println( (empresa,fichaParseada) )
	}

	//	Titulo, Lista de pares clave/valor
	type Tabla = (String, Map[String,String])

	def processTable(t: Element): Tabla = {
		val tds = t.select("td")
		val titulo = tds.first.text
		val kvs = tds.asScala.drop(1).zipWithIndex
		val ks = kvs.filter { case (e,i) => i % 2 == 0 } map (_._1.text)
		val vs = kvs.filter { case (e,i) => i % 2 == 1 } map (_._1.text)
		val kvMap = (ks zip vs).toMap
		(titulo, kvMap)
	}
}


