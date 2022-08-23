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
import conra.mentoria.lojavirtual.model.Endereco;
import conra.mentoria.lojavirtual.model.PessoaFisica;
import conra.mentoria.lojavirtual.model.VendaCompraLojaVirtual;
import conra.mentoria.lojavirtual.model.dto.VendaCompraLojaVirtualDTO;
import conra.mentoria.lojavirtual.repository.EnderecoRepository;
import conra.mentoria.lojavirtual.repository.NotaFiscalVendaRepository;
import conra.mentoria.lojavirtual.repository.Vd_Cp_Loja_Virt_Repository;

@RestController
public class Vd_Cp_Loja_Virt_Controller {
	
	@Autowired
	private Vd_Cp_Loja_Virt_Repository vd_Cp_Loja_Virt_Repository;
	
	@Autowired
	private EnderecoRepository enderecoRepository;
	
	@Autowired
	private PessoaController pessoaController;
	
	@Autowired
	private NotaFiscalVendaRepository notaFiscalVendaRepository;
	
	@ResponseBody
	@PostMapping(value = "**/salvarVendaCompraLoja")
	public ResponseEntity<VendaCompraLojaVirtualDTO> salvarVendaCompraLoja(@RequestBody @Valid VendaCompraLojaVirtual vendaCompraLojaVirtual) throws ExceptionMentoriaJava {
		
		vendaCompraLojaVirtual.getPessoa().setEmpresa(vendaCompraLojaVirtual.getEmpresa());
		PessoaFisica pessoaFisica = pessoaController.salvarPf(vendaCompraLojaVirtual.getPessoa()).getBody();
		vendaCompraLojaVirtual.setPessoa(pessoaFisica);
		
		vendaCompraLojaVirtual.getEnderecoCobranca().setPessoa(pessoaFisica);
		vendaCompraLojaVirtual.getEnderecoCobranca().setEmpresa(vendaCompraLojaVirtual.getEmpresa());
		Endereco enderecoCobranca = enderecoRepository.save(vendaCompraLojaVirtual.getEnderecoCobranca());
		vendaCompraLojaVirtual.setEnderecoCobranca(enderecoCobranca);
		
		vendaCompraLojaVirtual.getEnderecoEntrega().setPessoa(pessoaFisica);
		vendaCompraLojaVirtual.getEnderecoEntrega().setEmpresa(vendaCompraLojaVirtual.getEmpresa());
		Endereco enderecoEntrega = enderecoRepository.save(vendaCompraLojaVirtual.getEnderecoEntrega());
		vendaCompraLojaVirtual.setEnderecoEntrega(enderecoEntrega);
		
		vendaCompraLojaVirtual.getNotaFiscalVenda().setEmpresa(vendaCompraLojaVirtual.getEmpresa());
		
		/*Salva primeiro a venda e todos os dados*/
		vendaCompraLojaVirtual = vd_Cp_Loja_Virt_Repository.saveAndFlush(vendaCompraLojaVirtual);
		
		/*Associa a venda gravada no banco com a nota fiscal*/
		vendaCompraLojaVirtual.getNotaFiscalVenda().setVendaCompraLojaVirtual(vendaCompraLojaVirtual);
		
		/*Persiste novamente a nota fiscal pra ficar amarrada na venda*/
		notaFiscalVendaRepository.saveAndFlush(vendaCompraLojaVirtual.getNotaFiscalVenda());
		
		VendaCompraLojaVirtualDTO compraLojaVirtualDTO = new VendaCompraLojaVirtualDTO();
		compraLojaVirtualDTO.setValorTotal(vendaCompraLojaVirtual.getValorTotal());

		return new ResponseEntity<VendaCompraLojaVirtualDTO>(compraLojaVirtualDTO, HttpStatus.OK); 


	}

}
