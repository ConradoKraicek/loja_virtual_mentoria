package conra.mentoria.lojavirtual.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import conra.mentoria.lojavirtual.ExceptionMentoriaJava;
import conra.mentoria.lojavirtual.model.FormaPagamento;
import conra.mentoria.lojavirtual.repository.FormaPagamentoRepository;

@RestController
public class FormaPagamentoController {
     
	 @Autowired
	 private FormaPagamentoRepository formaPagamentoRepository;
	 
	 
	 @ResponseBody 
		@PostMapping(value = "**/salvarFormaPagamento") 
		public ResponseEntity<FormaPagamento> salvarFormaPagamento(@RequestBody @Valid FormaPagamento formaPagamento ) throws ExceptionMentoriaJava { 
			
			
		    formaPagamento = formaPagamentoRepository.save(formaPagamento);
			
			return new ResponseEntity<FormaPagamento>(formaPagamento, HttpStatus.OK);
		}
	 
}
