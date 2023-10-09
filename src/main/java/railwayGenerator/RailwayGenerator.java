package railwayGenerator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

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

public class RailwayGenerator {
	
	int id = 1;
	int km = 240;
	int countPontos = 1;
	
	public void execute(RailwayGeneratorRequest req) throws Exception {
		org.jsoup.nodes.Document doc = Jsoup.parse(new File(req.getFilePath()));

//		ArrayList<Element> retas = new ArrayList<Element>();
//		ArrayList<Element> curvas = new ArrayList<Element>();

		ArrayList<Element> aclivesArr = new ArrayList<Element>();
		ArrayList<Element> declivesArr = new ArrayList<Element>();
//		ArrayList<Element> planosArr = new ArrayList<Element>();
//		ArrayList<Element> cristasArr = new ArrayList<Element>();

		Elements listaDePontos = doc.getElementsByTag("listaDePontosDeMedida").first()
				.getElementsByTag("pontoDeMedida");

		for (Element ponto : listaDePontos) {
//			Elements raioCurva = ponto.getElementsByTag("raioCurva");
//			Elements quantidadeRampas = ponto.getElementsByTag("rampa");
			double grauRampa = Double.parseDouble(ponto.getElementsByTag("rampa").first().text());

//			if (raioCurva.size() > 1) {
//				curvas.add(ponto);
//			} else {
//				retas.add(ponto);
//			}

//			if (quantidadeRampas.size() > 1) {
//				cristasArr.add(ponto);
//			} else 
			
			if (grauRampa >= 0) {
				aclivesArr.add(ponto);
			} else if (grauRampa < 0) {
				declivesArr.add(ponto);
			} 
//			else if (grauRampa == 0.0){
//				planosArr.add(ponto);
//			}

		}
		System.out.println("Quantidade de aclives: " + aclivesArr.size());
		System.out.println("Quantidade de declives: " + declivesArr.size());
//		System.out.println("Quantidade de planos: " + planosArr.size());
//		System.out.println("Quantidade de cristas: " + cristasArr.size());

		ArrayList<Element> aclives = generatePms(aclivesArr);
		ArrayList<Element> declives = generatePms(declivesArr);
//		ArrayList<Element> planos = generatePms(planosArr);
//		ArrayList<Element> cristas = new ArrayList<Element>();
		
		System.out.println("Quantidade de trechos de aclive: " + aclives.size());
		System.out.println("Quantidade de trechos de declive: " + declives.size());

		writeToFile(aclives, declives, null, null, req);
		
		System.out.println("Via gerada.");
	}

