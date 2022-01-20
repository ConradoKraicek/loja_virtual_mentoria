package conra.mentoria.lojavirtual;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import conra.mentoria.lojavirtual.controller.AcessoController;
import conra.mentoria.lojavirtual.model.Acesso;
import conra.mentoria.lojavirtual.repository.AcessoRepository;
import junit.framework.TestCase;

@SpringBootTest(classes = LojaVirtualMentoriaApplication.class)
public class LojaVirtualMentoriaApplicationTests extends TestCase {
	
	
	
	@Autowired
	private AcessoController acessoController;
	
	@Autowired
	private AcessoRepository acessoRepository;
	
	@Autowired
	private WebApplicationContext wac;
	
	/*Teste do end-point de salvar*/
	@Test
	public void testeRestApiCadastroAcesso() throws JsonProcessingException, Exception {
		DefaultMockMvcBuilder builder = MockMvcBuilders.webAppContextSetup(this.wac);
		MockMvc mockMvc = builder.build();
		
		Acesso acesso = new Acesso();	
		acesso.setDescricao("ROLE_COMPRADOR");
		
		ObjectMapper objectMapper = new ObjectMapper();
		
		ResultActions retornoApi = mockMvc
				                 .perform(MockMvcRequestBuilders.post("/salvarAcesso")
				                 .content(objectMapper.writeValueAsString(acesso))
				                 .accept(MediaType.APPLICATION_JSON)
				                 .contentType(MediaType.APPLICATION_JSON)); 
		
		
		//System.out.println("Retorno da API: " + retornoApi.andReturn().getResponse().getContentAsString());
		
		/* Converter o retorno da API para um objeto de acesso */
		Acesso objetoRetorno = objectMapper.readValue(retornoApi.andReturn().getResponse().getContentAsString(), Acesso.class); 
		
		assertEquals(acesso.getDescricao(), objetoRetorno.getDescricao());
		
	}
	
	/*Teste do end-point de deletar*/
	@Test
	public void testeRestApiDeleteAcesso() throws JsonProcessingException, Exception {
		DefaultMockMvcBuilder builder = MockMvcBuilders.webAppContextSetup(this.wac);
		MockMvc mockMvc = builder.build();
		
		Acesso acesso = new Acesso();	
		acesso.setDescricao("ROLE_TESTE_DELETE");
		
		acesso = acessoRepository.save(acesso);
		
		ObjectMapper objectMapper = new ObjectMapper();
		
		ResultActions retornoApi = mockMvc
				                 .perform(MockMvcRequestBuilders.post("/deleteAcesso")
				                 .content(objectMapper.writeValueAsString(acesso))
				                 .accept(MediaType.APPLICATION_JSON)
				                 .contentType(MediaType.APPLICATION_JSON)); 
		
		System.out.println("Retorno da API: " + retornoApi.andReturn().getResponse().getContentAsString());
		System.out.println("Status de retorno: " + retornoApi.andReturn().getResponse().getStatus());
		
		assertEquals("Acesso Removido", retornoApi.andReturn().getResponse().getContentAsString());
		assertEquals(200, retornoApi.andReturn().getResponse().getStatus());
		
	}
	
	/*Teste do end-point de deletar por ID*/
	@Test
	public void testeRestApiDeletePorIDAcesso() throws JsonProcessingException, Exception {
		DefaultMockMvcBuilder builder = MockMvcBuilders.webAppContextSetup(this.wac);
		MockMvc mockMvc = builder.build();
		
		Acesso acesso = new Acesso();	
		acesso.setDescricao("ROLE_TESTE_DELETE_ID");
		
		acesso = acessoRepository.save(acesso);
		
		ObjectMapper objectMapper = new ObjectMapper();
		
		ResultActions retornoApi = mockMvc
				                 .perform(MockMvcRequestBuilders.delete("/deleteAcessoPorId/" + acesso.getId())
				                 .content(objectMapper.writeValueAsString(acesso))
				                 .accept(MediaType.APPLICATION_JSON)
				                 .contentType(MediaType.APPLICATION_JSON)); 
		
		System.out.println("Retorno da API: " + retornoApi.andReturn().getResponse().getContentAsString());
		System.out.println("Status de retorno: " + retornoApi.andReturn().getResponse().getStatus());
		
		assertEquals("Acesso Removido", retornoApi.andReturn().getResponse().getContentAsString());
		assertEquals(200, retornoApi.andReturn().getResponse().getStatus());
		
	}
	
