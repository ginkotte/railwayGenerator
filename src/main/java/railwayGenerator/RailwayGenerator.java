package railwayGenerator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

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

	public void execute(RailwayGeneratorRequest req) throws Exception 
	{
		org.jsoup.nodes.Document doc = Jsoup.parse(new File(req.getFilePath()));

		ArrayList<Element> retas = new ArrayList<Element>();
		ArrayList<Element> curvas = new ArrayList<Element>();

		Elements listaDePontos = doc.getElementsByTag("listaDePontosDeMedida").first()
				.getElementsByTag("pontoDeMedida");

		for (Element ponto : listaDePontos) {
			Elements raioCurva = ponto.getElementsByTag("raioCurva");

			if (raioCurva.size() > 1) {
				curvas.add(ponto);
			} else {
				retas.add(ponto);
			}
		}
		System.out.println("Quantidade de retas: " + retas.size());
		System.out.println("Quantidade de curvas: " + curvas.size());
		
		writeToFile(retas, curvas, req);
	}

	public void writeToFile(ArrayList<Element> retas, ArrayList<Element> curvas, RailwayGeneratorRequest req)
			throws Exception 
	{
		if (req.getQuantidadeCurvas() > curvas.size()) {
			throw new Exception("Quantidade de curvas maior do que o valor existente: " + curvas.size());
		}

		if (req.getQuantidadeRetas() > retas.size()) {
			throw new Exception("Quantidade de retas maior do que o valor existente: " + retas.size());
		}

		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

		Document doc = docBuilder.newDocument();
		org.w3c.dom.Element rootElement = doc.createElement("viaFerrea");
		doc.appendChild(rootElement);
		
		org.w3c.dom.Element listaDePontosDeMedida = writeDefaultTags(doc, rootElement);
		
		writePontoDeMedida(retas.get(0), listaDePontosDeMedida, doc, "1");
		
		int quantidadeSegmentos = req.getQuantidadeCurvas() + req.getQuantidadeRetas();

		for (int i = 0; i < quantidadeSegmentos; i++) {

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
	
	public void writePontoDeMedida(Element ponto, org.w3c.dom.Element listaDePontosDeMedida, Document doc, String id)
	{
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
		
		org.w3c.dom.Element rampa = doc.createElement("rampa");
		rampa.setAttribute("fim", ponto.getElementsByTag("rampa").first().attr("fim"));
		rampa.setAttribute("ini", ponto.getElementsByTag("rampa").first().attr("ini"));
		rampa.setTextContent(ponto.getElementsByTag("rampa").first().text());
		pontoDeMedida.appendChild(rampa);
		
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

}
