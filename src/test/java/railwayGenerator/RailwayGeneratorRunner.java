package railwayGenerator;

public class RailwayGeneratorRunner 
{
	public static void main(String[] args) throws Exception 
	{
		RailwayGeneratorRequest req = RailwayGeneratorRequest
				.builder()
				.filePath("/Users/inkt/dev/tcc/railwayGenerator/src/test/resources/viaBase.xml")
				.qtdCurvas(100)
				.qtdRetas(2000)
				.qtdAclives(478)
				.qtdDeclives(470)
				.qtdPlanos(2)
				.qtdCristas(0)
				.build();
		
		new RailwayGenerator().execute(req);
	}
}
