package conra.mentoria.lojavirtual;

import java.util.Calendar;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;

import conra.mentoria.lojavirtual.controller.PessoaController;
import conra.mentoria.lojavirtual.enums.TipoEndereco;
import conra.mentoria.lojavirtual.model.Endereco;
import conra.mentoria.lojavirtual.model.PessoaFisica;
import conra.mentoria.lojavirtual.model.PessoaJuridica;
import conra.mentoria.lojavirtual.repository.PessoaRepository;
import conra.mentoria.lojavirtual.service.ServiceJunoBoleto;
import junit.framework.TestCase;

@Profile("test")
@SpringBootTest(classes = LojaVirtualMentoriaApplication.class)
public class TestePessoaUsuario extends TestCase {
	
	@Autowired
	private PessoaRepository pessoaRepository;
	
	@Autowired
	private PessoaController pessoaController;
	
	@Autowired
	private ServiceJunoBoleto serviceJunoBoleto;
	
	
	@Test
	public void testeToken() throws Exception {
		
		serviceJunoBoleto.obterTokenApiJuno();
		
	}
	
	
	@Test
	public void testCadPessoaJuridica() throws ExceptionMentoriaJava {
		
		PessoaJuridica pessoaJuridica = new PessoaJuridica();
		
		pessoaJuridica.setCnpj("" + Calendar.getInstance().getTimeInMillis());
		pessoaJuridica.setNome("Conrado Albuquerque");
		pessoaJuridica.setEmail("conradoempresa123@gmail.com");
		pessoaJuridica.setTelefone("071996568989");
		pessoaJuridica.setInscEstadual("6565656565656");
		pessoaJuridica.setInscMunicipal("998998998998999898");
		pessoaJuridica.setNomeFantasia("Nestle America");
		pessoaJuridica.setRazaoSocial("568568568568568568");
		
	    Endereco endereco1 = new Endereco();
	    endereco1.setBairro("Jd dias");
	    endereco1.setCep("45454548956");
	    endereco1.setComplemento("Casa Cinza");
	    endereco1.setEmpresa(pessoaJuridica);
	    endereco1.setNumero("389");
	    endereco1.setPessoa(pessoaJuridica);
	    endereco1.setRuaLogra("Rua Logra");
	    endereco1.setTipoEndereco(TipoEndereco.COBRANCA);
	    endereco1.setUf("PR");
	    endereco1.setCidade("Curitiba");
	    
	    
	    Endereco endereco2 = new Endereco();
	    endereco2.setBairro("Novo Horizonte");
	    endereco2.setCep("999888754215");
	    endereco2.setComplemento("Casa Bela");
	    endereco2.setEmpresa(pessoaJuridica);
	    endereco2.setNumero("756");
	    endereco2.setPessoa(pessoaJuridica);
	    endereco2.setRuaLogra("Rua Xavier");
	    endereco2.setTipoEndereco(TipoEndereco.ENTREGA);
	    endereco2.setUf("PR");
	    endereco2.setCidade("curitiba");
	    
	    
	    pessoaJuridica.getEnderecos().add(endereco1);
	    pessoaJuridica.getEnderecos().add(endereco2);
	    
	    
		pessoaJuridica = pessoaController.salvarPj(pessoaJuridica).getBody();
		
		assertEquals(true, pessoaJuridica.getId() > 0);
		
		for (Endereco endereco : pessoaJuridica.getEnderecos()) {
			assertEquals(true, endereco.getId() > 0);
		}
		
		assertEquals(2, pessoaJuridica.getEnderecos().size());
		
		
		/*
		 * PessoaFisica pessoaFisica = new PessoaFisica();
		 * 
		 * pessoaFisica.setCpf("0597975788"); pessoaFisica.setNome("Alex Fernando");
		 * pessoaFisica.setEmail("Alex.fernando@gmail.com");
		 * pessoaFisica.setTelefone("071996568989");
		 * pessoaFisica.setEmpresa(pessoaFisica);
		 */
		
		
	}
	
	
	@Test
	public void testCadPessoaFisica() throws ExceptionMentoriaJava {
		
		PessoaJuridica pessoaJuridica = pessoaRepository.existeCnpjCadastrado("1649183846853");
		
		PessoaFisica pessoaFisica = new PessoaFisica();
		
		pessoaFisica.setCpf("517.768.530-67");
		pessoaFisica.setNome("Conrado Albuquerque");
		pessoaFisica.setEmail("conradoereretrempresa123@gmail.com");
		pessoaFisica.setTelefone("071996568989");
		pessoaFisica.setEmpresa(pessoaJuridica);
		
		
	    Endereco endereco1 = new Endereco();
	    endereco1.setBairro("Jd dias");
	    endereco1.setCep("45454548956");
	    endereco1.setComplemento("Casa Cinza");
	    endereco1.setEmpresa(pessoaFisica);
	    endereco1.setNumero("389");
	    endereco1.setRuaLogra("Rua Logra");
	    endereco1.setTipoEndereco(TipoEndereco.COBRANCA);
	    endereco1.setUf("PR");
	    endereco1.setCidade("Curitiba");
	    endereco1.setEmpresa(pessoaJuridica);
	    
	    Endereco endereco2 = new Endereco();
	    endereco2.setBairro("Novo Horizonte");
	    endereco2.setCep("999888754215");
	    endereco2.setComplemento("Casa Bela");
	    endereco2.setNumero("756");
	    endereco2.setPessoa(pessoaFisica);
	    endereco2.setRuaLogra("Rua Xavier");
	    endereco2.setTipoEndereco(TipoEndereco.ENTREGA);
	    endereco2.setUf("PR");
	    endereco2.setCidade("curitiba");
	    endereco2.setEmpresa(pessoaJuridica);
	    
	    
	    pessoaFisica.getEnderecos().add(endereco1);
	    pessoaFisica.getEnderecos().add(endereco2);
	    
	    
		pessoaFisica = pessoaController.salvarPf(pessoaFisica).getBody();
		
		assertEquals(true, pessoaFisica.getId() > 0);
		
		for (Endereco endereco : pessoaFisica.getEnderecos()) {
			assertEquals(true, endereco.getId() > 0);
		}
		
		assertEquals(2, pessoaFisica.getEnderecos().size());
		
		
		/*
		 * PessoaFisica pessoaFisica = new PessoaFisica();
		 * 
		 * pessoaFisica.setCpf("0597975788"); pessoaFisica.setNome("Alex Fernando");
		 * pessoaFisica.setEmail("Alex.fernando@gmail.com");
		 * pessoaFisica.setTelefone("071996568989");
		 * pessoaFisica.setEmpresa(pessoaFisica);
		 */
		
		
	}


}
