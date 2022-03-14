package conra.mentoria.lojavirtual;

import java.util.Calendar;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;

import conra.mentoria.lojavirtual.controller.PessoaController;
import conra.mentoria.lojavirtual.model.PessoaJuridica;
import conra.mentoria.lojavirtual.repository.PessoaRepository;
import conra.mentoria.lojavirtual.service.PessoaUserService;
import junit.framework.TestCase;

@Profile("test")
@SpringBootTest(classes = LojaVirtualMentoriaApplication.class)
public class TestePessoaUsuario extends TestCase {
	
	@Autowired
	private PessoaUserService pessoaUserService;
	
	@Autowired
	private PessoaRepository pessoaRepository;
	
	@Autowired
	private PessoaController pessoaController;
	
	
	@Test
	public void testCadPessoaFisica() throws ExceptionMentoriaJava {
		
		PessoaJuridica pessoaJuridica = new PessoaJuridica();
		
		pessoaJuridica.setCnpj("" + Calendar.getInstance().getTimeInMillis());
		pessoaJuridica.setNome("Alex Fernando");
		pessoaJuridica.setEmail("testesalvarpj@gmail.com");
		pessoaJuridica.setTelefone("071996568989");
		pessoaJuridica.setInscEstadual("6565656565656");
		pessoaJuridica.setInscMunicipal("998998998998999898");
		pessoaJuridica.setNomeFantasia("Nestle America");
		pessoaJuridica.setRazaoSocial("568568568568568568");
		
		pessoaController.salvarPj(pessoaJuridica);
		
		
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
