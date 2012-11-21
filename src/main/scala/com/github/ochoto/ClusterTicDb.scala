package com.github.ochoto

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.File

import scala.collection.JavaConverters._

// Implicit
/* import scala.collection.JavaConversions._ */

object ClusterTicDb  {
	def main(args:Array[String]): Unit = {
	    val file = args(0);
	    println("Working with [" + file + "]")
	    val input = new File(file);
		val doc = Jsoup.parse(input, "UTF-8", "")
		val anchors = doc.select("a").asScala
		for {
			a <- anchors
			if (!a.text.trim.isEmpty)
		} println(a.text)
	}
}


