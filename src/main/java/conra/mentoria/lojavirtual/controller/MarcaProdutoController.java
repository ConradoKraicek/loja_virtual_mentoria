package conra.mentoria.lojavirtual.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import conra.mentoria.lojavirtual.ExceptionMentoriaJava;
import conra.mentoria.lojavirtual.model.MarcaProduto;
import conra.mentoria.lojavirtual.repository.MarcaRepository;

@Controller
@RestController
public class MarcaProdutoController {
	
	@Autowired
	private MarcaRepository marcaRepository;
	
	
	@ResponseBody /*Poder dar um retorno da API*/
	@PostMapping(value = "**/salvarMarca") /* Mapeando a url para rebeber o JSON */
	public ResponseEntity<MarcaProduto> salvarMarca(@RequestBody @Valid MarcaProduto marcaProduto ) throws ExceptionMentoriaJava { /*Recebe o JSON e converte pra Objeto*/
		
		if (marcaProduto.getId() == null) {
			
			List<MarcaProduto> marcaProdutos = marcaRepository.buscarMarcaDesc(marcaProduto.getNomeDesc().toUpperCase());
			
			if (!marcaProdutos.isEmpty()) {
				throw new ExceptionMentoriaJava("Já existe Marca com a descrição:" + marcaProduto.getNomeDesc());
			}
		}
		
		MarcaProduto marcaProdutoSalvo = marcaRepository.save(marcaProduto);
		
		return new ResponseEntity<MarcaProduto>(marcaProdutoSalvo, HttpStatus.OK);
	}
	

	@ResponseBody /*Poder dar um retorno da API*/
	@PostMapping(value = "**/deleteMarca") /* Mapeando a url para rebeber o JSON */
	public ResponseEntity<?> deleteMarca(@RequestBody MarcaProduto marcaProduto ) { /*Recebe o JSON e converte pra Objeto*/
		
		marcaRepository.deleteById(marcaProduto.getId());
		
		return new ResponseEntity("Marca produto Removido", HttpStatus.OK);
	}
	
	//@Secured({"ROLE_GERENTE","ROLE_ADMIN"})
	@ResponseBody 
	@DeleteMapping(value = "**/deleteMarcaPorId/{id}") 
	public ResponseEntity<?> deleteMarcaPorId(@PathVariable("id") Long id) {
		
		marcaRepository.deleteById(id);
		
		return new ResponseEntity("Marca produto Removido", HttpStatus.OK);
	}
	
	
	@ResponseBody 
	@GetMapping(value = "**/obterMarcaProduto/{id}") 
	public ResponseEntity<MarcaProduto> obterMarcaProduto(@PathVariable("id") Long id) throws ExceptionMentoriaJava {
		
		MarcaProduto marcaProduto = marcaRepository.findById(id).orElse(null);
		
		if (marcaProduto == null) {
			throw new ExceptionMentoriaJava("Não encontrou Marca Produto com código:" + id);
		}
		
		return new ResponseEntity<MarcaProduto>(marcaProduto, HttpStatus.OK);
	}
	
	
	@ResponseBody 
	@GetMapping(value = "**/buscarMarcaProdutoPorDescricao/{desc}") 
	public ResponseEntity<List<MarcaProduto>> buscarMarcaProdutoPorDescricao(@PathVariable("desc") String desc) {
		
		List<MarcaProduto> marcaProduto = marcaRepository.buscarMarcaDesc(desc.toUpperCase().trim());
		
		return new ResponseEntity<List<MarcaProduto>>(marcaProduto, HttpStatus.OK);
	}
}
