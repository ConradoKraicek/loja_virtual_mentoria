package conra.mentoria.lojavirtual;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import conra.mentoria.lojavirtual.enums.ApiTokenIntegração;
import conra.mentoria.lojavirtual.model.dto.EmpresaTransporteDTO;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TesteAPIMelhorEnvio {
	
	public static void main(String[] args) throws Exception {
		
		        OkHttpClient client = new OkHttpClient().newBuilder().build(); /*Instancia o objeto de requisição*/
				MediaType mediaType = MediaType.parse("application/json"); /*Tipo do dados JSON*/
				RequestBody body = RequestBody.create(mediaType, "{ \"from\": { \"postal_code\": \"96020360\" }, \"to\": { \"postal_code\": \"01018020\" }, \"products\": [ { \"id\": \"x\", \"width\": 11, \"height\": 17, \"length\": 11, \"weight\": 0.3, \"insurance_value\": 10.1, \"quantity\": 1 }, { \"id\": \"y\", \"width\": 16, \"height\": 25, \"length\": 11, \"weight\": 0.3, \"insurance_value\": 55.05, \"quantity\": 2 }, { \"id\": \"z\", \"width\": 22, \"height\": 30, \"length\": 11, \"weight\": 1, \"insurance_value\": 30, \"quantity\": 1 } ] }");
				Request request = new Request.Builder()
				  .url(ApiTokenIntegração.URL_MELHOR_ENVIO_SAND_BOX + "api/v2/me/shipment/calculate")
				  .method("POST", body)
				  .addHeader("Accept", "application/json")
				  .addHeader("Content-Type", "application/json")
				  .addHeader("Authorization", "Bearer " + ApiTokenIntegração.TOKEN_MELHOR_ENVIO_SAND_BOX)
				  .addHeader("User-Agent", "conradoempresa123@gmail.com")
				  .build();
				Response response = client.newCall(request).execute();
				//System.out.println(response.body().string());
				
				
				JsonNode jsonNode = new ObjectMapper().readTree(response.body().string());
				
				Iterator<JsonNode> iterator = jsonNode.iterator();
				
				List<EmpresaTransporteDTO> empresaTransporteDTOs = new ArrayList<EmpresaTransporteDTO>();
				
				while (iterator.hasNext()) {
					
					JsonNode node = iterator.next();
					//System.out.println(node.get("name"));
					
					EmpresaTransporteDTO empresaTransporteDTO = new EmpresaTransporteDTO();
					
					if (node.get("id") != null) {
						empresaTransporteDTO.setId(node.get("id").asText());
					}
					
					if (node.get("name") != null) {
						empresaTransporteDTO.setNome(node.get("name").asText());
					}
					
					if (node.get("price") != null) {
						empresaTransporteDTO.setValor(node.get("price").asText());
					}
					
					if (node.get("company") != null) {
						empresaTransporteDTO.setEmpresa(node.get("company").get("name").asText());
						empresaTransporteDTO.setPicture(node.get("company").get("picture").asText());
					}
					
					if (empresaTransporteDTO.dadosOK()) {
						
						empresaTransporteDTOs.add(empresaTransporteDTO);
					}
					
				}
				
				System.out.println(empresaTransporteDTOs);
		
	}

}
