package com.mauvaisetroupe.eadesignit.service.drawio;

import com.mauvaisetroupe.eadesignit.service.drawio.dto.Point;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class PLantumlToDrawioPositioner {

    // public static void main(String[] args) throws Exception {

    //     PLantumlToDrawioPositioner app = new PLantumlToDrawioPositioner();

    //     ClassLoader classLoader = PLantumlToDrawioPositioner.class.getClassLoader();
    //     File svgFile = new File(classLoader.getResource("svg-formatted.svg").getFile());
    //     Document svgDoc = createDocFromFile(svgFile);
    //     Map<String, Point> pointFromSVGMap = app.getPointFromSVG(svgDoc);

    //     File drawioFile = new File(classLoader.getResource("draw-io.xml").getFile());
    //     Document drawioDoc = createDocFromFile(drawioFile);
    //     app.addPosition(drawioDoc, pointFromSVGMap);

    //     FileOutputStream output = new FileOutputStream("c:\\workspaces\\staff-dom.xml");
    //     writeXml(drawioDoc, output);

    // }

    public Document addPositions(Document drawDocument, String svgXML) {
        if (svgXML == null) return drawDocument;

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        Document copiedDocument;
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Node originalRoot = drawDocument.getDocumentElement();
            copiedDocument = db.newDocument();
            Node copiedRoot = copiedDocument.importNode(originalRoot, true);
            copiedDocument.appendChild(copiedRoot);
        } catch (ParserConfigurationException e) {
            return drawDocument;
        }

        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document docSvgXML = db.parse(new InputSource(new StringReader(svgXML)));

            Map<String, Point> pointFromSVGMap = getPointFromSVG(docSvgXML);
            addPositions(copiedDocument, pointFromSVGMap);
            return copiedDocument;
        } catch (XPathExpressionException | ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
            return drawDocument;
        }
    }

    private void addPositions(Document doc, Map<String, Point> pointFromSVGMap) throws XPathExpressionException {
        XPathFactory xpathfactory = XPathFactory.newInstance();
        XPath xpath = xpathfactory.newXPath();
        NodeList nodeList = (NodeList) xpath.evaluate(
            "//mxCell[starts-with(@elementId,'" + MXFileSerializer.APP_ID_PREFIX + "')]",
            doc,
            XPathConstants.NODESET
        );
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element mxcellElement = ((Element) nodeList.item(i));
            String applicationName = mxcellElement.getAttribute("value");
            Point position = pointFromSVGMap.get(applicationName);

            NodeList textNodeList = mxcellElement.getElementsByTagName("mxGeometry");
            Element mxGeometryElement = extractFirstChild(textNodeList);
            if (mxGeometryElement != null && position != null) {
                mxGeometryElement.setAttribute("x", position.getX());
                mxGeometryElement.setAttribute("y", position.getY());
            }
        }
    }

    // private static Document createDocFromFile(File file)
    //         throws ParserConfigurationException, SAXException, IOException {
    //     DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    //     DocumentBuilder db = factory.newDocumentBuilder();
    //     Document doc = db.parse(file);
    //     return doc;
    // }

    private Map<String, Point> getPointFromSVG(Document doc) throws XPathExpressionException {
        Map<String, Point> map = new HashMap<>();

        XPathFactory xpathfactory = XPathFactory.newInstance();
        XPath xpath = xpathfactory.newXPath();

        // <g id="elem_Trust Service">
        // <rect fill="#F1F1F1" height="46.2969" rx="2.5" ry="2.5"
        // style="stroke:#181818;stroke-width:0.5;" width="124" x="2986.5" y="463.543"/>
        // <text fill="#000000" font-family="sans-serif" font-size="14"
        // lengthAdjust="spacing" textLength="94" x="3001.5"
        // y="491.5381">TrustService</text>
        // </g>

        // Find all groups <g>
        NodeList nodeList = (NodeList) xpath.evaluate("//g", doc, XPathConstants.NODESET);
        populateMap(map, nodeList);
        // Find all groups <a>
        nodeList = (NodeList) xpath.evaluate("//a", doc, XPathConstants.NODESET);
        System.out.println(map);
        return map;
    }

    private void populateMap(Map<String, Point> map, NodeList nodeList) {
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element groupelement = ((Element) nodeList.item(i));
            String idValue = groupelement.getAttribute("id");

            NodeList textNodeList = groupelement.getElementsByTagName("text");
            Element textElement = extractFirstChild(textNodeList);
            String applicationName = textElement.getTextContent();

            NodeList rectNodeList = groupelement.getElementsByTagName("rect");
            Element rectElement = extractFirstChild(rectNodeList);
            if (rectElement != null) {
                String _x = rectElement.getAttribute("x");
                String _y = rectElement.getAttribute("y");
                map.put(applicationName, new Point(_x, _y));
            }
        }
    }

    private static Element extractFirstChild(NodeList nodeList) {
        if (nodeList != null && nodeList.getLength() > 0) {
            Element element = (Element) nodeList.item(0);
            return element;
        }
        return null;
    }
}