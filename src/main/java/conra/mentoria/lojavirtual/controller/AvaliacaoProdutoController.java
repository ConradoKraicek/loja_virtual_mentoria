package conra.mentoria.lojavirtual.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import conra.mentoria.lojavirtual.ExceptionMentoriaJava;
import conra.mentoria.lojavirtual.model.AvaliacaoProduto;
import conra.mentoria.lojavirtual.repository.AvaliacaoProdutoRepository;

@RestController
public class AvaliacaoProdutoController {
	
	@Autowired
	private AvaliacaoProdutoRepository avaliacaoProdutoRepository;
	
	@ResponseBody
	@PostMapping(value = "**/salvaAvaliacaoProduto")
	public ResponseEntity<AvaliacaoProduto> salvaAvaliacaoProduto(@RequestBody @Valid AvaliacaoProduto avaliacaoProduto ) throws ExceptionMentoriaJava {
		
		if (avaliacaoProduto.getEmpresa() == null || (avaliacaoProduto.getEmpresa() != null && avaliacaoProduto.getEmpresa().getId() <= 0)) {
			
			throw new ExceptionMentoriaJava("Informa a empresa dona do registro");
		}
		
        if (avaliacaoProduto.getProduto() == null || (avaliacaoProduto.getProduto() != null && avaliacaoProduto.getProduto().getId() <= 0)) {
			
			throw new ExceptionMentoriaJava("A avaliação deve conter o Produto associado.");
		}
        
        if (avaliacaoProduto.getPessoa() == null || (avaliacaoProduto.getPessoa() != null && avaliacaoProduto.getPessoa().getId() <= 0)) {
			
			throw new ExceptionMentoriaJava("A avaliação deve conter a Pessoa associada.");
		}
		
		avaliacaoProduto = avaliacaoProdutoRepository.saveAndFlush(avaliacaoProduto);
		
		
		return new ResponseEntity<AvaliacaoProduto>(avaliacaoProduto, HttpStatus.OK);
		
	}
	
	
	@ResponseBody
	@DeleteMapping(value = "**/deleteAvaliacaoProduto/{idAvaliacao}")
	public ResponseEntity<?> deleteAvaliacaoProduto(@PathVariable("idAvaliacao") Long idAvaliacao) {
		
		
		avaliacaoProdutoRepository.deleteById(idAvaliacao);
		
		
		return new ResponseEntity<String>("Avaliação Removida", HttpStatus.OK);
		
	}
	
	@ResponseBody
	@GetMapping(value = "**/avaliacaoProduto/{idProduto}")
	public ResponseEntity<List<AvaliacaoProduto>> avaliacaoProduto(@PathVariable("idProduto") Long idProduto) {
		
		List<AvaliacaoProduto> avaliacaoProdutos = avaliacaoProdutoRepository.avaliacaoProduto(idProduto);
		
		
		return new ResponseEntity<List<AvaliacaoProduto>>(avaliacaoProdutos, HttpStatus.OK);
		
	}
	
	@ResponseBody
	@GetMapping(value = "**/avaliacaoProdutoPessoa/{idProduto}/{idPessoa}")
	public ResponseEntity<List<AvaliacaoProduto>> avaliacaoProdutoPessoa(@PathVariable("idProduto") Long idProduto, @PathVariable("idPessoa") Long idPessoa) {
		
		List<AvaliacaoProduto> avaliacaoProdutos = avaliacaoProdutoRepository.avaliacaoProdutoPessoa(idProduto, idPessoa);
		
		
		return new ResponseEntity<List<AvaliacaoProduto>>(avaliacaoProdutos, HttpStatus.OK);
		
	}
	
	@ResponseBody
	@GetMapping(value = "**/avaliacaoPessoa/{idPessoa}")
	public ResponseEntity<List<AvaliacaoProduto>> avaliacaoPessoa(@PathVariable("idPessoa") Long idPessoa) {
		
		List<AvaliacaoProduto> avaliacaoProdutos = avaliacaoProdutoRepository.avaliacaoPessoa(idPessoa);
		
		
		return new ResponseEntity<List<AvaliacaoProduto>>(avaliacaoProdutos, HttpStatus.OK);
		
	}
	
	

}
