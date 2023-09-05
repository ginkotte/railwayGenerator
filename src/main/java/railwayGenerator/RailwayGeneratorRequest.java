package railwayGenerator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RailwayGeneratorRequest {
	private String filePath;
	private int qtdRetas;
	private int qtdCurvas;
	private int qtdAclives;
	private int qtdDeclives;
	private int qtdPlanos;

}
