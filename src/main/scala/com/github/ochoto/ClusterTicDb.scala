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

		val fichasConDatos = fichas filter { case (i,t,l) => !t.isEmpty }
		println ("Encontradas [" + fichasConDatos.size + "] fichas.\n")
		fichasConDatos map { case (i,t,l) => viewCompany(i,t,l) }
	}

	def parseFile(file: File): (Int,String, List[Tabla]) = {
		val n = file.getName.replace(".html","").toInt
		//println("Procesando: [" + n + "]")
		
		val doc = Jsoup.parse(file, "ISO-8859-1", "")
		
		val ficha = doc.select("div.fichas").first
		val empresa = ficha.select("h1").first.text.trim
		if (empresa.isEmpty) {
			//println("El fichero [" + file.getName + "] esta vacío")
			(-1,"",Nil)
		}
		else {
			//println("Encontrada empresa: " + empresa)
			val tablas = ficha.select("table.FichaTabla").asScala
			val fichaParseada = for {
					t <- tablas
					pt = processTable(t)
					if (!pt._2.isEmpty)
				}
				yield pt

			(n, empresa, fichaParseada.toList)
		}
	}

	//	Titulo, Lista de pares clave/valor
	type Tabla = (String, Map[String,String])

	//TODO: Los TD con COLSPAN de "Servicio de software" definen una nueva jerarquia
	//TODO: Al repetirse los nombres de claves se recoge únicamente el último valor
	//TODO: Ejemplo: Ficha Anasinf (99)

	def processTable(t: Element): Tabla = {
		val tds = t.select("td")
		val titulo = tds.first.text

		val tdsClean = tds.not("td[colspan]").asScala

		val kv = for {
			e <- tdsClean grouped(2)
			if (e.size == 2)
			p = (e(0).text.replace(":",""), e(1).text)
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

}

