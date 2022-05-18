package conra.mentoria.lojavirtual.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import conra.mentoria.lojavirtual.ExceptionMentoriaJava;
import conra.mentoria.lojavirtual.model.Endereco;
import conra.mentoria.lojavirtual.model.PessoaFisica;
import conra.mentoria.lojavirtual.model.PessoaJuridica;
import conra.mentoria.lojavirtual.model.dto.CepDTO;
import conra.mentoria.lojavirtual.repository.EnderecoRepository;
import conra.mentoria.lojavirtual.repository.PessoaFisicaRepository;
import conra.mentoria.lojavirtual.repository.PessoaRepository;
import conra.mentoria.lojavirtual.service.PessoaUserService;
import conra.mentoria.lojavirtual.util.ValidaCNPJ;
import conra.mentoria.lojavirtual.util.ValidaCPF;

@RestController
public class PessoaController {
	
	@Autowired
	private PessoaRepository pessoaRepository;
	
	@Autowired
	private PessoaUserService pessoaUserService;
	
	@Autowired
	private EnderecoRepository enderecoRepository;
	
	@Autowired
	private PessoaFisicaRepository pessoaFisicaRepository;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	
	@ResponseBody
	@GetMapping(value = "**/consultaPfNome/{nome}")
	public ResponseEntity<List<PessoaFisica>> consultaPfNome(@PathVariable("nome") String nome){
		
		List<PessoaFisica> fisicas = pessoaFisicaRepository.pesquisaPorNomePF(nome.trim().toUpperCase());
		
		jdbcTemplate.execute("begin; update tabela_acesso_end_point set qtd_acesso_end_point = qtd_acesso_end_point + 1 where nome_end_point = 'END-POINT-NOME-PESSOA-FISICA'; commit;");
		
		return new ResponseEntity<List<PessoaFisica>>(fisicas, HttpStatus.OK);
		
	}
	
	@ResponseBody
	@GetMapping(value = "**/consultaPfCpf/{cpf}")
	public ResponseEntity<List<PessoaFisica>> consultaPfCpf(@PathVariable("cpf") String cpf){
		
		List<PessoaFisica> fisicas = pessoaFisicaRepository.pesquisaPorCpfPF(cpf);
		
		return new ResponseEntity<List<PessoaFisica>>(fisicas, HttpStatus.OK);
		
	}
	
	@ResponseBody
	@GetMapping(value = "**/consultaCnpjPJ/{cnpj}")
	public ResponseEntity<List<PessoaJuridica>> consultaCnpjPJ(@PathVariable("cnpj") String cnpj){
		
		List<PessoaJuridica> fisicas = pessoaRepository.existeCnpjCadastradoList(cnpj);
		
		return new ResponseEntity<List<PessoaJuridica>>(fisicas, HttpStatus.OK);
		
	}
	
	@ResponseBody
	@GetMapping(value = "**/consultaNomePJ/{nome}")
	public ResponseEntity<List<PessoaJuridica>> consultaNomePJ(@PathVariable("cpf") String nome){
		
		List<PessoaJuridica> fisicas = pessoaRepository.pesquisaPorNomePJ(nome.trim().toUpperCase());
		
		return new ResponseEntity<List<PessoaJuridica>>(fisicas, HttpStatus.OK);
		
	}
	
	@ResponseBody
	@GetMapping(value = "**/consultaCep/{cep}")
	public ResponseEntity<CepDTO> consultaCep(@PathVariable("cep") String cep) {

		return new ResponseEntity<CepDTO>(pessoaUserService.consultaCep(cep), HttpStatus.OK);

	}
	
