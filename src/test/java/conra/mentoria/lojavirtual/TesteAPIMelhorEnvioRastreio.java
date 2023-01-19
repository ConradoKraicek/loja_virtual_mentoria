package conra.mentoria.lojavirtual;

import conra.mentoria.lojavirtual.enums.ApiTokenIntegração;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TesteAPIMelhorEnvioRastreio {
	
	public static void main(String[] args) throws Exception {
		
				OkHttpClient client = new OkHttpClient().newBuilder()
				  .build();
				MediaType mediaType = MediaType.parse("application/json");
				RequestBody body = RequestBody.create(mediaType, "{\n    \"orders\": [\n        \"4683ecc2-cec6-48e7-9ebf-58df1329ab52\"\n    ]\n}");
				Request request = new Request.Builder()
				  .url(ApiTokenIntegração.URL_MELHOR_ENVIO_SAND_BOX + "api/v2/me/shipment/tracking")
				  .method("POST", body)
				  .addHeader("Accept", "application/json")
				  .addHeader("Content-Type", "application/json")
				  .addHeader("Authorization", "Bearer " + ApiTokenIntegração.TOKEN_MELHOR_ENVIO_SAND_BOX)
				  .addHeader("User-Agent", "conradoempresa123@gmail.com")
				  .build();
				Response response = client.newCall(request).execute();
				
				System.out.println(response.body().string());
	}

}
