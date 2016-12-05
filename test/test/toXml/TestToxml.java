package test.toXml;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.junit.Test;
import org.w3c.dom.Document;

import ognl.Node;
import test.BaseTest;

public class TestToxml extends BaseTest{
	@Test
    public void testSAX() throws Throwable{  
		 DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	     Document document = builder.parse(new File("E:/workspace/Java-Util/src/util/xml/widgets.xml"));
	     XPath xpath = XPathFactory.newInstance().newXPath();
	     String expression = "/widgets/widget";
	     Node book = (Node) xpath.evaluate(expression, document, XPathConstants.NODE);
//	     System.out.println(book.getNodeName());//节点名称
//	     System.out.println(book.getNodeValue());//节点值是null，为什么？
//	     System.out.println(book.getTextContent());//节点text
    } 

}