	public void writeToFile(ArrayList<Element> aclives, ArrayList<Element> declives, ArrayList<Element> planos,
			ArrayList<Element> cristas, RailwayGeneratorRequest req) throws Exception {
//		if (req.getQtdCurvas() > curvas.size()) {
//			throw new Exception("Quantidade de curvas maior do que o valor existente: " + curvas.size());
//		}
//
//		if (req.getQtdRetas() > retas.size()) {
//			throw new Exception("Quantidade de retas maior do que o valor existente: " + retas.size());
//		}

		if (req.getQtdAclives() > aclives.size()) {
			throw new Exception("Quantidade de aclives maior do que o valor existente: " + aclives.size());
		}

		if (req.getQtdDeclives() > declives.size()) {
			throw new Exception("Quantidade de declives maior do que o valor existente: " + declives.size());
		}

//		if (req.getQtdPlanos() > planos.size()) {
//			throw new Exception("Quantidade de planos maior do que o valor existente: " + planos.size());
//		}

//		if (req.getQtdCristas() > cristas.size()) {
//			throw new Exception("Quantidade de cristas maior do que o valor existente: " + cristas.size());
//		}

		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

		Document doc = docBuilder.newDocument();
		org.w3c.dom.Element rootElement = doc.createElement("viaFerrea");
		doc.appendChild(rootElement);

		org.w3c.dom.Element listaDePontosDeMedida = writeDefaultTags(doc, rootElement);

		ArrayList<Element> novaVia = generateNewArray(aclives, declives, planos, cristas, req);

		
//		for (Element trecho : novaVia) {
//			writePontoDeMedida(trecho, listaDePontosDeMedida, doc, novaVia);
//			
//			novaVia.remove(trecho);
//		}
		for (int i = 0; i < novaVia.size(); i++) {
			writePontoDeMedida(novaVia.get(i), listaDePontosDeMedida, doc, novaVia);
			novaVia.remove(novaVia.get(i));
		}

		try (FileOutputStream output = new FileOutputStream(
				"/Users/inkt/dev/tcc/railwayGenerator/src/test/resources/via.xml")) {
			writeXml(doc, output);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public org.w3c.dom.Element writeDefaultTags(Document doc, org.w3c.dom.Element rootElement) {
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

	public void writePontoDeMedida(Element pmLista, org.w3c.dom.Element listaDePontosDeMedida, Document doc, ArrayList<Element> novaVia) {

		Elements listaPontos = pmLista.getElementsByTag("listaDePontosDeMedida").first().getElementsByTag("pontoDeMedida");
		

		for (Element ponto : listaPontos) {
			org.w3c.dom.Element pontoDeMedida = doc.createElement("pontoDeMedida");
			listaDePontosDeMedida.appendChild(pontoDeMedida);

			org.w3c.dom.Element idEl = doc.createElement("id");
			idEl.setTextContent(Integer.toString(id));
			pontoDeMedida.appendChild(idEl);
			id++;

			org.w3c.dom.Element velocidadeMax = doc.createElement("velocidadeMax");
			velocidadeMax.setTextContent(ponto.getElementsByTag("velocidadeMax").first().text());
			pontoDeMedida.appendChild(velocidadeMax);
			
			
			if(countPontos > 50) {
				countPontos = 1;
				km++;
			}
			countPontos++;
			
			org.w3c.dom.Element km = doc.createElement("km");
			km.setTextContent(km.toString());
			pontoDeMedida.appendChild(km);

			org.w3c.dom.Element rampa = doc.createElement("rampa");
			rampa.setAttribute("ini", "0.0");
			rampa.setAttribute("fim", "20.0");
			rampa.setTextContent(ponto.getElementsByTag("rampa").first().text());
			pontoDeMedida.appendChild(rampa);
			
			for(int i = 1; i <= ponto.getElementsByTag("raioCurva").size(); i++) {
				org.w3c.dom.Element raioCurva = doc.createElement("raioCurva");
				raioCurva.setAttribute("fim", ponto.getElementsByTag("raioCurva").get(i-1).attr("fim"));
				raioCurva.setAttribute("ini", ponto.getElementsByTag("raioCurva").get(i-1).attr("ini"));
				raioCurva.setTextContent(ponto.getElementsByTag("raioCurva").get(i-1).text());
				pontoDeMedida.appendChild(raioCurva);
			}
				
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
			latitude.setTextContent(
					ponto.getElementsByTag("localizacao").first().getElementsByTag("latitude").first().text());
			localizacao.appendChild(latitude);

			try {
				org.w3c.dom.Element longitude = doc.createElement("longitude");
				longitude.setTextContent(
						ponto.getElementsByTag("localizacao").first().getElementsByTag("longitude").first().text());
				localizacao.appendChild(longitude);
			} catch (Exception e) {
				System.err.println(ponto);
			}

			
//			if(ponto.getElementsByTag("rampa").size() > 1) {
//				String novaRampa = ponto.getElementsByTag("rampa").get(1).text();
//				forceNextPm(novaRampa, listaDePontosDeMedida, doc);
//			}
		}
	}

	private void writeXml(Document doc, OutputStream output) throws TransformerException {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();

		transformer.setOutputProperty(OutputKeys.INDENT, "yes");

		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(output);

		transformer.transform(source, result);
	}

	public ArrayList<Element> generateNewArray(ArrayList<Element> aclives, ArrayList<Element> declives,
			ArrayList<Element> planos, ArrayList<Element> cristas, RailwayGeneratorRequest req) {
		ArrayList<Element> arr = new ArrayList<Element>();

		Random random = new Random();

		for (int i = 0; i < req.getQtdAclives(); i++) {
			arr.add(aclives.get(random.nextInt(aclives.size())));
		}

		for (int i = 0; i < req.getQtdDeclives(); i++) {
			arr.add(declives.get(random.nextInt(declives.size())));
		}

//		for (int i = 0; i < req.getQtdPlanos(); i++) {
//			arr.add(planos.get(random.nextInt(planos.size())));
//		}

//		for (int i = 0; i < req.getQtdCristas(); i++) {
//			arr.add(cristas.get(random.nextInt(cristas.size())));
//		}

		Collections.shuffle(arr);
		
		

		return arr;
	}

	private ArrayList<Element> generatePms(ArrayList<Element> listaPontos) {
		Element tmpEl = new Element("listaDePontosDeMedida");
		ArrayList<Element> finalArr = new ArrayList<Element>();
		String rampaAnterior = listaPontos.get(0).getElementsByTag("rampa").first().text();;

		for (Element ponto : listaPontos) {
			String grauRampa = ponto.getElementsByTag("rampa").first().text();

			if (grauRampa.equals(rampaAnterior)) {
				ponto.appendTo(tmpEl);
			} else {
				finalArr.add(tmpEl);
				tmpEl = new Element("listaDePontosDeMedida");
				ponto.appendTo(tmpEl);
			}
			rampaAnterior = grauRampa;
		}

//		System.out.println(finalArr);
		return finalArr;
	}
	
//	private void forceNextPm(String novaRampa, org.w3c.dom.Element listaDePontosDeMedida, Document doc) {
//		for (int i = 0; i < novaVia.size(); i++) {
//			Elements listaPontos = novaVia.get(i).getElementsByTag("listaDePontosDeMedida").first().getElementsByTag("pontoDeMedida");
//			String rampa = listaPontos.first().getElementsByTag("rampa").first().text();
//			
//			if(rampa.equalsIgnoreCase(novaRampa)) {
//				Element pontos = novaVia.get(i);
//				novaVia.remove(novaVia.get(i));
//				writePontoDeMedida(pontos, listaDePontosDeMedida, doc, novaVia);
//			}
//		}
//	}
}
