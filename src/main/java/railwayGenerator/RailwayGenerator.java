package railwayGenerator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Document;

import java.util.Random;

public class RailwayGenerator 
{
//	ArrayList<ArrayList<Element>> aclivesArr = new ArrayList<ArrayList<Element>>();
//	ArrayList<ArrayList<Element>> declivesArr = new ArrayList<ArrayList<Element>>();
//	ArrayList<ArrayList<Element>> planosArr = new ArrayList<ArrayList<Element>>();
//	ArrayList<Element> novaViaArr = new ArrayList<Element>();

	public void execute(RailwayGeneratorRequest req) throws Exception 
	{
		org.jsoup.nodes.Document doc = Jsoup.parse(new File(req.getFilePath()));

		ArrayList<Element> retas = new ArrayList<Element>();
//		ArrayList<ArrayList<Element>> cristas = new ArrayList<ArrayList<Element>>();
		ArrayList<Element> curvas = new ArrayList<Element>();
		
		ArrayList<Element> aclivesArr = new ArrayList<Element>();
		ArrayList<Element> declivesArr = new ArrayList<Element>();
		ArrayList<Element> planosArr = new ArrayList<Element>();
		ArrayList<Element> cristasArr = new ArrayList<Element>();


		Elements listaDePontos = doc.getElementsByTag("listaDePontosDeMedida").first()
				.getElementsByTag("pontoDeMedida");
		

		for (Element ponto : listaDePontos) {
			Elements raioCurva = ponto.getElementsByTag("raioCurva");
			Elements quantidadeRampas = ponto.getElementsByTag("rampa");
			double grauRampa = Double.parseDouble(ponto.getElementsByTag("rampa").first().text());

			if (raioCurva.size() > 1) {
				curvas.add(ponto);
			} else {
				retas.add(ponto);
			}
			
			if(quantidadeRampas.size() > 1) {
				cristasArr.add(ponto);
			} else if(grauRampa > 0) {
				aclivesArr.add(ponto);
			} else if(grauRampa < 0) {
				declivesArr .add(ponto);
			} else {
				planosArr.add(ponto);
			}
			
			
		}
		System.out.println("Quantidade de retas: " + retas.size());
		System.out.println("Quantidade de curvas: " + curvas.size());
		
//		generatePerfisVerticais(retas);
		
		System.out.println("");
		System.out.println("Quantidade de aclives: " + aclivesArr.size());
		System.out.println("Quantidade de declives: " + declivesArr.size());
		System.out.println("Quantidade de planos: " + planosArr.size());
		System.out.println("Quantidade de cristas: " + cristasArr.size());
		
		ArrayList<Element> testeAclive = generateAclives(aclivesArr);
		System.out.println(testeAclive);
		
//		writeToFile(retas, curvas, aclivesArr, declivesArr, planosArr, req);
		
		System.out.println("Via gerada.");
	}

