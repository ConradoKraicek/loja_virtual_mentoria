package conra.mentoria.lojavirtual.controller;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.mail.MessagingException;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import conra.mentoria.lojavirtual.ExceptionMentoriaJava;
import conra.mentoria.lojavirtual.enums.StatusContaReceber;
import conra.mentoria.lojavirtual.model.ContaReceber;
import conra.mentoria.lojavirtual.model.Endereco;
import conra.mentoria.lojavirtual.model.ItemVendaLoja;
import conra.mentoria.lojavirtual.model.PessoaFisica;
import conra.mentoria.lojavirtual.model.StatusRastreio;
import conra.mentoria.lojavirtual.model.VendaCompraLojaVirtual;
import conra.mentoria.lojavirtual.model.dto.ItemVendaDTO;
import conra.mentoria.lojavirtual.model.dto.VendaCompraLojaVirtualDTO;
import conra.mentoria.lojavirtual.repository.ContaReceberRepository;
import conra.mentoria.lojavirtual.repository.EnderecoRepository;
import conra.mentoria.lojavirtual.repository.NotaFiscalVendaRepository;
import conra.mentoria.lojavirtual.repository.StatusRastreioRepository;
import conra.mentoria.lojavirtual.repository.Vd_Cp_Loja_Virt_Repository;
import conra.mentoria.lojavirtual.service.ServiceSendEmail;
import conra.mentoria.lojavirtual.service.VendaService;

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
	
	@Autowired
	private StatusRastreioRepository statusRastreioRepository;
	
	@Autowired
	private VendaService vendaService;
	
	@Autowired
	private ContaReceberRepository contaReceberRepository;
	
	@Autowired
	private ServiceSendEmail serviceSendEmail;
	
	
	@ResponseBody
	@PostMapping(value = "**/salvarVendaCompraLoja")
	public ResponseEntity<VendaCompraLojaVirtualDTO> salvarVendaCompraLoja(@RequestBody @Valid VendaCompraLojaVirtual vendaCompraLojaVirtual) throws ExceptionMentoriaJava, UnsupportedEncodingException, MessagingException {
		
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
		
		
		for (int i = 0; i < vendaCompraLojaVirtual.getItemVendaLojas().size(); i++) {
			
			vendaCompraLojaVirtual.getItemVendaLojas().get(i).setEmpresa(vendaCompraLojaVirtual.getEmpresa());
			vendaCompraLojaVirtual.getItemVendaLojas().get(i).setVendaCompraLojaVirtual(vendaCompraLojaVirtual);
		}
		
		/*Salva primeiro a venda e todos os dados*/
		vendaCompraLojaVirtual = vd_Cp_Loja_Virt_Repository.saveAndFlush(vendaCompraLojaVirtual);
		
		StatusRastreio statusRastreio = new StatusRastreio();
		statusRastreio.setCentroDistribuicao("Loja Local");
		statusRastreio.setCidade("Local");
		statusRastreio.setEmpresa(vendaCompraLojaVirtual.getEmpresa());
		statusRastreio.setEstado("Local");
		statusRastreio.setStatus("Inicaio compra");
		statusRastreio.setVendaCompraLojaVirtual(vendaCompraLojaVirtual);
		statusRastreioRepository.save(statusRastreio);
		
		
		/*Associa a venda gravada no banco com a nota fiscal*/
		vendaCompraLojaVirtual.getNotaFiscalVenda().setVendaCompraLojaVirtual(vendaCompraLojaVirtual);
		
		/*Persiste novamente a nota fiscal pra ficar amarrada na venda*/
		notaFiscalVendaRepository.saveAndFlush(vendaCompraLojaVirtual.getNotaFiscalVenda());
		
		VendaCompraLojaVirtualDTO compraLojaVirtualDTO = new VendaCompraLojaVirtualDTO();
		compraLojaVirtualDTO.setValorTotal(vendaCompraLojaVirtual.getValorTotal());
		compraLojaVirtualDTO.setPessoa(vendaCompraLojaVirtual.getPessoa());
		compraLojaVirtualDTO.setEntrega(vendaCompraLojaVirtual.getEnderecoEntrega());
		compraLojaVirtualDTO.setCobranca(vendaCompraLojaVirtual.getEnderecoCobranca());
		compraLojaVirtualDTO.setValorDesc(vendaCompraLojaVirtual.getValorDesconto());
		compraLojaVirtualDTO.setValorFrete(vendaCompraLojaVirtual.getValorFrete());
		compraLojaVirtualDTO.setId(vendaCompraLojaVirtual.getId());
		
		for (ItemVendaLoja item: vendaCompraLojaVirtual.getItemVendaLojas() ) {
			
			ItemVendaDTO itemVendaDTO = new ItemVendaDTO();
			itemVendaDTO.setQuantidade(item.getQuantidade());
			itemVendaDTO.setProduto(item.getProduto());
			
			compraLojaVirtualDTO.getItemVendaLojas().add(itemVendaDTO);
		}
		
		ContaReceber contaReceber = new ContaReceber();
		contaReceber.setDescricao("Venda da loja virtual nº: " + vendaCompraLojaVirtual.getId());
		contaReceber.setDtPagamento(Calendar.getInstance().getTime());
		contaReceber.setDtVencimento(Calendar.getInstance().getTime());
		contaReceber.setEmpresa(vendaCompraLojaVirtual.getEmpresa());
		contaReceber.setPessoa(vendaCompraLojaVirtual.getPessoa());
		contaReceber.setStatus(StatusContaReceber.QUITADA);
		contaReceber.setValorDesconto(vendaCompraLojaVirtual.getValorDesconto());
		contaReceber.setValorTotal(vendaCompraLojaVirtual.getValorTotal());
		
		contaReceberRepository.saveAndFlush(contaReceber);
		
		/*Email para o comprador*/
		StringBuilder msgEmail = new StringBuilder();
		msgEmail.append("Olá, ").append(pessoaFisica.getNome()).append("</br>");
		msgEmail.append("você realizou a compra de nº: ").append(vendaCompraLojaVirtual.getId()).append("</br>");
		msgEmail.append("Na loja ").append(vendaCompraLojaVirtual.getEmpresa().getNomeFantasia());
		
		/*assunto, msg, destino*/
		serviceSendEmail.enviarEmailHtml("Compra Realizada", msgEmail.toString(), pessoaFisica.getEmail());
		
		/*Email para o vendedor*/
		msgEmail = new StringBuilder();
		msgEmail.append("você realizou uma venda de nº: ").append(vendaCompraLojaVirtual.getId());
		
		/*assunto, msg, destino*/
		serviceSendEmail.enviarEmailHtml("Venda Realizada", msgEmail.toString(), vendaCompraLojaVirtual.getEmpresa().getEmail());
	

		return new ResponseEntity<VendaCompraLojaVirtualDTO>(compraLojaVirtualDTO, HttpStatus.OK); 


	}
	
	@ResponseBody
	@GetMapping(value = "**/consultaVendaId/{id}")
    public ResponseEntity<VendaCompraLojaVirtualDTO> consultaVendaId(@PathVariable("id") Long idVenda) {
		
		VendaCompraLojaVirtual vendaCompraLojaVirtual = vd_Cp_Loja_Virt_Repository.findByIdExclusao(idVenda);
		
		if (vendaCompraLojaVirtual == null) {
			
			vendaCompraLojaVirtual = new VendaCompraLojaVirtual();
		}
		
		VendaCompraLojaVirtualDTO compraLojaVirtualDTO = new VendaCompraLojaVirtualDTO();
		
		compraLojaVirtualDTO.setValorTotal(vendaCompraLojaVirtual.getValorTotal());
		compraLojaVirtualDTO.setPessoa(vendaCompraLojaVirtual.getPessoa());
		compraLojaVirtualDTO.setEntrega(vendaCompraLojaVirtual.getEnderecoEntrega());
		compraLojaVirtualDTO.setCobranca(vendaCompraLojaVirtual.getEnderecoCobranca());
		compraLojaVirtualDTO.setValorDesc(vendaCompraLojaVirtual.getValorDesconto());
		compraLojaVirtualDTO.setValorFrete(vendaCompraLojaVirtual.getValorFrete());
		compraLojaVirtualDTO.setId(vendaCompraLojaVirtual.getId());
		
        for (ItemVendaLoja item: vendaCompraLojaVirtual.getItemVendaLojas() ) {
			
			ItemVendaDTO itemVendaDTO = new ItemVendaDTO();
			itemVendaDTO.setQuantidade(item.getQuantidade());
			itemVendaDTO.setProduto(item.getProduto());
			
			compraLojaVirtualDTO.getItemVendaLojas().add(itemVendaDTO);
		}
		
		return new ResponseEntity<VendaCompraLojaVirtualDTO>(compraLojaVirtualDTO, HttpStatus.OK); 
    	
    }	
	
	@ResponseBody
	@DeleteMapping(value = "**/deleteVendaTotalBanco/{idVenda}")
	public ResponseEntity<String> deleteVendaTotalBanco(@PathVariable(value = "idVenda") Long idVenda) {
		
		vendaService.exclusaoTotalVendaBanco(idVenda);
		
		return new ResponseEntity<String>("Venda Excluída com sucesso", HttpStatus.OK);
	}

	@ResponseBody
	@DeleteMapping(value = "**/deleteVendaTotalBanco2/{idVenda}")
	public ResponseEntity<String> deleteVendaTotalBanco2(@PathVariable(value = "idVenda") Long idVenda) {
		
		vendaService.exclusaoTotalVendaBanco2(idVenda);
		
		return new ResponseEntity<String>("Venda Excluída logicamente com sucesso", HttpStatus.OK);
	}
	
	
	@ResponseBody
	@PutMapping(value = "**/ativaRegistroVendaBanco/{idVenda}")
	public ResponseEntity<String> ativaRegistroVendaBanco(@PathVariable(value = "idVenda") Long idVenda) {
		
		vendaService.ativaRegistroVendaBanco(idVenda);
		
		return new ResponseEntity<String>("Venda ativada com sucesso", HttpStatus.OK);
	}
	
	@ResponseBody
	@GetMapping(value = "**/consultaVendaPorProdutoId/{id}")
    public ResponseEntity<List<VendaCompraLojaVirtualDTO>> consultaVendaPorProdutoId(@PathVariable("id") Long idProduto) {
		
		List<VendaCompraLojaVirtual> vendaCompraLojaVirtual = vd_Cp_Loja_Virt_Repository.vendaPorProduto(idProduto);
		
		if (vendaCompraLojaVirtual == null) {
			
			vendaCompraLojaVirtual = new ArrayList<VendaCompraLojaVirtual>();
		}
		
		List<VendaCompraLojaVirtualDTO> compraLojaVirtualDTOList = new ArrayList<VendaCompraLojaVirtualDTO>();
		
		for (VendaCompraLojaVirtual vcl : vendaCompraLojaVirtual) {
			
			VendaCompraLojaVirtualDTO compraLojaVirtualDTO = new VendaCompraLojaVirtualDTO();
			
			compraLojaVirtualDTO.setValorTotal(vcl.getValorTotal());
			compraLojaVirtualDTO.setPessoa(vcl.getPessoa());
			compraLojaVirtualDTO.setEntrega(vcl.getEnderecoEntrega());
			compraLojaVirtualDTO.setCobranca(vcl.getEnderecoCobranca());
			compraLojaVirtualDTO.setValorDesc(vcl.getValorDesconto());
			compraLojaVirtualDTO.setValorFrete(vcl.getValorFrete());
			compraLojaVirtualDTO.setId(vcl.getId());
		
	        for (ItemVendaLoja item: vcl.getItemVendaLojas() ) {
				
				ItemVendaDTO itemVendaDTO = new ItemVendaDTO();
				itemVendaDTO.setQuantidade(item.getQuantidade());
				itemVendaDTO.setProduto(item.getProduto());
				
				compraLojaVirtualDTO.getItemVendaLojas().add(itemVendaDTO);
			}
        
	        compraLojaVirtualDTOList.add(compraLojaVirtualDTO);
        
		}
		
		
		return new ResponseEntity<List<VendaCompraLojaVirtualDTO>>(compraLojaVirtualDTOList, HttpStatus.OK); 
    	
    }	
	
	@ResponseBody
	@GetMapping(value = "**/consultaVendaDinamicaFaixaData/{data1}/{data2}")
	public ResponseEntity<List<VendaCompraLojaVirtualDTO>> consultaVendaDinamicaFaixaData(@PathVariable("data1") String data1, @PathVariable("data2") String data2) throws ParseException {
		
		List<VendaCompraLojaVirtual> vendaCompraLojaVirtual = null;
		
		vendaCompraLojaVirtual = vendaService.consultaVendaFaixaData(data1, data2);
		
		
		if (vendaCompraLojaVirtual == null) {

			vendaCompraLojaVirtual = new ArrayList<VendaCompraLojaVirtual>();
		}
		
		List<VendaCompraLojaVirtualDTO> compraLojaVirtualDTOList = new ArrayList<VendaCompraLojaVirtualDTO>();
		
		for (VendaCompraLojaVirtual vcl : vendaCompraLojaVirtual) {
			
			VendaCompraLojaVirtualDTO compraLojaVirtualDTO = new VendaCompraLojaVirtualDTO();
			
			compraLojaVirtualDTO.setValorTotal(vcl.getValorTotal());
			compraLojaVirtualDTO.setPessoa(vcl.getPessoa());
			compraLojaVirtualDTO.setEntrega(vcl.getEnderecoEntrega());
			compraLojaVirtualDTO.setCobranca(vcl.getEnderecoCobranca());
			compraLojaVirtualDTO.setValorDesc(vcl.getValorDesconto());
			compraLojaVirtualDTO.setValorFrete(vcl.getValorFrete());
			compraLojaVirtualDTO.setId(vcl.getId());
		
	        for (ItemVendaLoja item: vcl.getItemVendaLojas() ) {
				
				ItemVendaDTO itemVendaDTO = new ItemVendaDTO();
				itemVendaDTO.setQuantidade(item.getQuantidade());
				itemVendaDTO.setProduto(item.getProduto());
				
				compraLojaVirtualDTO.getItemVendaLojas().add(itemVendaDTO);
			}
        
	        compraLojaVirtualDTOList.add(compraLojaVirtualDTO);
        
		}
		
		return new ResponseEntity<List<VendaCompraLojaVirtualDTO>>(compraLojaVirtualDTOList, HttpStatus.OK); 
		
	}
	
	@ResponseBody
	@GetMapping(value = "**/consultaVendaDinamica/{valor}/{tipoConsulta}")
    public ResponseEntity<List<VendaCompraLojaVirtualDTO>> consultaVendaDinamica(@PathVariable("valor") String valor, @PathVariable("tipoConsulta") String tipoConsulta ) {
		
		List<VendaCompraLojaVirtual> vendaCompraLojaVirtual = null;
		
		if (tipoConsulta.equalsIgnoreCase("POR_ID_PROD")) {
			vendaCompraLojaVirtual = vd_Cp_Loja_Virt_Repository.vendaPorProduto(Long.parseLong(valor));
		} else if (tipoConsulta.equalsIgnoreCase("POR_NOME_PROD")) {
			vendaCompraLojaVirtual = vd_Cp_Loja_Virt_Repository.vendaPorNomeProduto(valor.toUpperCase().trim());
		} else if (tipoConsulta.equalsIgnoreCase("POR_NOME_CLIENTE")) {
			vendaCompraLojaVirtual = vd_Cp_Loja_Virt_Repository.vendaPorNomeCliente(valor.toUpperCase().trim());
		} else if (tipoConsulta.equalsIgnoreCase("POR_ENDERECO_COBRANCA")) {
			vendaCompraLojaVirtual = vd_Cp_Loja_Virt_Repository.vendaPorEnderecoCobranca(valor.toUpperCase().trim());
		} else if (tipoConsulta.equalsIgnoreCase("POR_ENDERECO_ENTREGA")) {
			vendaCompraLojaVirtual = vd_Cp_Loja_Virt_Repository.vendaPorEnderecoEntrega(valor.toUpperCase().trim());
		}
		
		if (vendaCompraLojaVirtual == null) {
			
			vendaCompraLojaVirtual = new ArrayList<VendaCompraLojaVirtual>();
		}
		
		List<VendaCompraLojaVirtualDTO> compraLojaVirtualDTOList = new ArrayList<VendaCompraLojaVirtualDTO>();
		
		for (VendaCompraLojaVirtual vcl : vendaCompraLojaVirtual) {
			
			VendaCompraLojaVirtualDTO compraLojaVirtualDTO = new VendaCompraLojaVirtualDTO();
			
			compraLojaVirtualDTO.setValorTotal(vcl.getValorTotal());
			compraLojaVirtualDTO.setPessoa(vcl.getPessoa());
			compraLojaVirtualDTO.setEntrega(vcl.getEnderecoEntrega());
			compraLojaVirtualDTO.setCobranca(vcl.getEnderecoCobranca());
			compraLojaVirtualDTO.setValorDesc(vcl.getValorDesconto());
			compraLojaVirtualDTO.setValorFrete(vcl.getValorFrete());
			compraLojaVirtualDTO.setId(vcl.getId());
		
	        for (ItemVendaLoja item: vcl.getItemVendaLojas() ) {
				
				ItemVendaDTO itemVendaDTO = new ItemVendaDTO();
				itemVendaDTO.setQuantidade(item.getQuantidade());
				itemVendaDTO.setProduto(item.getProduto());
				
				compraLojaVirtualDTO.getItemVendaLojas().add(itemVendaDTO);
			}
        
	        compraLojaVirtualDTOList.add(compraLojaVirtualDTO);
        
		}
		
		
		return new ResponseEntity<List<VendaCompraLojaVirtualDTO>>(compraLojaVirtualDTOList, HttpStatus.OK); 
    	
    }	
	
	@ResponseBody
	@GetMapping(value = "**/vendaPorCliente/{idCliente}")
    public ResponseEntity<List<VendaCompraLojaVirtualDTO>> vendaPorCliente(@PathVariable("idCliente") Long idCliente) {
		
		List<VendaCompraLojaVirtual> vendaCompraLojaVirtual = vd_Cp_Loja_Virt_Repository.vendaPorCliente(idCliente);
		
		if (vendaCompraLojaVirtual == null) {
			
			vendaCompraLojaVirtual = new ArrayList<VendaCompraLojaVirtual>();
		}
		
		List<VendaCompraLojaVirtualDTO> compraLojaVirtualDTOList = new ArrayList<VendaCompraLojaVirtualDTO>();
		
		for (VendaCompraLojaVirtual vcl : vendaCompraLojaVirtual) {
			
			VendaCompraLojaVirtualDTO compraLojaVirtualDTO = new VendaCompraLojaVirtualDTO();
			
			compraLojaVirtualDTO.setValorTotal(vcl.getValorTotal());
			compraLojaVirtualDTO.setPessoa(vcl.getPessoa());
			compraLojaVirtualDTO.setEntrega(vcl.getEnderecoEntrega());
			compraLojaVirtualDTO.setCobranca(vcl.getEnderecoCobranca());
			compraLojaVirtualDTO.setValorDesc(vcl.getValorDesconto());
			compraLojaVirtualDTO.setValorFrete(vcl.getValorFrete());
			compraLojaVirtualDTO.setId(vcl.getId());
		
	        for (ItemVendaLoja item: vcl.getItemVendaLojas() ) {
				
				ItemVendaDTO itemVendaDTO = new ItemVendaDTO();
				itemVendaDTO.setQuantidade(item.getQuantidade());
				itemVendaDTO.setProduto(item.getProduto());
				
				compraLojaVirtualDTO.getItemVendaLojas().add(itemVendaDTO);
			}
        
	        compraLojaVirtualDTOList.add(compraLojaVirtualDTO);
        
		}
		
		
		return new ResponseEntity<List<VendaCompraLojaVirtualDTO>>(compraLojaVirtualDTOList, HttpStatus.OK); 
    	
    }	
}