	/*Teste do end-point de obter Acesso por ID*/
	@Test
	public void testeRestApiObterAcessoID() throws JsonProcessingException, Exception {
		DefaultMockMvcBuilder builder = MockMvcBuilders.webAppContextSetup(this.wac);
		MockMvc mockMvc = builder.build();
		
		Acesso acesso = new Acesso();	
		acesso.setDescricao("ROLE_OBTER_ID");
		
		acesso = acessoRepository.save(acesso);
		
		ObjectMapper objectMapper = new ObjectMapper();
		
		ResultActions retornoApi = mockMvc
				                 .perform(MockMvcRequestBuilders.get("/obterAcesso/" + acesso.getId())
				                 .content(objectMapper.writeValueAsString(acesso))
				                 .accept(MediaType.APPLICATION_JSON)
				                 .contentType(MediaType.APPLICATION_JSON)); 
		
		assertEquals(200, retornoApi.andReturn().getResponse().getStatus());
		
		Acesso acessoRetorno = objectMapper.readValue(retornoApi.andReturn().getResponse().getContentAsString(), Acesso.class);
		
		assertEquals(acesso.getDescricao(), acessoRetorno.getDescricao());
	}
	
	
	@Test
	public void testeRestApiObterAcessoDesc() throws JsonProcessingException, Exception {
		DefaultMockMvcBuilder builder = MockMvcBuilders.webAppContextSetup(this.wac);
		MockMvc mockMvc = builder.build();
		
		Acesso acesso = new Acesso();	
		acesso.setDescricao("ROLE_OBTER_LIST");
		
		acesso = acessoRepository.save(acesso);
		
		ObjectMapper objectMapper = new ObjectMapper();
		
		ResultActions retornoApi = mockMvc
				                 .perform(MockMvcRequestBuilders.get("/buscarPorDescricao/OBTER_LIST")
				                 .content(objectMapper.writeValueAsString(acesso))
				                 .accept(MediaType.APPLICATION_JSON)
				                 .contentType(MediaType.APPLICATION_JSON)); 
		
		assertEquals(200, retornoApi.andReturn().getResponse().getStatus());
		
		List<Acesso> retornoApiList = objectMapper.readValue(retornoApi.andReturn().getResponse().getContentAsString(), new TypeReference<List<Acesso>>() {});
		
		assertEquals(1, retornoApiList.size());
		
		assertEquals(acesso.getDescricao(), retornoApiList.get(0).getDescricao());
		
		acessoRepository.deleteById(acesso.getId());
	}

	@Test
	public void testeCadastraAcesso() {
		
		Acesso acesso = new Acesso();
		
		acesso.setDescricao("ROLE_ADMIN");
		
		assertEquals(true, acesso.getId() == null);
		
		/*Gravou no banco de dados*/
		acesso = acessoController.salvarAcesso(acesso).getBody();
		
		assertEquals(true, acesso.getId() > 0);
		
		/*Validar dados salvos da forma correta*/
		assertEquals("ROLE_ADMIN", acesso.getDescricao());
		
		
		/* Teste de carregamento */
		Acesso acesso2 = acessoRepository.findById(acesso.getId()).get();
		
		assertEquals(acesso.getId(), acesso2.getId());
		
		
		/* Teste de delete */
		acessoRepository.deleteById(acesso2.getId());
		acessoRepository.flush(); /*roda esse SQL de delete no banco de dados*/
		Acesso acesso3 = acessoRepository.findById(acesso2.getId()).orElse(null);
		
		assertEquals(true, acesso3 == null);
		
		/*Teste de query*/
		acesso = new Acesso();
		acesso.setDescricao("ROLE_ALUNO");
		
		acesso = acessoController.salvarAcesso(acesso).getBody();
		List<Acesso> acessos = acessoRepository.buscarAcessoDescricao("ALUNO".trim().toUpperCase());
		
		assertEquals(1, acessos.size());
		
		acessoRepository.deleteById(acesso.getId());
	}

}
