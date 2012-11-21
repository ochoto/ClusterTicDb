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
		val htmlFiles = new File(dir).listFiles.filter(_.getName.endsWith(".html"))
		val fichas = htmlFiles map ( parseFile(_) ) 

		val fichasConDatos = fichas filter { case (t,l) => !t.isEmpty }
		fichasConDatos map ( println(_) )
	}

	def parseFile(file: File) = {
		println("Procesando: [" + file.getName + "]")
		
		val doc = Jsoup.parse(file, "UTF-8", "")
		
		val ficha = doc.select("div.fichas").first
		val empresa = ficha.select("h1").first.text.trim
		if (empresa.isEmpty) {
			println("El fichero [" + file.getName + "] esta vacío")
			("",Nil)
		}
		else {
			println("Encontrada empresa: " + empresa)

			val tablas = ficha.select("table.FichaTabla").asScala
			val fichaParseada = for {
				t <- tablas
			}
			yield processTable(t)

			(empresa,fichaParseada)
		}
	}

	//	Titulo, Lista de pares clave/valor
	type Tabla = (String, Map[String,String])

	def processTable(t: Element): Tabla = {
		val tds = t.select("td")
		val titulo = tds.first.text

		val tdsClean = tds.not("td[colspan]").asScala

		val kv = for {
			e <- tdsClean drop(1) grouped(2)
			if (e.size == 2)
			p = (e(0).text, e(1).text)
		} yield p

		(titulo, kv.toMap)
	}
}