	/*end-point é microsservicos é um API*/
	@ResponseBody /*Poder dar um retorno da API*/
	@PostMapping(value = "**/salvarPj") /* Mapeando a url para rebeber o JSON */
	public ResponseEntity<PessoaJuridica> salvarPj(@RequestBody  @Valid PessoaJuridica pessoaJuridica) throws ExceptionMentoriaJava {
		
//		if (pessoaJuridica.getNome() == null || (pessoaJuridica.getNome().trim().isEmpty()) ) {
//			throw new ExceptionMentoriaJava("Pessoa juridica não pode ser NUll");
//		}
		
		if (pessoaJuridica == null ) {
			throw new ExceptionMentoriaJava("Pessoa juridica não pode ser NUll");
		}
		
		if (pessoaJuridica.getId() == null && pessoaRepository.existeCnpjCadastrado(pessoaJuridica.getCnpj()) != null) {
			throw new ExceptionMentoriaJava("Já existe CNPJ cadastrado com o número: " + pessoaJuridica.getCnpj());
		}
		
		if (pessoaJuridica.getId() == null && pessoaRepository.existeInsEstadualCadastrado(pessoaJuridica.getInscEstadual()) != null) {
			throw new ExceptionMentoriaJava("Já existe Inscrição Estadual cadastrado com o número: " + pessoaJuridica.getInscEstadual());
		}
		
		if (!ValidaCNPJ.isCNPJ(pessoaJuridica.getCnpj())) {
			throw new ExceptionMentoriaJava("Cnpj :" + pessoaJuridica.getCnpj() + "está inválido");
		}
		
		if (pessoaJuridica.getId() == null || pessoaJuridica.getId() <= 0) {
			
			for (int i = 0; i < pessoaJuridica.getEnderecos().size(); i++) {
				
				CepDTO cepDTO = pessoaUserService.consultaCep(pessoaJuridica.getEnderecos().get(i).getCep());
				
				pessoaJuridica.getEnderecos().get(i).setBairro(cepDTO.getBairro());
				pessoaJuridica.getEnderecos().get(i).setCidade(cepDTO.getLocalidade());
				pessoaJuridica.getEnderecos().get(i).setComplemento(cepDTO.getComplemento());
				pessoaJuridica.getEnderecos().get(i).setRuaLogra(cepDTO.getLogradouro());
				pessoaJuridica.getEnderecos().get(i).setUf(cepDTO.getUf());
			}
		} else {
			for (int i = 0; i < pessoaJuridica.getEnderecos().size(); i++) {
				
				Endereco enderecoTemp = enderecoRepository.findById(pessoaJuridica.getEnderecos().get(i).getId()).get();
				
				if (!enderecoTemp.getCep().equals(pessoaJuridica.getEnderecos().get(i).getCep())) {
					
					CepDTO cepDTO = pessoaUserService.consultaCep(pessoaJuridica.getEnderecos().get(i).getCep());
					
					pessoaJuridica.getEnderecos().get(i).setBairro(cepDTO.getBairro());
					pessoaJuridica.getEnderecos().get(i).setCidade(cepDTO.getLocalidade());
					pessoaJuridica.getEnderecos().get(i).setComplemento(cepDTO.getComplemento());
					pessoaJuridica.getEnderecos().get(i).setRuaLogra(cepDTO.getLogradouro());
					pessoaJuridica.getEnderecos().get(i).setUf(cepDTO.getUf());
				}
			}
		}
		
		pessoaJuridica = pessoaUserService.salvarPessoaJuridica(pessoaJuridica);
		
		
		return new ResponseEntity<PessoaJuridica>(pessoaJuridica, HttpStatus.OK);
		
	}
	
	
	/*end-point é microsservicos é um API*/
	@ResponseBody /*Poder dar um retorno da API*/
	@PostMapping(value = "**/salvarPf") /* Mapeando a url para rebeber o JSON */
	public ResponseEntity<PessoaFisica> salvarPf(@RequestBody PessoaFisica pessoaFisica) throws ExceptionMentoriaJava {
		
		if (pessoaFisica == null ) {
			throw new ExceptionMentoriaJava("Pessoa fisica não pode ser NUll");
		}
		
		if (pessoaFisica.getId() == null && pessoaRepository.existeCpfCadastrado(pessoaFisica.getCpf()) != null) {
			throw new ExceptionMentoriaJava("Já existe CPF cadastrado com o número: " + pessoaFisica.getCpf());
		}
		
		if (!ValidaCPF.isCPF(pessoaFisica.getCpf())) {
			throw new ExceptionMentoriaJava("Cpf :" + pessoaFisica.getCpf() + "está inválido");
		}
		
		
		pessoaFisica = pessoaUserService.salvarPessoaFisica(pessoaFisica);
		
		
		return new ResponseEntity<PessoaFisica>(pessoaFisica, HttpStatus.OK);
		
	}

}
