package conra.mentoria.lojavirtual.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import conra.mentoria.lojavirtual.ExceptionMentoriaJava;
import conra.mentoria.lojavirtual.model.ContaPagar;
import conra.mentoria.lojavirtual.repository.ContaPagarRepository;

@Controller
@RestController
public class ContaPagarController {
	
	@Autowired
	private ContaPagarRepository contaPagarRepository;
	
	
	@ResponseBody /*Poder dar um retorno da API*/
	@PostMapping(value = "**/salvarContaPagar") /* Mapeando a url para rebeber o JSON */
	public ResponseEntity<ContaPagar> salvarContaPagar(@RequestBody @Valid ContaPagar contaPagar ) throws ExceptionMentoriaJava { /*Recebe o JSON e converte pra Objeto*/
		
		if (contaPagar.getEmpresa() == null || contaPagar.getEmpresa().getId() <= 0) {
			throw new ExceptionMentoriaJava("Empresa responsável deve ser informada:");
		}
		
		if (contaPagar.getPessoa() == null || contaPagar.getPessoa().getId() <= 0) {
			throw new ExceptionMentoriaJava("Pessoa responsável deve ser informada:");
		}
		
		if (contaPagar.getPessoa_fornecedor() == null || contaPagar.getPessoa_fornecedor().getId() <= 0) {
			throw new ExceptionMentoriaJava("Fornecedor responsável deve ser informado:");
		}
		
		if (contaPagar.getId() == null) {
			
			List<ContaPagar> contaPagars = contaPagarRepository.buscarContaDesc(contaPagar.getDescricao().toUpperCase().trim());
			if (!contaPagars.isEmpty()) {
				throw new ExceptionMentoriaJava("Já existe conta a pagar com a mesma descrição.");
			}
		}
		
		
		
		ContaPagar contaPagarSalvo = contaPagarRepository.save(contaPagar);
		
		return new ResponseEntity<ContaPagar>(contaPagarSalvo, HttpStatus.OK);
	}
	

	@ResponseBody /*Poder dar um retorno da API*/
	@PostMapping(value = "**/deleteContaPagar") /* Mapeando a url para rebeber o JSON */
	public ResponseEntity<?> deleteContaPagar(@RequestBody ContaPagar contaPagar ) { /*Recebe o JSON e converte pra Objeto*/
		
		contaPagarRepository.deleteById(contaPagar.getId());
		
		return new ResponseEntity("Conta Pagar Removida", HttpStatus.OK);
	}
	
	//@Secured({"ROLE_GERENTE","ROLE_ADMIN"})
	@ResponseBody 
	@DeleteMapping(value = "**/deleteContaPagarPorId/{id}") 
	public ResponseEntity<String> deleteContaPagarPorId(@PathVariable("id") Long id) {
		
		contaPagarRepository.deleteById(id);
		
		return new ResponseEntity<String>("Conta pagar Removida", HttpStatus.OK);
	}
	
	
	@ResponseBody 
	@GetMapping(value = "**/obterContaPagar/{id}") 
	public ResponseEntity<ContaPagar> obterContaPagar(@PathVariable("id") Long id) throws ExceptionMentoriaJava {
		
		ContaPagar contaPagar = contaPagarRepository.findById(id).orElse(null);
		
		if (contaPagar == null) {
			throw new ExceptionMentoriaJava("Não encontrou conta a Pagar com código:" + id);
		}
		
		return new ResponseEntity<ContaPagar>(contaPagar, HttpStatus.OK);
	}
	
	
	@ResponseBody 
	@GetMapping(value = "**/buscarContaPagarDesc/{nome}") 
	public ResponseEntity<List<ContaPagar>> buscarContaPagarDesc(@PathVariable("desc") String desc) {
		
		List<ContaPagar> contaPagar = contaPagarRepository.buscarContaDesc(desc.toUpperCase());
		
		return new ResponseEntity<List<ContaPagar>>(contaPagar, HttpStatus.OK);
	}
}
