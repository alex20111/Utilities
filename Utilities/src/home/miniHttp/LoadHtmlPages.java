package home.miniHttp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class LoadHtmlPages {
//
//	private static final Logger logger = Logger.getLogger( LoadHtmlPages.class.getName() );

	public static void main(String args[]) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException, ParserConfigurationException, SAXException, IOException{
		loadPagePartsFromXml("c:\\temp\\wb\\web\\");
	}

	public static Map<String, List<String>> loadPagePartsFromXml(String filePath) throws ParserConfigurationException, SAXException, IOException, IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException{
//		logger.log(Level.CONFIG, "loadRadioFromXml()");

		Map<String, List<String>> htmlPageParts = new HashMap<String, List<String>>();

		File fXmlFile = new File(filePath + "contentLayout.xml");

		if (fXmlFile.exists()){

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();

			NodeList nList = doc.getElementsByTagName("page");

			for (int temp = 0; temp < nList.getLength(); temp++) {

				List<String> pageParts = new ArrayList<String>();

				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {

					Element eElement = (Element) nNode;				 //name of the <page> tag

					if (nNode.hasChildNodes()){
						NodeList nodePagePartList = nNode.getChildNodes();

						for (int child = 0; child < nodePagePartList.getLength(); child++) {
							Node childNode = nodePagePartList.item(child);
							if (childNode.getNodeType() == Node.ELEMENT_NODE) {
								Element childEle = (Element) childNode;	
								pageParts.add(childEle.getAttribute("name"));
							}
						}

						htmlPageParts.put(eElement.getAttribute("name"), pageParts);
					}

				}
			}
		}else{
			throw new FileNotFoundException("contentLayout.xml file not found, please add it to the externalHtmlFolder folder. FilePath: (" + filePath + "). ");
		}
		return htmlPageParts;
	}
}