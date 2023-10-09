package railwayGenerator;

public class RailwayGeneratorRunner 
{
	public static void main(String[] args) throws Exception 
	{
		RailwayGeneratorRequest req = RailwayGeneratorRequest
				.builder()
				.filePath("/Users/inkt/dev/tcc/railwayGenerator/src/test/resources/viaBase.xml")
//				.qtdCurvas(100)
//				.qtdRetas(2000)
				.qtdAclives(482)
				.qtdDeclives(469)
//				.qtdPlanos(7)
				.qtdCristas(0)
				.build();
		
		new RailwayGenerator().execute(req);
	}
}
