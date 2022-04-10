package conra.mentoria.lojavirtual.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import conra.mentoria.lojavirtual.ExceptionMentoriaJava;
import conra.mentoria.lojavirtual.model.PessoaFisica;
import conra.mentoria.lojavirtual.model.PessoaJuridica;
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
	
	/*end-point é microsservicos é um API*/
	@ResponseBody /*Poder dar um retorno da API*/
	@PostMapping(value = "**/salvarPj") /* Mapeando a url para rebeber o JSON */
	public ResponseEntity<PessoaJuridica> salvarPj(@RequestBody PessoaJuridica pessoaJuridica) throws ExceptionMentoriaJava {
		
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