	public void writeToFile(ArrayList<Element> retas, ArrayList<Element> curvas, 
			ArrayList<ArrayList<Element>> aclivesArr, ArrayList<ArrayList<Element>> declivesArr,
			ArrayList<ArrayList<Element>> planosArr, RailwayGeneratorRequest req)
			throws Exception 
	{
//		if (req.getQtdCurvas() > curvas.size()) {
//			throw new Exception("Quantidade de curvas maior do que o valor existente: " + curvas.size());
//		}
//
//		if (req.getQtdRetas() > retas.size()) {
//			throw new Exception("Quantidade de retas maior do que o valor existente: " + retas.size());
//		}
		
		if (req.getQtdAclives() > aclivesArr.size()) {
			throw new Exception("Quantidade de aclives maior do que o valor existente: " + aclivesArr.size());
		}
		
		if (req.getQtdDeclives() > declivesArr.size()) {
			throw new Exception("Quantidade de declives maior do que o valor existente: " + declivesArr.size());
		}
		
		if (req.getQtdPlanos() > planosArr.size()) {
			throw new Exception("Quantidade de planos maior do que o valor existente: " + planosArr.size());
		}

		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

		Document doc = docBuilder.newDocument();
		org.w3c.dom.Element rootElement = doc.createElement("viaFerrea");
		doc.appendChild(rootElement);
		
		org.w3c.dom.Element listaDePontosDeMedida = writeDefaultTags(doc, rootElement);

		ArrayList<ArrayList<Element>> novaVia = generateNewArray(retas, curvas, aclivesArr, declivesArr, planosArr, req);
		
		for(int i = 0; i < novaVia.size(); i++)
		{
			writePontoDeMedida(novaVia.get(i), listaDePontosDeMedida, doc, Integer.toString(i + 1));
		}
			
		
		try (FileOutputStream output = new FileOutputStream("/Users/inkt/dev/tcc/railwayGenerator/src/test/resources/via.xml")) {
			writeXml(doc, output);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public org.w3c.dom.Element writeDefaultTags(Document doc, org.w3c.dom.Element rootElement)
	{
		org.w3c.dom.Element identificador = doc.createElement("identificador");
	    identificador.setTextContent("Preencher nome");
	    rootElement.appendChild(identificador);
	    
		org.w3c.dom.Element distanciaPonto = doc.createElement("distanciaPonto");
		distanciaPonto.setTextContent("20");
	    rootElement.appendChild(distanciaPonto);
	    
		org.w3c.dom.Element bitolaLinha = doc.createElement("bitolaLinha");
		bitolaLinha.setTextContent("1.60");
	    rootElement.appendChild(bitolaLinha);
	    
		org.w3c.dom.Element velocidadeMedia = doc.createElement("velocidadeMedia");
		velocidadeMedia.setTextContent("100");
	    rootElement.appendChild(velocidadeMedia);
	    
		org.w3c.dom.Element listaDePontosDeMedida = doc.createElement("listaDePontosDeMedida");
	    rootElement.appendChild(listaDePontosDeMedida);
	    
	    return listaDePontosDeMedida;
	}
	
	public void writePontoDeMedida(ArrayList<Element> pontosArr, org.w3c.dom.Element listaDePontosDeMedida, Document doc, String id)
	{
		for (Element ponto : pontosArr) {
			org.w3c.dom.Element pontoDeMedida = doc.createElement("pontoDeMedida");
			listaDePontosDeMedida.appendChild(pontoDeMedida);
			
			org.w3c.dom.Element idEl = doc.createElement("id");
			idEl.setTextContent(id);
			pontoDeMedida.appendChild(idEl);
			
			org.w3c.dom.Element velocidadeMax = doc.createElement("velocidadeMax");
			velocidadeMax.setTextContent(ponto.getElementsByTag("velocidadeMax").first().text());
			pontoDeMedida.appendChild(velocidadeMax);
			
			org.w3c.dom.Element km = doc.createElement("km");
			km.setTextContent(ponto.getElementsByTag("km").first().text());
			pontoDeMedida.appendChild(km);
			
			try {
				org.w3c.dom.Element rampa = doc.createElement("rampa");
				rampa.setAttribute("fim", ponto.getElementsByTag("rampa").first().attr("fim"));
				rampa.setAttribute("ini", ponto.getElementsByTag("rampa").first().attr("ini"));
				rampa.setTextContent(ponto.getElementsByTag("rampa").first().text());
				pontoDeMedida.appendChild(rampa);
			} catch (Exception e) {}
	
			
			org.w3c.dom.Element raioCurva = doc.createElement("raioCurva");
			raioCurva.setAttribute("fim", ponto.getElementsByTag("raioCurva").first().attr("fim"));
			raioCurva.setAttribute("ini", ponto.getElementsByTag("raioCurva").first().attr("ini"));
			raioCurva.setTextContent(ponto.getElementsByTag("raioCurva").first().text());
			pontoDeMedida.appendChild(raioCurva);
			
			org.w3c.dom.Element ac = doc.createElement("ac");
			ac.setAttribute("fim", ponto.getElementsByTag("ac").first().attr("fim"));
			ac.setAttribute("ini", ponto.getElementsByTag("ac").first().attr("ini"));
			ac.setTextContent(ponto.getElementsByTag("ac").first().text());
			pontoDeMedida.appendChild(ac);
			
			org.w3c.dom.Element g20 = doc.createElement("g20");
			g20.setAttribute("fim", ponto.getElementsByTag("g20").first().attr("fim"));
			g20.setAttribute("ini", ponto.getElementsByTag("g20").first().attr("ini"));
			g20.setTextContent(ponto.getElementsByTag("g20").first().text());
			pontoDeMedida.appendChild(g20);
			
			org.w3c.dom.Element altitude = doc.createElement("altitude");
			altitude.setAttribute("fim", ponto.getElementsByTag("altitude").first().attr("fim"));
			altitude.setAttribute("ini", ponto.getElementsByTag("altitude").first().attr("ini"));
			altitude.setTextContent(ponto.getElementsByTag("altitude").first().text());
			pontoDeMedida.appendChild(altitude);
			
			org.w3c.dom.Element localizacao = doc.createElement("localizacao");
			pontoDeMedida.appendChild(localizacao);
			
			org.w3c.dom.Element latitude = doc.createElement("latitude");
			latitude.setTextContent(ponto.getElementsByTag("localizacao").first().getElementsByTag("latitude").first().text());
			localizacao.appendChild(latitude);
			
			org.w3c.dom.Element longitude = doc.createElement("longitude");
			longitude.setTextContent(ponto.getElementsByTag("localizacao").first().getElementsByTag("longitude").first().text());
			localizacao.appendChild(longitude);
		}
	}

	private static void writeXml(Document doc, OutputStream output) throws TransformerException 
	{
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(output);

		transformer.transform(source, result);
	}
	
	public ArrayList<ArrayList<Element>> generateNewArray(ArrayList<Element> retas, ArrayList<Element> curvas, 
			ArrayList<ArrayList<Element>> aclivesArr, ArrayList<ArrayList<Element>> declivesArr,
			ArrayList<ArrayList<Element>> planosArr, RailwayGeneratorRequest req)
	{
		ArrayList<ArrayList<Element>> arr = new ArrayList<ArrayList<Element>>();
		
		Random random = new Random();
		
		for(int i = 0; i < req.getQtdAclives(); i++) {
			arr.add(aclivesArr.get(random.nextInt(declivesArr.size())));
		}
		
		for(int i = 0; i < req.getQtdDeclives(); i++) {
			arr.add(declivesArr.get(random.nextInt(declivesArr.size())));
		}
		
		for(int i = 0; i < req.getQtdPlanos(); i++) {
			arr.add(planosArr.get(random.nextInt(planosArr.size())));
		}
		
//		for(int i = 0; i < req.getQtdRetas(); i++)
//		{
//			arr.add(retas.get(random.nextInt(retas.size())));
//		}
		
//		for(int i = 0; i < req.getQtdCurvas(); i++)
//		{
//			arr.add(curvas.get(random.nextInt(curvas.size())));
//		}
		
		Collections.shuffle(arr);
		
		return arr;
	}
	
	private ArrayList<Element> generateAclives(ArrayList<Element> aclivesArr) {
		Element tmpAclivesEl = null;
		ArrayList<Element> finalAclivesArr = new ArrayList<Element>();
		String rampaAnterior = "";

		
		for (Element ponto : aclivesArr) {
			String grauRampa = ponto.getElementsByTag("rampa").first().text();
			
			if(rampaAnterior.equals("")) {
				rampaAnterior = grauRampa;
				tmpAclivesEl = ponto;
				continue;
			}
			
			if(grauRampa.equals(rampaAnterior)) {
				ponto.appendTo(tmpAclivesEl);
				rampaAnterior = grauRampa;
				System.out.println(tmpAclivesEl);
			} else {
				finalAclivesArr.add(tmpAclivesEl);
//				tmpAclivesEl = null;
				tmpAclivesEl = ponto;
//				ponto.appendTo(tmpAclivesEl);
				rampaAnterior = grauRampa;
			}
		}
		
		return finalAclivesArr;
	}
	
//	public void generatePerfisVerticais(ArrayList<Element> retas) {
//		String rampaBase = "";
//		ArrayList<Element> pmArr = new ArrayList<Element>();
//		
//		for (Element pm : retas) {
//			String grauRampa = pm.getElementsByTag("rampa").first().text();
//			
//			if(rampaBase.equals("")) {
//				rampaBase = grauRampa;
//			}
//			
//			if(grauRampa.equals(rampaBase)) {
//				pmArr.add(pm);
//			} else {
//				String perfilVertical = verifyPerfilVertical(rampaBase);
//				
//				switch (perfilVertical) {
//				case "ACLIVE":
//					aclivesArr.add(pmArr);
//					break;
//				case "DECLIVE":
//					declivesArr.add(pmArr);
//					break;
//				case "PLANO":
//					planosArr.add(pmArr);
//					break;
//				default:
//					break;
//				}
//				pmArr.clear();
//				
//				rampaBase = grauRampa;
//				pmArr.add(pm);
//			}
//		}
//	}
	
	public static String verifyPerfilVertical(String pm) {
		if(pm.startsWith("-")) {
			return "DECLIVE";
		} else if(pm.equals("0.0")){
			return "PLANO";
		} else {
			return "ACLIVE";
		}
	}
}
