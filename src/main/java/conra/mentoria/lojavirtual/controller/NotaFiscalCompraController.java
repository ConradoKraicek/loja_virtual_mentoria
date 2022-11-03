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
import conra.mentoria.lojavirtual.model.NotaFiscalCompra;
import conra.mentoria.lojavirtual.model.NotaFiscalVenda;
import conra.mentoria.lojavirtual.repository.NotaFiscalCompraRepository;
import conra.mentoria.lojavirtual.repository.NotaFiscalVendaRepository;

@RestController
public class NotaFiscalCompraController {
	
	@Autowired
	private NotaFiscalCompraRepository notaFiscalCompraRepository;
	
	@Autowired
	private NotaFiscalVendaRepository notaFiscalVendaRepository;
	
	
	
	
	@ResponseBody /*Poder dar um retorno da API*/
	@PostMapping(value = "**/salvarNotaFiscalCompra") /* Mapeando a url para rebeber o JSON */
	public ResponseEntity<NotaFiscalCompra> salvarNotaFiscalCompra(@RequestBody @Valid NotaFiscalCompra notaFiscalCompra) throws ExceptionMentoriaJava { /*Recebe o JSON e converte pra Objeto*/
		
		if (notaFiscalCompra.getId() == null) {
			
			if (notaFiscalCompra.getDescricaoObs() != null) {
				
				List<NotaFiscalCompra> fiscalCompras = notaFiscalCompraRepository.buscaNotaDesc(notaFiscalCompra.getDescricaoObs().toUpperCase().trim());
				
				if (!fiscalCompras.isEmpty()) {
					throw new ExceptionMentoriaJava("Já existe Nota de compra com essa mesma descrição:" + notaFiscalCompra.getDescricaoObs());
				}
			}
		}
		
		if (notaFiscalCompra.getPessoa() == null || notaFiscalCompra.getPessoa().getId() <= 0) {
			throw new ExceptionMentoriaJava("A Pessoa Jurídica da nota fiscal deve ser informada.");
		}
		
		if (notaFiscalCompra.getEmpresa() == null || notaFiscalCompra.getEmpresa().getId() <= 0) {
			throw new ExceptionMentoriaJava("A Empresa responsável deve ser informada.");
		}
		
		if (notaFiscalCompra.getContaPagar() == null || notaFiscalCompra.getContaPagar().getId() <= 0) {
			throw new ExceptionMentoriaJava("A conta a pagar da nota deve ser informada.");
		}
		
		NotaFiscalCompra notaFiscalCompraSalva = notaFiscalCompraRepository.save(notaFiscalCompra);
		
		return new ResponseEntity<NotaFiscalCompra>(notaFiscalCompraSalva, HttpStatus.OK);
	}
	
	
	
	@ResponseBody 
	@DeleteMapping(value = "**/deleteNotaFiscalCompraPorId/{id}") 
	public ResponseEntity<?> deleteNotaFiscalCompraPorId(@PathVariable("id") Long id) {
		
		notaFiscalCompraRepository.deleteItemNotaFiscalCompra(id);/*delete os filhos*/
		notaFiscalCompraRepository.deleteById(id);/*Deleta o pai*/
		
		return new ResponseEntity("Nota Fiscal Compra Removida", HttpStatus.OK);
	}
	
	
	
	@ResponseBody 
	@GetMapping(value = "**/obterNotaFiscalCompra/{id}") 
	public ResponseEntity<NotaFiscalCompra> obterNotaFiscalCompra(@PathVariable("id") Long id) throws ExceptionMentoriaJava {
		
		NotaFiscalCompra notaFiscalCompra = notaFiscalCompraRepository.findById(id).orElse(null);
		
		if (notaFiscalCompra == null) {
			throw new ExceptionMentoriaJava("Não encontrou Nota Fiscal com código:" + id);
		}
		
		return new ResponseEntity<NotaFiscalCompra>(notaFiscalCompra, HttpStatus.OK);
	}
	
	@ResponseBody 
	@GetMapping(value = "**/obterNotaFiscalCompraDaVenda/{idVenda}") 
	public ResponseEntity<List<NotaFiscalVenda>> obterNotaFiscalCompraDaVenda(@PathVariable("idVenda") Long idVenda) throws ExceptionMentoriaJava {
		
		List<NotaFiscalVenda> notaFiscalVenda = notaFiscalVendaRepository.buscaNotaPorVenda(idVenda);
		
		if (notaFiscalVenda == null) {
			throw new ExceptionMentoriaJava("Não encontrou Nota Fiscal de venda com código da venda:" + idVenda);
		}
		
		return new ResponseEntity<List<NotaFiscalVenda>>(notaFiscalVenda, HttpStatus.OK);
	}
	
	@ResponseBody 
	@GetMapping(value = "**/obterNotaFiscalCompraDaVendaUnica/{idVenda}") 
	public ResponseEntity<NotaFiscalVenda> obterNotaFiscalCompraDaVendaUnica(@PathVariable("idVenda") Long idVenda) throws ExceptionMentoriaJava {
		
		NotaFiscalVenda notaFiscalVenda = notaFiscalVendaRepository.buscaNotaPorVendaUnica(idVenda);
		
		if (notaFiscalVenda == null) {
			throw new ExceptionMentoriaJava("Não encontrou Nota Fiscal de venda com código da venda:" + idVenda);
		}
		
		return new ResponseEntity<NotaFiscalVenda>(notaFiscalVenda, HttpStatus.OK);
	}
	
	
	@ResponseBody 
	@GetMapping(value = "**/buscarNotaFiscalPorDescricao/{desc}") 
	public ResponseEntity<List<NotaFiscalCompra>> buscarNotaFiscalPorDescricao(@PathVariable("desc") String desc) {
		
		List<NotaFiscalCompra> notaFiscalCompras = notaFiscalCompraRepository.buscaNotaDesc(desc.toUpperCase().trim());
		
		return new ResponseEntity<List<NotaFiscalCompra>>(notaFiscalCompras, HttpStatus.OK);
	}

}
