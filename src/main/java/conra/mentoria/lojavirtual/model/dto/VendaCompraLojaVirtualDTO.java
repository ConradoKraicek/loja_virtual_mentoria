package conra.mentoria.lojavirtual.model.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import conra.mentoria.lojavirtual.model.Endereco;
import conra.mentoria.lojavirtual.model.Pessoa;

public class VendaCompraLojaVirtualDTO {
	
	private Long id;

	private BigDecimal valorTotal;
	
	private BigDecimal valorDesc;

	private Pessoa pessoa;

	private Endereco cobranca;

	private Endereco entrega;
	
	private BigDecimal valorFrete;
	
	private List<ItemVendaDTO> itemVendaLojas = new ArrayList<ItemVendaDTO>();
	
	
	
	public List<ItemVendaDTO> getItemVendaLojas() {
		return itemVendaLojas;
	}

	public void setItemVendaLojas(List<ItemVendaDTO> itemVendaLojas) {
		this.itemVendaLojas = itemVendaLojas;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public BigDecimal getValorTotal() {
		return valorTotal;
	}
	
	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;
	}

	public Endereco getCobranca() {
		return cobranca;
	}

	public void setCobranca(Endereco cobranca) {
		this.cobranca = cobranca;
	}

	public Endereco getEntrega() {
		return entrega;
	}

	public void setEntrega(Endereco entrega) {
		this.entrega = entrega;
	}

	public BigDecimal getValorDesc() {
		return valorDesc;
	}
	
	public void setValorDesc(BigDecimal valorDesc) {
		this.valorDesc = valorDesc;
	}
	
	public BigDecimal getValorFrete() {
		return valorFrete;
	}

	public void setValorFrete(BigDecimal valorFrete) {
		this.valorFrete = valorFrete;
	}

	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

}
