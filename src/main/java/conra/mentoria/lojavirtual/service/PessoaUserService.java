package conra.mentoria.lojavirtual.service;

import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import conra.mentoria.lojavirtual.model.PessoaFisica;
import conra.mentoria.lojavirtual.model.PessoaJuridica;
import conra.mentoria.lojavirtual.model.Usuario;
import conra.mentoria.lojavirtual.model.dto.CepDTO;
import conra.mentoria.lojavirtual.repository.PessoaFisicaRepository;
import conra.mentoria.lojavirtual.repository.PessoaRepository;
import conra.mentoria.lojavirtual.repository.UsuarioRepository;

@Service
public class PessoaUserService {
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private PessoaRepository pessoaRepository;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private ServiceSendEmail serviceSendEmail;
	
	@Autowired
	private PessoaFisicaRepository pessoaFisicaRepository;	
	
	
	public PessoaJuridica salvarPessoaJuridica(PessoaJuridica juridica) {
		
		//juridica = pessoaRepository.save(juridica);
		
		for (int i = 0; i < juridica.getEnderecos().size(); i++) {
			juridica.getEnderecos().get(i).setPessoa(juridica);
			juridica.getEnderecos().get(i).setEmpresa(juridica);
		}
		
		juridica = pessoaRepository.save(juridica);
		
		Usuario usuarioPj = usuarioRepository.findUserByPessoa(juridica.getId(), juridica.getEmail());
		
		if (usuarioPj == null) {
			
			String constraint = usuarioRepository.consultaConstraintAcesso();
			if (constraint != null) {
				jdbcTemplate.execute("begin; alter table usuarios_acesso drop constraint " + constraint + "; commit;");
			}
			
			usuarioPj = new Usuario();
			usuarioPj.setDataAtualSenha(Calendar.getInstance().getTime());
			usuarioPj.setEmpresa(juridica);
			usuarioPj.setPessoa(juridica);
			usuarioPj.setLogin(juridica.getEmail());
			
			String senha = "" + Calendar.getInstance().getTimeInMillis();
			String senhaCript = new BCryptPasswordEncoder().encode(senha);
			
			usuarioPj.setSenha(senhaCript);
			
			usuarioPj = usuarioRepository.save(usuarioPj);
			
			usuarioRepository.insereAcessoUser(usuarioPj.getId());
			usuarioRepository.insereAcessoUserPj(usuarioPj.getId(), "ROLE_ADMIN");
			
			StringBuilder mensagemHtml = new StringBuilder();
			
			mensagemHtml.append("<b>Segue abaixo seus dados de acesso para loja virtual</b><br/>");
			mensagemHtml.append("<b>Login: </b>"+juridica.getEmail()+"<br/>");
			mensagemHtml.append("<b>Senha: </b>").append(senha).append("<br/><br/>");
			mensagemHtml.append("Obrigado!");
			
			
			try {
				serviceSendEmail.enviarEmailHtml("Acesso Gerado para Loja Virtual", mensagemHtml.toString(), juridica.getEmail());
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
		
		return juridica;
		
	}


	public PessoaFisica salvarPessoaFisica(PessoaFisica fisica) {
		//fisica = pessoaRepository.save(fisica);
		
				for (int i = 0; i < fisica.getEnderecos().size(); i++) {
					//fisica.getEnderecos().get(i).setEmpresa(fisica);
					fisica.getEnderecos().get(i).setPessoa(fisica);
				}
				
				fisica = pessoaFisicaRepository.save(fisica);
				
				Usuario usuarioPj = usuarioRepository.findUserByPessoa(fisica.getId(), fisica.getEmail());
				
				if (usuarioPj == null) {
					
					String constraint = usuarioRepository.consultaConstraintAcesso();
					if (constraint != null) {
						jdbcTemplate.execute("begin; alter table usuarios_acesso drop constraint " + constraint + "; commit;");
					}
					
					usuarioPj = new Usuario();
					usuarioPj.setDataAtualSenha(Calendar.getInstance().getTime());
					usuarioPj.setEmpresa(fisica.getEmpresa());
					usuarioPj.setPessoa(fisica);
					usuarioPj.setLogin(fisica.getEmail());
					
					String senha = "" + Calendar.getInstance().getTimeInMillis();
					String senhaCript = new BCryptPasswordEncoder().encode(senha);
					
					usuarioPj.setSenha(senhaCript);
					
					usuarioPj = usuarioRepository.save(usuarioPj);
					
					usuarioRepository.insereAcessoUser(usuarioPj.getId());
					
					StringBuilder mensagemHtml = new StringBuilder();
					
					mensagemHtml.append("<b>Segue abaixo seus dados de acesso para loja virtual</b><br/>");
					mensagemHtml.append("<b>Login: </b>"+fisica.getEmail()+"<br/>");
					mensagemHtml.append("<b>Senha: </b>").append(senha).append("<br/><br/>");
					mensagemHtml.append("Obrigado!");
					
					
					try {
						serviceSendEmail.enviarEmailHtml("Acesso Gerado para Loja Virtual", mensagemHtml.toString(), fisica.getEmail());
					} catch (Exception e) {
						e.printStackTrace();
					} 
				}
				
				return fisica;
				
	} 
	
	public CepDTO consultaCep(String cep) {
		
		return new RestTemplate().getForEntity("https://viacep.com.br/ws/" + cep + "/json/", CepDTO.class).getBody();
	}

}
