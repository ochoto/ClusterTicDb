package com.github.ochoto

import scala.xml.{Elem, XML}
import scala.xml.factory.XMLLoader
 
import org.ccil.cowan.tagsoup.jaxp.SAXFactoryImpl

object ClusterTicDb  {
	def main(args:Array[String]): Unit = {
	    val file = args(0);
	    val src = scala.io.Source.fromFile(file);
	    val cpa = scala.xml.parsing.ConstructingParser.fromSource(src, false); // fromSource initializes automatically
	    val doc = cpa.document();

	    // let's see what it is
	    val ppr = new scala.xml.PrettyPrinter(80,5);
	    val ele = doc.docElem;
	    Console.println("finished parsing");
	    val out = ppr.format(ele);
	    Console.println(out);
	}

    private val factory = new SAXFactoryImpl()
    def get(): XMLLoader[Elem] = {
        XML.withSAXParser(factory.newSAXParser())
    }
}


