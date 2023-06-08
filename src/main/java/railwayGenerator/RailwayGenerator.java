package railwayGenerator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class RailwayGenerator {
	
	public void execute(RailwayGeneratorRequest req) throws IOException {
		Document doc = Jsoup.parse(new File(req.getFilePath()));
		
		ArrayList<Element> retas = new ArrayList<Element>();
		ArrayList<Element> curvas = new ArrayList<Element>();
		
		Elements listaDePontos = doc.getElementsByTag("listaDePontosDeMedida").first().getElementsByTag("pontoDeMedida");
		
		for (Element ponto : listaDePontos) 
		{
			Elements raioCurva = ponto.getElementsByTag("raioCurva");
			
			if(raioCurva.size() > 1)
			{
				curvas.add(ponto);
			} else {
				retas.add(ponto);
			}
		}
		System.out.println("Quantidade de retas: " + retas.size());
		System.out.println("Quantidade de curvas: " + curvas.size());
	}

}
