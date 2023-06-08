package railwayGenerator;

import java.io.IOException;

public class RailwayGeneratorRunner 
{
	public static void main(String[] args) throws IOException 
	{
		RailwayGeneratorRequest req = RailwayGeneratorRequest
				.builder()
				.filePath("/Users/inkt/dev/tcc/railwayGenerator/src/test/resources/via01.xml")
				.build();
		
		new RailwayGenerator().execute(req);
	}
}
