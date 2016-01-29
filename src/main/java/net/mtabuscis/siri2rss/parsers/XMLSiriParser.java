package net.mtabuscis.siri2rss.parsers;

import java.io.IOException;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import net.mtabuscis.siri2rss.input.services.SiriParserService;
import net.mtabuscis.siri2rss.model.SiriPayload;

@Component
public class XMLSiriParser implements SiriParserService {

	private static Logger _log = LoggerFactory.getLogger(XMLSiriParser.class);
	
	public boolean isHandler(String data){
		DocumentBuilderFactory dbFactory=DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();//first catch
			InputSource is = new InputSource(new StringReader(data));
			dBuilder.parse(is);//second catch
		} catch (ParserConfigurationException e) {
			_log.info("XML Parser is the wrong parser");
			return false;
		} catch (SAXException | IOException e) {
			e.printStackTrace();
			_log.info("XML Parser is the wrong parser");
			return false;
		}
		_log.info("XML Parser is the correct parser");
		return true;
	}

	public List<SiriPayload> parse(String xmlString){
		DocumentBuilderFactory dbFactory=DocumentBuilderFactory.newInstance();
		Document doc = null;
		try {
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			InputSource is = new InputSource(new StringReader(xmlString));
			doc = dBuilder.parse(is);//second catch
		} catch (ParserConfigurationException | SAXException | IOException e) {//caught previously
			e.printStackTrace();
			return null;
		}
		doc.getDocumentElement().normalize();
		//Open the information that is important for the RSS feed.
		NodeList nList=doc.getElementsByTagName("PtSituationElement");
		List<SiriPayload> outSP = new ArrayList<SiriPayload>();
		for (int i=0; i<nList.getLength();i++){
			Node nNode = nList.item(i);
			SiriPayload p = xmlNodeToPayload(nNode);
			if(p != null){
				outSP.add(p);
			}
		}
		return outSP;
	}
	
	private SiriPayload xmlNodeToPayload(Node nNode){
		if (nNode.getNodeType() == Node.ELEMENT_NODE) {
			//Gut the information from the XML file.
			Element eElement = (Element) nNode;
			SiriPayload entry=new SiriPayload();
			entry.setTitle("Alert: " + eElement.getElementsByTagName("ReasonName").item(0).getTextContent());
			entry.setComments("Affected Routes: " + eElement.getElementsByTagName("VehicleJourneys").item(0).getTextContent());
			entry.setIdentification(eElement.getElementsByTagName("SituationNumber").item(0).getTextContent());
			
			//Implements some logic for the dates
			//First, see if there is a start date for the alert.
			if (eElement.getElementsByTagName("StartTime").item(0)!=null){
				String date=eElement.getElementsByTagName("StartTime").item(0).getTextContent();
				entry.setCreatedDate(stringToDate(date));
			}
			//If this fails, then take the creation time listed in SIRI.
			else{
				String date=eElement.getElementsByTagName("CreationTime").item(0).getTextContent();
				entry.setCreatedDate(stringToDate(date));
			}
			
			//TODO: for the date issue being wrong timezone, you need to set timezone correctly then format it in another timezone
			//Java is tricky with timezones
			
			//If there is an end time listed, read it in and add it to the comments.
			if (eElement.getElementsByTagName("EndTime").item(0)!=null){
				String stopDate=eElement.getElementsByTagName("EndTime").item(0).getTextContent();
				Date alertEnds = stringToDate(stopDate);
				entry.setSummary(eElement.getElementsByTagName("Description").item(0).getTextContent()+" Alert ends at: "+alertEnds);
			}else{
				entry.setSummary(eElement.getElementsByTagName("Description").item(0).getTextContent());
			}
			
			return entry;
		}
		return null;
	}
	
	//Jake: no need for static functions...
	private Date stringToDate(String original){
		String toParse=original.substring(0,10);
		String time=original.substring(11,19);
		toParse=toParse+" "+time;
		Date result=new Date();
		
		//Parse the date from Siri
		DateFormat df=new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
		
		//df.setTimeZone(fix);
		
		try {
			result=df.parse(toParse);
		} catch (ParseException e) {//We don't blindly catch exceptions
			e.printStackTrace();
			return null;
		}
		
		return result;
	}
	
}
