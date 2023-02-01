package conra.mentoria.lojavirtual.service;

import java.io.Serializable;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.DatatypeConverter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import conra.mentoria.lojavirtual.model.AcessTokenJunoAPI;
import conra.mentoria.lojavirtual.repository.AccesTokenJunoRepository;

@Service
public class ServiceJunoBoleto implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Autowired
	private AcessTokenJunoService accessTokenJunoService;
	
	@Autowired
	private AccesTokenJunoRepository accesTokenJunoRepository;
	
	public AcessTokenJunoAPI obterTokenApiJuno() throws Exception {
		
		AcessTokenJunoAPI accessTokenJunoAPI = accessTokenJunoService.buscaTokenAtivo();
		
		if (accessTokenJunoAPI == null || (accessTokenJunoAPI != null && accessTokenJunoAPI.expirado()) ) {
			
			String clienteID = "vi7QZerW09C8JG1o";
			String secretID = "$A_+&ksH}&+2<3VM]1MZqc,F_xif_-Dc";
			
			Client client = new HostIgnoringCliente("https://api.juno.com.br/").hostIgnoringCliente();
			
			WebResource webResource = client.resource("https://api.juno.com.br/authorization-server/oauth/token?grant_type=client_credentials");
			
			String basicChave = clienteID + ":" + secretID;
			String token_autenticao = DatatypeConverter.printBase64Binary(basicChave.getBytes());
			
			ClientResponse clientResponse = webResource.
					accept(MediaType.APPLICATION_FORM_URLENCODED)
					.type(MediaType.APPLICATION_FORM_URLENCODED)
					.header("Content-Type", "application/x-www-form-urlencoded")
					.header("Authorization", "Basic " + token_autenticao)
					.post(ClientResponse.class);
			
			if (clientResponse.getStatus() == 200) { /*Sucesso*/
				accesTokenJunoRepository.deleteAll();
				accesTokenJunoRepository.flush();
				
				AcessTokenJunoAPI accessTokenJunoAPI2 = clientResponse.getEntity(AcessTokenJunoAPI.class);
				accessTokenJunoAPI2.setToken_acesso(token_autenticao);
				
				accessTokenJunoAPI2 = accesTokenJunoRepository.saveAndFlush(accessTokenJunoAPI2);
				
				return accessTokenJunoAPI2;
			}else {
				return null;
			}
			
			
		}else {
			return accessTokenJunoAPI;
		}
	}

}