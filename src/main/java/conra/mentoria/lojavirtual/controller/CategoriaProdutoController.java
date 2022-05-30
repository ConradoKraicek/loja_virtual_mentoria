package conra.mentoria.lojavirtual.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import conra.mentoria.lojavirtual.ExceptionMentoriaJava;
import conra.mentoria.lojavirtual.model.CategoriaProduto;
import conra.mentoria.lojavirtual.model.dto.CategoriaProdutoDTO;
import conra.mentoria.lojavirtual.repository.CategoriaProdutoRepository;

@RestController
public class CategoriaProdutoController {
	
	@Autowired
	private CategoriaProdutoRepository categoriaProdutoRepository;
	
	
	@ResponseBody 
	@GetMapping(value = "**/buscarPorDescricaoCategoria/{desc}") 
	public ResponseEntity<List<CategoriaProduto>> buscarPorDescricaoCategoria(@PathVariable("desc") String desc) {
		
		List<CategoriaProduto> categoriaDescricao = categoriaProdutoRepository.buscarCategoriaPorDescricao(desc.toUpperCase());
		
		return new ResponseEntity<List<CategoriaProduto>>(categoriaDescricao, HttpStatus.OK);
	}
	
	
	@ResponseBody /*Poder dar um retorno da API*/
	@PostMapping(value = "**/deleteCategoria") /* Mapeando a url para rebeber o JSON */
	public ResponseEntity<?> deleteCategoria(@RequestBody CategoriaProduto categoriaProduto) { /*Recebe o JSON e converte pra Objeto*/
		
		if(categoriaProdutoRepository.findById(categoriaProduto.getId()).isPresent() == false) {
			return new ResponseEntity("Categoria já foi removida", HttpStatus.OK);
		}
		
		categoriaProdutoRepository.deleteById(categoriaProduto.getId());
		
		return new ResponseEntity("Categoria Removida", HttpStatus.OK);
	}
	
	
	@ResponseBody
	@PostMapping(value = "**/salvarCategoria")
	public ResponseEntity<CategoriaProdutoDTO> salvarCategoria(@RequestBody CategoriaProduto categoriaProduto) throws ExceptionMentoriaJava {
		
		if (categoriaProduto.getEmpresa() == null || categoriaProduto.getEmpresa().getId() == null) {
			
			throw new ExceptionMentoriaJava("A empresa deve ser informada.");
		}
		
        if (categoriaProduto.getId() == null && categoriaProdutoRepository.existeCategoria(categoriaProduto.getNomeDesc())) {
			
			throw new ExceptionMentoriaJava("Não pode cadastrar categoria com mesmo nome.");
		}
		
		CategoriaProduto categoriaSalva = categoriaProdutoRepository.save(categoriaProduto);
		
		CategoriaProdutoDTO categoriaProdutoDTO = new CategoriaProdutoDTO();
		categoriaProdutoDTO.setId(categoriaSalva.getId());
		categoriaProdutoDTO.setNomeDesc(categoriaSalva.getNomeDesc());
		categoriaProdutoDTO.setEmpresa(categoriaSalva.getEmpresa().getId().toString());
		
		return new ResponseEntity<CategoriaProdutoDTO>(categoriaProdutoDTO , HttpStatus.OK);
	}

}
