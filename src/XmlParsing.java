import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


public class XmlParsing {

	final public static class AndroidManifest
	{
		public boolean ContainsApplicationName;
		public String ApplicationName;
		public String ApplicationNameAttribute;
		public String PackageName;
		public String PathIconHDPI;
		public String PathIconMDPI;
		public String PathIconLDPI;
	}

	public static void saveManifestXml(String TempPath, AndroidManifest androidManifest) throws SAXException, IOException, ParserConfigurationException, XPathExpressionException, TransformerFactoryConfigurationError, TransformerException
	{
		String inputFile = ConvertSeperatorToOsSpecific(TempPath + "/AndroidManifest.xml");

		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(inputFile));

		// locate the node(s)
		XPath xpath = XPathFactory.newInstance().newXPath();
		NodeList nodes = (NodeList)xpath.evaluate("/manifest/application", doc, XPathConstants.NODESET);

		System.out.println("idx = " + nodes.getLength());

		// make the change
		for (int idx = 0; idx < nodes.getLength(); idx++) {
			nodes.item(idx).getAttributes().getNamedItem("android:label").setNodeValue(androidManifest.ApplicationName);
		}

		// save the result
		Transformer xformer = TransformerFactory.newInstance().newTransformer();
		xformer.transform
		(new DOMSource(doc), new StreamResult(new File(inputFile)));
	}

	public static void saveSettingsXml(String TempPath, Locale locale, String Name, String Value) throws SAXException, IOException, ParserConfigurationException, XPathExpressionException, TransformerFactoryConfigurationError, TransformerException 
	{
		// String inputFile = ConvertSeperatorToOsSpecific(TempPath + "/res/values/strings.xml");
		String inputFile = getStringsResPath(TempPath, locale);
		
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(inputFile));

		// locate the node(s)
		XPath xpath = XPathFactory.newInstance().newXPath();
		NodeList nodes = (NodeList)xpath.evaluate("/resources/string[@name=\"" + Name + "\"]", doc, XPathConstants.NODESET);

		// make the change
		for (int idx = 0; idx < nodes.getLength(); idx++) {
			nodes.item(idx).setTextContent(Value);
		}

		// save the result
		Transformer xformer = TransformerFactory.newInstance().newTransformer();
		xformer.transform
		(new DOMSource(doc), new StreamResult(new File(inputFile)));

	}

	public static AndroidManifest parseManifestXml(String TempPath) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException
	{
		AndroidManifest returnValue = new AndroidManifest();
		returnValue.ContainsApplicationName = false;
		returnValue.ApplicationName = null;
		returnValue.ApplicationNameAttribute = null;
		returnValue.PackageName = null;
		returnValue.PathIconHDPI = null;
		returnValue.PathIconMDPI = null;
		returnValue.PathIconLDPI = null;


		String iconValue = null;
		String labelValue = null;
		String pathPreFix = "/res/";
		String iconname = "icon.png"; // Default value


		File file = new File(ConvertSeperatorToOsSpecific(TempPath + "/AndroidManifest.xml"));
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(file);
		doc.getDocumentElement().normalize();
		NodeList nodeLst = doc.getElementsByTagName("manifest");
		NamedNodeMap attributes = nodeLst.item(0).getAttributes();

		// Set returnValue.PackageName
		returnValue.PackageName = attributes.getNamedItem("package").getNodeValue();

		nodeLst = doc.getElementsByTagName("application");
		attributes = nodeLst.item(0).getAttributes();
		iconValue = attributes.getNamedItem("android:icon").getNodeValue();
		labelValue = attributes.getNamedItem("android:label").getNodeValue();
		if (iconValue.startsWith("@"))
		{
			int i = iconValue.lastIndexOf("/");
			pathPreFix = pathPreFix + iconValue.substring(1, i ).replace("/", System.getProperty("file.separator") ) + System.getProperty("file.separator") ;
			String iconNamePreFix = iconValue.substring(i + 1, iconValue.length());
			iconname = iconNamePreFix + ".png";
		}

		// HD
		String path = TempPath + pathPreFix + iconname;
		if (!FileExists(ConvertSeperatorToOsSpecific(path)))
		{
			path = TempPath + "/res/drawable-hdpi/" + iconname;
			if (!FileExists(ConvertSeperatorToOsSpecific(path)))
			{
				path = TempPath + "/res/drawable/" + iconname;
				if (!FileExists(ConvertSeperatorToOsSpecific(path)))
				{
					path = TempPath + "/res/" + iconname;
					if (!FileExists(ConvertSeperatorToOsSpecific(path)))
					{
						path = null;
					}
				}
			}
		}
		returnValue.PathIconHDPI = path;

		// MD
		path = ConvertSeperatorToOsSpecific(TempPath + "/res/drawable-mdpi/" + iconname);
		returnValue.PathIconMDPI = (FileExists(path)) ? path : null;

		// LD
		path = ConvertSeperatorToOsSpecific(TempPath + "/res/drawable-ldpi/" + iconname);
		returnValue.PathIconLDPI = (FileExists(path)) ? path : null;


		// Get attribute name in stings or use this as name
		if (labelValue.startsWith("@string"))
		{
			returnValue.ContainsApplicationName = false;
			returnValue.ApplicationNameAttribute = labelValue.replace("@string/", "");

			// Retrieve application name from strings.xml
			file = new File(ConvertSeperatorToOsSpecific(TempPath + "/res/values/strings.xml"));
			dbf = DocumentBuilderFactory.newInstance();
			db = dbf.newDocumentBuilder();
			doc = db.parse(file);
			doc.getDocumentElement().normalize();

			XPathFactory xPathFactory = XPathFactory.newInstance();
			XPath xPath = xPathFactory.newXPath();
			String expression = "/resources/string[@name=\"" + returnValue.ApplicationNameAttribute + "\"]";
			XPathExpression xPathExpression = xPath.compile(expression);
			returnValue.ApplicationName = (String) xPathExpression.evaluate(doc, XPathConstants.STRING);
		}
		else
		{
			returnValue.ContainsApplicationName = true;
			returnValue.ApplicationName = labelValue;
			returnValue.ApplicationNameAttribute = null;
		}

		System.out.println("returnValue.ApplicationName = " + returnValue.ApplicationName);
		System.out.println("returnValue.ApplicationNameAttribute = " + returnValue.ApplicationNameAttribute);
		System.out.println("returnValue.PackageName = " + returnValue.PackageName);
		System.out.println("returnValue.PathIconHDPI = " + returnValue.PathIconHDPI);
		System.out.println("returnValue.PathIconLDPI = " + returnValue.PathIconLDPI);
		System.out.println("returnValue.PathIconMDPI = " + returnValue.PathIconMDPI);
		System.out.println("returnValue.ContainsApplicationName = " + returnValue.ContainsApplicationName);

		return returnValue;
	}

	public static String getStringsResPath(String TempPath, Locale locale)
	{
		String inputFile = ConvertSeperatorToOsSpecific(TempPath + "/res/values/strings.xml");

		if (locale.getLanguage() != "en" && locale.getCountry() != "US")
		{
			String coutryStr = (locale.getCountry() != "") ?  "-r" + locale.getCountry() : "";
			System.out.println("coutryStr = " + coutryStr);
			inputFile = ConvertSeperatorToOsSpecific(TempPath + "/res/values-" + locale.getLanguage() + coutryStr + "/strings.xml");
		}
		return inputFile;
	}
	public static Map<String, String> getValuesXml(String TempPath, Locale locale) throws SAXException, IOException, ParserConfigurationException, XPathExpressionException
	{
		Map<String, String> returnValue = new HashMap<String, String>();
		
		String inputFile = getStringsResPath(TempPath, locale);
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(inputFile));

		// locate the node(s)
		XPath xpath = XPathFactory.newInstance().newXPath();
		NodeList nodes = (NodeList)xpath.evaluate("/resources/string", doc, XPathConstants.NODESET);

		System.out.println("idx = " + nodes.getLength());
		System.out.println("locale = " + locale.getDisplayLanguage());

		// make the change
		for (int idx = 0; idx < nodes.getLength(); idx++) {
			returnValue.put(nodes.item(idx).getAttributes().item(0).getNodeValue(), nodes.item(idx).getTextContent());
		}
		
		return returnValue;		
	}
	
	public static boolean FileExists(String FileNameToCheck)
	{
		boolean exists = (new File(FileNameToCheck)).exists();
		return exists;	
	}

	public static String ConvertSeperatorToOsSpecific(String InputPath)
	{
		return InputPath.replace("/", System.getProperty("file.separator")); 
	}
}
