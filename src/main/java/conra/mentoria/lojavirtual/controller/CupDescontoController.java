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
import conra.mentoria.lojavirtual.model.CupDesc;
import conra.mentoria.lojavirtual.model.MarcaProduto;
import conra.mentoria.lojavirtual.repository.CupDescontoRepository;

@RestController
public class CupDescontoController {
	
	@Autowired
	private CupDescontoRepository cupDescontoRepository;
	
	
	
	@ResponseBody 
	@DeleteMapping(value = "**/deleteCupomPorId/{id}") 
	public ResponseEntity<?> deleteCupomPorId(@PathVariable("id") Long id) {
		
		cupDescontoRepository.deleteById(id);
		
		return new ResponseEntity("Cupom Removido", HttpStatus.OK);
	}
	
	
	@ResponseBody 
	@GetMapping(value = "**/obterCupom/{id}") 
	public ResponseEntity<CupDesc> obterCupom(@PathVariable("id") Long id) throws ExceptionMentoriaJava {
		
		CupDesc cupDesc = cupDescontoRepository.findById(id).orElse(null);
		
		if (cupDesc == null) {
			throw new ExceptionMentoriaJava("Não encontrou Cupom com código:" + id);
		}
		
		return new ResponseEntity<CupDesc>(cupDesc, HttpStatus.OK);
	}
	
	
	@ResponseBody
	@PostMapping(value = "**/salvarCupomDesconto")
	public ResponseEntity<CupDesc> salvarCupomDesconto(@RequestBody @Valid CupDesc cupDesc) throws ExceptionMentoriaJava {
		
		CupDesc cupDesc2 = cupDescontoRepository.save(cupDesc);
		
		return new ResponseEntity<CupDesc>(cupDesc2, HttpStatus.OK);
	}
	
	
	@ResponseBody
	@GetMapping(value = "**/listaCupomDesconto")
	public ResponseEntity<List<CupDesc>> listaCupomDesconto() {
		
		return new ResponseEntity<List<CupDesc>>(cupDescontoRepository.findAll(), HttpStatus.OK);
	}
	
	@ResponseBody
	@GetMapping(value = "**/listaCupomDesconto/{idEmpresa}")
    public ResponseEntity<List<CupDesc>> listaCupomDesconto(@PathVariable("idEmpresa") Long idEmpresa) {
		
		return new ResponseEntity<List<CupDesc>>(cupDescontoRepository.cupDescontoPorEmpresa(idEmpresa), HttpStatus.OK);
	}

}
