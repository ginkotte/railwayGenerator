package railwayGenerator;

import java.io.IOException;

public class RailwayGeneratorRunner 
{
	public static void main(String[] args) throws Exception 
	{
		RailwayGeneratorRequest req = RailwayGeneratorRequest
				.builder()
				.filePath("/Users/inkt/dev/tcc/railwayGenerator/src/test/resources/via01.xml")
				.quantidadeCurvas(100)
				.quantidadeRetas(2000)
				.build();
		
		new RailwayGenerator().execute(req);
	}
}
