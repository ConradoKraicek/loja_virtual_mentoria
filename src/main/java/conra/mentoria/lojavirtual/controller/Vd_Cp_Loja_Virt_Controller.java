package conra.mentoria.lojavirtual.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import javax.mail.MessagingException;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import conra.mentoria.lojavirtual.ExceptionMentoriaJava;
import conra.mentoria.lojavirtual.enums.ApiTokenIntegração;
import conra.mentoria.lojavirtual.enums.StatusContaReceber;
import conra.mentoria.lojavirtual.model.ContaReceber;
import conra.mentoria.lojavirtual.model.Endereco;
import conra.mentoria.lojavirtual.model.ItemVendaLoja;
import conra.mentoria.lojavirtual.model.PessoaFisica;
import conra.mentoria.lojavirtual.model.StatusRastreio;
import conra.mentoria.lojavirtual.model.VendaCompraLojaVirtual;
import conra.mentoria.lojavirtual.model.dto.ConsultaFreteDTO;
import conra.mentoria.lojavirtual.model.dto.EmpresaTransporteDTO;
import conra.mentoria.lojavirtual.model.dto.EnvioEtiquetaDTO;
import conra.mentoria.lojavirtual.model.dto.ItemVendaDTO;
import conra.mentoria.lojavirtual.model.dto.ProductsEnvioEtiquetaDTO;
import conra.mentoria.lojavirtual.model.dto.TagsEnvioDTO;
import conra.mentoria.lojavirtual.model.dto.VendaCompraLojaVirtualDTO;
import conra.mentoria.lojavirtual.model.dto.VolumesEnvioEtiquetaDTO;
import conra.mentoria.lojavirtual.repository.ContaReceberRepository;
import conra.mentoria.lojavirtual.repository.EnderecoRepository;
import conra.mentoria.lojavirtual.repository.NotaFiscalVendaRepository;
import conra.mentoria.lojavirtual.repository.StatusRastreioRepository;
import conra.mentoria.lojavirtual.repository.Vd_Cp_Loja_Virt_Repository;
import conra.mentoria.lojavirtual.service.ServiceSendEmail;
import conra.mentoria.lojavirtual.service.VendaService;
import okhttp3.OkHttpClient;

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
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	
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
	
	@ResponseBody
	@GetMapping(value = "**/imprimeCompraEtiquetaFrete/{idVenda}")
	public ResponseEntity<String> imprimeCompraEtiquetaFrete(@PathVariable Long idVenda) throws ExceptionMentoriaJava, IOException {
		
		VendaCompraLojaVirtual compraLojaVirtual = vd_Cp_Loja_Virt_Repository.findById(idVenda).orElseGet(null);
		
		if (compraLojaVirtual == null) {
			return new ResponseEntity<String>("Venda não encontrada", HttpStatus.OK);
		}
		
		List<Endereco> enderecos = enderecoRepository.enderecoPj(compraLojaVirtual.getEmpresa().getId());
		compraLojaVirtual.getEmpresa().setEnderecos(enderecos);
		
		EnvioEtiquetaDTO envioEtiquetaDTO = new EnvioEtiquetaDTO();
		envioEtiquetaDTO.setService(compraLojaVirtual.getServicoTransportadora());
		envioEtiquetaDTO.setAgency("49");
		
		envioEtiquetaDTO.getFrom().setName(compraLojaVirtual.getEmpresa().getNome());
		envioEtiquetaDTO.getFrom().setPhone(compraLojaVirtual.getEmpresa().getTelefone());
		envioEtiquetaDTO.getFrom().setEmail(compraLojaVirtual.getEmpresa().getEmail());
		envioEtiquetaDTO.getFrom().setCompany_document(compraLojaVirtual.getEmpresa().getCnpj());
		envioEtiquetaDTO.getFrom().setState_register(compraLojaVirtual.getEmpresa().getInscEstadual());
		envioEtiquetaDTO.getFrom().setAddress(compraLojaVirtual.getEmpresa().getEnderecos().get(0).getRuaLogra());
		envioEtiquetaDTO.getFrom().setComplement(compraLojaVirtual.getEmpresa().getEnderecos().get(0).getComplemento());
		envioEtiquetaDTO.getFrom().setNumber(compraLojaVirtual.getEmpresa().getEnderecos().get(0).getNumero());
		envioEtiquetaDTO.getFrom().setDistrict(compraLojaVirtual.getEmpresa().getEnderecos().get(0).getEstado());
		envioEtiquetaDTO.getFrom().setCity(compraLojaVirtual.getEmpresa().getEnderecos().get(0).getCidade());
		envioEtiquetaDTO.getFrom().setCountry_id(compraLojaVirtual.getEmpresa().getEnderecos().get(0).getUf());
		envioEtiquetaDTO.getFrom().setPostal_code(compraLojaVirtual.getEmpresa().getEnderecos().get(0).getCep());
		envioEtiquetaDTO.getFrom().setNote("Não Há");
		
		envioEtiquetaDTO.getTo().setName(compraLojaVirtual.getPessoa().getNome());
		envioEtiquetaDTO.getTo().setPhone(compraLojaVirtual.getPessoa().getTelefone());
		envioEtiquetaDTO.getTo().setEmail(compraLojaVirtual.getPessoa().getEmail());
		envioEtiquetaDTO.getTo().setDocument(compraLojaVirtual.getPessoa().getCpf());
		envioEtiquetaDTO.getTo().setAddress(compraLojaVirtual.getPessoa().enderecoEntrega().getRuaLogra());
		envioEtiquetaDTO.getTo().setComplement(compraLojaVirtual.getPessoa().enderecoEntrega().getComplemento());
		envioEtiquetaDTO.getTo().setNumber(compraLojaVirtual.getPessoa().enderecoEntrega().getNumero());
		envioEtiquetaDTO.getTo().setDistrict(compraLojaVirtual.getPessoa().enderecoEntrega().getEstado());
		envioEtiquetaDTO.getTo().setCity(compraLojaVirtual.getPessoa().enderecoEntrega().getCidade());
		envioEtiquetaDTO.getTo().setState_abbr(compraLojaVirtual.getPessoa().enderecoEntrega().getEstado());
		envioEtiquetaDTO.getTo().setCountry_id(compraLojaVirtual.getPessoa().enderecoEntrega().getUf());
		envioEtiquetaDTO.getTo().setPostal_code(compraLojaVirtual.getPessoa().enderecoEntrega().getCep());
		envioEtiquetaDTO.getTo().setNote("Não Há");
		
		List<ProductsEnvioEtiquetaDTO> products = new ArrayList<>();
		
		for (ItemVendaLoja itemVendaLoja : compraLojaVirtual.getItemVendaLojas()) {
			ProductsEnvioEtiquetaDTO dto = new ProductsEnvioEtiquetaDTO();
			dto.setName(itemVendaLoja.getProduto().getNome());
			dto.setQuantity(itemVendaLoja.getQuantidade().toString());
			dto.setUnitary_value("" + itemVendaLoja.getProduto().getValorVenda().doubleValue());
			
			products.add(dto);
		}
		
		envioEtiquetaDTO.setProducts(products);
		
		
		List<VolumesEnvioEtiquetaDTO> volumes = new ArrayList<>();
		
		for (ItemVendaLoja itemVendaLoja : compraLojaVirtual.getItemVendaLojas()) {
			VolumesEnvioEtiquetaDTO dto = new VolumesEnvioEtiquetaDTO();
			dto.setHeight(itemVendaLoja.getProduto().getAltura().toString());
			dto.setLength(itemVendaLoja.getProduto().getProfundidade().toString());
			dto.setWeight(itemVendaLoja.getProduto().getPeso().toString());
			dto.setWidth(itemVendaLoja.getProduto().getLargura().toString());
			
			volumes.add(dto);
		}
		
		envioEtiquetaDTO.setVolumes(volumes);
		
		envioEtiquetaDTO.getOptions().setInsurance_value("" + compraLojaVirtual.getValorTotal().doubleValue());
		envioEtiquetaDTO.getOptions().setReceipt(false);
		envioEtiquetaDTO.getOptions().setOwn_hand(false);
		envioEtiquetaDTO.getOptions().setReverse(false);
		envioEtiquetaDTO.getOptions().setNon_commercial(false);
		envioEtiquetaDTO.getOptions().getInvoice().setKey(compraLojaVirtual.getNotaFiscalVenda().getNumero());
		envioEtiquetaDTO.getOptions().setPlatform(compraLojaVirtual.getEmpresa().getNomeFantasia());
		
		TagsEnvioDTO dtoTagEnvio = new TagsEnvioDTO();
		dtoTagEnvio.setTag("Identificação do pedido na plataforma, exemplo:" + compraLojaVirtual.getId());
		dtoTagEnvio.setUrl(null);
		
		envioEtiquetaDTO.getOptions().getTags().add(dtoTagEnvio);
		
		
		String jsonEnvio = new ObjectMapper().writeValueAsString(envioEtiquetaDTO);
		
		OkHttpClient client = new OkHttpClient().newBuilder().build();
		okhttp3.MediaType mediaType = okhttp3.MediaType.parse("application/json");
		okhttp3.RequestBody body = okhttp3.RequestBody.create(mediaType, jsonEnvio);
		okhttp3.Request request = new okhttp3.Request.Builder()
		  .url(ApiTokenIntegração.URL_MELHOR_ENVIO_SAND_BOX + "api/v2/me/cart")
		  .method("POST", body)
		  .addHeader("Accept", "application/json")
		  .addHeader("Content-Type", "application/json")
		  .addHeader("Authorization", "Bearer " + ApiTokenIntegração.TOKEN_MELHOR_ENVIO_SAND_BOX)
		  .addHeader("User-Agent", "conradoempresa123@gmail.com")
		  .build();
		okhttp3.Response response = client.newCall(request).execute();
		
		String respostaJson = response.body().string();
		
		if (respostaJson.contains("error")) {
			throw new ExceptionMentoriaJava(respostaJson);
		}
		
        JsonNode jsonNode = new ObjectMapper().readTree(respostaJson);
		
		Iterator<JsonNode> iterator = jsonNode.iterator();
		
		String idEtiqueta = "";
		
        while (iterator.hasNext()) {
			JsonNode node = iterator.next();
			if (node.get("id") != null) {
				idEtiqueta = node.get("id").asText();
			} else {
				idEtiqueta = node.asText();
			}
			break;

        }
        
        /*Salvando o código da etiqueta*/
        jdbcTemplate.execute("begin; update vd_cp_loja_virt set codigo_etiqueta = '"+idEtiqueta+"' where id = "+compraLojaVirtual.getId()+" ;commit;");
        //vd_Cp_Loja_Virt_Repository.updateEtiqueta(idEtiqueta, compraLojaVirtual.getId());
        
        
        OkHttpClient clientCompra = new OkHttpClient().newBuilder().build();
        okhttp3.MediaType mediaTypeC = okhttp3.MediaType.parse("application/json");
        okhttp3.RequestBody bodyC = okhttp3.RequestBody.create(mediaTypeC, "{\n    \"orders\": [\n        \"" +idEtiqueta+"\"\n    ]\n}");
        okhttp3.Request requestC = new okhttp3.Request.Builder()
		  .url(ApiTokenIntegração.URL_MELHOR_ENVIO_SAND_BOX + "api/v2/me/shipment/checkout")
		  .method("POST", bodyC)
		  .addHeader("Accept", "application/json")
		  .addHeader("Content-Type", "application/json")
		  .addHeader("Authorization", "Bearer " + ApiTokenIntegração.TOKEN_MELHOR_ENVIO_SAND_BOX)
		  .addHeader("User-Agent", "conradoempresa123@gmail.com")
		  .build();
        okhttp3.Response responseC = clientCompra.newCall(requestC).execute();
        
        if (!responseC.isSuccessful()) {
        	return new ResponseEntity<String>("Não foi possível realizar a compra da etiqueta", HttpStatus.OK);
		}
        
        
        /*Gerando o código da etiqueta*/
        OkHttpClient clientGe = new OkHttpClient().newBuilder().build();
        okhttp3.MediaType mediaTypeGe = okhttp3.MediaType.parse("application/json");
        okhttp3.RequestBody bodyGe = okhttp3.RequestBody.create(mediaTypeGe, "{\n    \"orders\":[\n        \""+idEtiqueta+"\"\n    ]\n}");
        okhttp3.Request requestGe = new okhttp3.Request.Builder()
		  .url(ApiTokenIntegração.URL_MELHOR_ENVIO_SAND_BOX + "api/v2/me/shipment/generate")
		  .method("POST", bodyGe)
		  .addHeader("Accept", "application/json")
		  .addHeader("Content-Type", "application/json")
		  .addHeader("Authorization", "Bearer " + ApiTokenIntegração.TOKEN_MELHOR_ENVIO_SAND_BOX)
		  .addHeader("User-Agent", "conradoempresa123@gmail.com")
		  .build();
		
        okhttp3.Response responseGe = clientGe.newCall(requestGe).execute();
        
        if (!responseGe.isSuccessful()) {
        	return new ResponseEntity<String>("Não foi possível gerar a etiqueta", HttpStatus.OK);
		}
        
       
        /*Faz impressão das etiquetas*/
        OkHttpClient clientIm = new OkHttpClient().newBuilder().build();
        okhttp3.MediaType mediaTypeIm = okhttp3.MediaType.parse("application/json");
        okhttp3.RequestBody bodyIm = okhttp3.RequestBody.create(mediaTypeIm, "{\n    \"mode\": \"private\",\n    \"orders\": [\n        \""+idEtiqueta+"\"\n    ]\n}");
        okhttp3.Request requestIm = new okhttp3.Request.Builder()
		  .url(ApiTokenIntegração.URL_MELHOR_ENVIO_SAND_BOX + "api/v2/me/shipment/print")
		  .method("POST", bodyIm)
		  .addHeader("Accept", "application/json")
		  .addHeader("Content-Type", "application/json")
		  .addHeader("Authorization", "Bearer " + ApiTokenIntegração.TOKEN_MELHOR_ENVIO_SAND_BOX)
		  .addHeader("User-Agent", "conradoempresa123@gmail.com")
		  .build();
        okhttp3.Response responseIm = clientIm.newCall(requestIm).execute();
        
        if (!responseIm.isSuccessful()) {
        	return new ResponseEntity<String>("Não foi possível imprimir a etiqueta", HttpStatus.OK);
		}
        
        String urlEtiqueta = responseIm.body().string();
        
        /*Salvando a url da etiqueta*/
        jdbcTemplate.execute("begin; update vd_cp_loja_virt set url_imprime_etiqueta = '"+urlEtiqueta+"' where id = "+compraLojaVirtual.getId()+" ;commit;");
        //vd_Cp_Loja_Virt_Repository.updateUrlEtiqueta(urlEtiqueta, compraLojaVirtual.getId());
		
		return new ResponseEntity<String>("Sucesso", HttpStatus.OK);
		
	}
	
	@ResponseBody
	@PostMapping(value = "**/consultarFreteLojaVirtual")
	public ResponseEntity<List<EmpresaTransporteDTO>> consultaFrete(@RequestBody @Valid ConsultaFreteDTO consultaFreteDTO) throws Exception {
		
		ObjectMapper objectMapper = new ObjectMapper();
		String json = objectMapper.writeValueAsString(consultaFreteDTO);
		
		OkHttpClient client = new OkHttpClient().newBuilder().build(); /*Instancia o objeto de requisição*/
		okhttp3.MediaType mediaType = okhttp3.MediaType.parse("application/json"); /*Tipo do dados JSON*/
		okhttp3.RequestBody body = okhttp3.RequestBody.create(mediaType, json); /*Entrada do JSON*/
		okhttp3.Request request = new okhttp3.Request.Builder()
		  .url(ApiTokenIntegração.URL_MELHOR_ENVIO_SAND_BOX + "api/v2/me/shipment/calculate")
		  .method("POST", body)
		  .addHeader("Accept", "application/json")
		  .addHeader("Content-Type", "application/json")
		  .addHeader("Authorization", "Bearer " + ApiTokenIntegração.TOKEN_MELHOR_ENVIO_SAND_BOX)
		  .addHeader("User-Agent", "conradoempresa123@gmail.com")
		  .build();
		okhttp3.Response response = client.newCall(request).execute();
		
		
		JsonNode jsonNode = new ObjectMapper().readTree(response.body().string());
		
		Iterator<JsonNode> iterator = jsonNode.iterator();
		
		List<EmpresaTransporteDTO> empresaTransporteDTOs = new ArrayList<EmpresaTransporteDTO>();
		
		while (iterator.hasNext()) {
			
			JsonNode node = iterator.next();
			
			EmpresaTransporteDTO empresaTransporteDTO = new EmpresaTransporteDTO();
			
			if (node.get("id") != null) {
				empresaTransporteDTO.setId(node.get("id").asText());
			}
			
			if (node.get("name") != null) {
				empresaTransporteDTO.setNome(node.get("name").asText());
			}
			
			if (node.get("price") != null) {
				empresaTransporteDTO.setValor(node.get("price").asText());
			}
			
			if (node.get("company") != null) {
				empresaTransporteDTO.setEmpresa(node.get("company").get("name").asText());
				empresaTransporteDTO.setPicture(node.get("company").get("picture").asText());
			}
			
			if (empresaTransporteDTO.dadosOK()) {
				
				empresaTransporteDTOs.add(empresaTransporteDTO);
			}
			
		}
		
		
		return new ResponseEntity<List<EmpresaTransporteDTO>>(empresaTransporteDTOs, HttpStatus.OK);
		
	}
}
