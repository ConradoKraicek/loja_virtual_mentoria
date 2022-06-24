package conra.mentoria.lojavirtual.controller;

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
import conra.mentoria.lojavirtual.model.Produto;
import conra.mentoria.lojavirtual.repository.ProdutoRepository;

@Controller
@RestController
public class ProdutoController {
	
	@Autowired
	private ProdutoRepository produtoRepository;
	
	
	@ResponseBody /*Poder dar um retorno da API*/
	@PostMapping(value = "**/salvarProduto") /* Mapeando a url para rebeber o JSON */
	public ResponseEntity<Produto> salvarProduto(@RequestBody @Valid Produto produto ) throws ExceptionMentoriaJava { /*Recebe o JSON e converte pra Objeto*/
		
		if (produto.getEmpresa() == null || produto.getEmpresa().getId() <= 0) {
			throw new ExceptionMentoriaJava("Empresa responsável deve ser informada:");
		}
		
		if (produto.getId() == null) {
			
			List<Produto> produtos = produtoRepository.buscarProdutoNome(produto.getNome().toUpperCase(), produto.getEmpresa().getId());
			
			if (!produtos.isEmpty()) {
				throw new ExceptionMentoriaJava("Já existe Produto com a descrição:" + produto.getNome());
			}
		}
		
		if (produto.getCategoriaProduto() == null || produto.getCategoriaProduto().getId() <= 0) {
			throw new ExceptionMentoriaJava("Categoria deve ser informada:");
		}
		
		if (produto.getMarcaProduto() == null || produto.getMarcaProduto().getId() <= 0) {
			throw new ExceptionMentoriaJava("Marca deve ser informada:");
		}
		
		Produto produtoSalvo = produtoRepository.save(produto);
		
		return new ResponseEntity<Produto>(produtoSalvo, HttpStatus.OK);
	}
	

	@ResponseBody /*Poder dar um retorno da API*/
	@PostMapping(value = "**/deleteProduto") /* Mapeando a url para rebeber o JSON */
	public ResponseEntity<?> deleteProduto(@RequestBody Produto produto ) { /*Recebe o JSON e converte pra Objeto*/
		
		produtoRepository.deleteById(produto.getId());
		
		return new ResponseEntity("Produto Removido", HttpStatus.OK);
	}
	
	//@Secured({"ROLE_GERENTE","ROLE_ADMIN"})
	@ResponseBody 
	@DeleteMapping(value = "**/deleteProdutoPorId/{id}") 
	public ResponseEntity<?> deleteProdutoPorId(@PathVariable("id") Long id) {
		produtoRepository.deleteById(id);
		
		return new ResponseEntity("Produto Removido", HttpStatus.OK);
	}
	
	
	@ResponseBody 
	@GetMapping(value = "**/obterProduto/{id}") 
	public ResponseEntity<Produto> obterProduto(@PathVariable("id") Long id) throws ExceptionMentoriaJava {
		
		Produto produto = produtoRepository.findById(id).orElse(null);
		
		if (produto == null) {
			throw new ExceptionMentoriaJava("Não encontrou Produto com código:" + id);
		}
		
		return new ResponseEntity<Produto>(produto, HttpStatus.OK);
	}
	
	
	@ResponseBody 
	@GetMapping(value = "**/buscarProdNome/{nome}") 
	public ResponseEntity<List<Produto>> buscarProdNome(@PathVariable("nome") String nome) {
		
		List<Produto> produto = produtoRepository.buscarProdutoNome(nome.toUpperCase());
		
		return new ResponseEntity<List<Produto>>(produto, HttpStatus.OK);
	}
}
