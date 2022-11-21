package conra.mentoria.lojavirtual.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import conra.mentoria.lojavirtual.model.dto.ObjetoRequisicaoRelatorioProdutoCompraNotaFiscalDTO;

@Service
public class NotaFiscalCompraService {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	

	public List<ObjetoRequisicaoRelatorioProdutoCompraNotaFiscalDTO> gerarRelatorioProdutoCompraNota(ObjetoRequisicaoRelatorioProdutoCompraNotaFiscalDTO objetoRequisicaoRelatorioProdutoCompraNotaFiscalDTO) {
		
		List<ObjetoRequisicaoRelatorioProdutoCompraNotaFiscalDTO> retorno = new ArrayList<ObjetoRequisicaoRelatorioProdutoCompraNotaFiscalDTO>();
		
		String sql = "select p.id as codigoProduto, p.nome as nomeProduto, p.valor_venda as valorVendaProduto, nip.quantidade as quantidadeComprada, "
				+ " pj.id as codigoFornecedor, pj.nome as nomeFornecedor, cfc.data_compra as dataCompra "
				+ " from nota_fiscal_compra as cfc "
				+ " inner join nota_item_produto as nip "
				+ " on cfc.id = nip.nota_fiscal_compra_id "
				+ " inner join produto as p "
				+ " on p.id = nip.produto_id "
				+ " inner join pessoa_juridica as pj "
				+ " on pj.id = cfc.pessoa_id where ";
		
		sql += " cfc.data_compra >='"+objetoRequisicaoRelatorioProdutoCompraNotaFiscalDTO.getDataInicial()+"' and ";
		sql += " cfc.data_compra <='"+objetoRequisicaoRelatorioProdutoCompraNotaFiscalDTO.getDataFinal()+"' ";
		
		if (!objetoRequisicaoRelatorioProdutoCompraNotaFiscalDTO.getCodigoNota().isEmpty()) {
			sql += " and cfc.id = " + objetoRequisicaoRelatorioProdutoCompraNotaFiscalDTO.getCodigoNota() + " ";
		}
		
		if (!objetoRequisicaoRelatorioProdutoCompraNotaFiscalDTO.getCodigoProduto().isEmpty()) {
			sql += " and p.id = " + objetoRequisicaoRelatorioProdutoCompraNotaFiscalDTO.getCodigoProduto() + " ";
		}
		
		if (!objetoRequisicaoRelatorioProdutoCompraNotaFiscalDTO.getNomeProduto().isEmpty()) {
			sql += " and upper(p.nome) like upper('%"+objetoRequisicaoRelatorioProdutoCompraNotaFiscalDTO.getNomeProduto()+"%')";
		}
		
		if (!objetoRequisicaoRelatorioProdutoCompraNotaFiscalDTO.getNomeFornecedor().isEmpty()) {
			sql += " and upper(pj.nome) like upper('%"+objetoRequisicaoRelatorioProdutoCompraNotaFiscalDTO.getNomeFornecedor()+"%')";
		} 
		
		retorno = jdbcTemplate.query(sql, new BeanPropertyRowMapper(ObjetoRequisicaoRelatorioProdutoCompraNotaFiscalDTO.class));
		
		return retorno;
	}

}
