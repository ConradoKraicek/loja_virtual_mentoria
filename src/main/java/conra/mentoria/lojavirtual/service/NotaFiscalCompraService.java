package conra.mentoria.lojavirtual.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import conra.mentoria.lojavirtual.model.dto.ObjetoRelatorioStatusCompraDTO;
import conra.mentoria.lojavirtual.model.dto.ObjetoRequisicaoRelatorioProdutoAlertaEstoqueDTO;
import conra.mentoria.lojavirtual.model.dto.ObjetoRequisicaoRelatorioProdutoCompraNotaFiscalDTO;

@Service
public class NotaFiscalCompraService {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	
	public List<ObjetoRelatorioStatusCompraDTO> gerarRelatorioStatusVendaLojaVirtual(ObjetoRelatorioStatusCompraDTO objetoRelatorioStatusCompraDTO) {
		
		List<ObjetoRelatorioStatusCompraDTO> retorno = new ArrayList<ObjetoRelatorioStatusCompraDTO>();
		
		String sql = "select p.id as codigoProduto, p.nome as nomeProduto, pf.email as emailCliente, pf.telefone as foneCliente, p.valor_venda as valorVendaProduto, "
				+ " pf.id as codigoCliente, pf.nome as nomeCliente, p.qtd_estoque as qtEstoque, cfc.id as codigoVenda, cfc.status_venda_loja_virtual as statusVenda "
				+ " from vd_cp_loja_virt as cfc "
				+ " inner join item_venda_loja as nip "
				+ " on nip.venda_compra_loja_virtual_id = cfc.id "
				+ " inner join produto as p "
				+ " on p.id = nip.produto_id "
				+ " inner join pessoa_fisica as pf "
				+ " on pf.id = cfc.pessoa_id ";
		
				sql += " where cfc.data_venda >= '"+objetoRelatorioStatusCompraDTO.getDataInicial()+"' and cfc.data_venda <= '"+objetoRelatorioStatusCompraDTO.getDataFinal()+"' ";
				
				if (!objetoRelatorioStatusCompraDTO.getNomeProduto().isEmpty()) {
					sql += " and upper(p.nome) like upper('%"+objetoRelatorioStatusCompraDTO.getNomeProduto()+"%') ";
				}
				
				if (!objetoRelatorioStatusCompraDTO.getStatusVenda().isEmpty()) {
					sql += " and cfc.status_venda_loja_virtual in ('" + objetoRelatorioStatusCompraDTO.getStatusVenda()+"') ";
				}
				
				if (!objetoRelatorioStatusCompraDTO.getNomeCliente().isEmpty()) {
					sql += " and and pf.nome like '%"+objetoRelatorioStatusCompraDTO.getNomeCliente()+"%' ";
				}
				
		retorno = jdbcTemplate.query(sql, new BeanPropertyRowMapper(ObjetoRelatorioStatusCompraDTO.class));
		
		return retorno;
	}
	
	/**
	 * Title: Histórico de compras de produto para a loja.
	 * Este relatório permite saber os produtos comprados para serem vendido pela loja virtual, todos os produtos tem relação com a nota fiscal de compra/venda.
	 * @param objetoRequisicaoRelatorioProdutoCompraNotaFiscalDTO
	 * @param dataInicio e dataFinal são parametros obrigatórios
	 * @return List<ObjetoRequisicaoRelatorioProdutoCompraNotaFiscalDTO>
	 */
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
	
	
	/**
	 * Este relatório retorna os produtos que estão com estoque menor ou igual a quantidade definida no campo de qtde_alerta_estoque.
	 * @param alertaEstoque ObjetoRequisicaoRelatorioProdutoAlertaEstoqueDTO
	 * @return List<ObjetoRequisicaoRelatorioProdutoAlertaEstoqueDTO>
	 */
	public List<ObjetoRequisicaoRelatorioProdutoAlertaEstoqueDTO> gerarRelatorioAlertaEstoque(ObjetoRequisicaoRelatorioProdutoAlertaEstoqueDTO alertaEstoque) {
		
        List<ObjetoRequisicaoRelatorioProdutoAlertaEstoqueDTO> retorno = new ArrayList<ObjetoRequisicaoRelatorioProdutoAlertaEstoqueDTO>();
		
		String sql = "select p.id as codigoProduto, p.nome as nomeProduto, p.valor_venda as valorVendaProduto, nip.quantidade as quantidadeComprada, "
				+ " pj.id as codigoFornecedor, pj.nome as nomeFornecedor, cfc.data_compra as dataCompra, p.qtd_estoque as qtdEstoque, p.qtde_alerta_estoque as qtdAlertaEstoque  "
				+ " from nota_fiscal_compra as cfc "
				+ " inner join nota_item_produto as nip "
				+ " on cfc.id = nip.nota_fiscal_compra_id "
				+ " inner join produto as p "
				+ " on p.id = nip.produto_id "
				+ " inner join pessoa_juridica as pj "
				+ " on pj.id = cfc.pessoa_id where ";
		
		sql += " cfc.data_compra >='"+alertaEstoque.getDataInicial()+"' and ";
		sql += " cfc.data_compra <='"+alertaEstoque.getDataFinal()+"' ";
		sql += " and p.alerta_qt_de_estoque = true and p.qtd_estoque <= p.qtde_alerta_estoque ";
		
		if (!alertaEstoque.getCodigoNota().isEmpty()) {
			sql += " and cfc.id = " + alertaEstoque.getCodigoNota() + " ";
		}
		
		if (!alertaEstoque.getCodigoProduto().isEmpty()) {
			sql += " and p.id = " + alertaEstoque.getCodigoProduto() + " ";
		}
		
		if (!alertaEstoque.getNomeProduto().isEmpty()) {
			sql += " and upper(p.nome) like upper('%"+alertaEstoque.getNomeProduto()+"%')";
		}
		
		if (!alertaEstoque.getNomeFornecedor().isEmpty()) {
			sql += " and upper(pj.nome) like upper('%"+alertaEstoque.getNomeFornecedor()+"%')";
		} 
		
		retorno = jdbcTemplate.query(sql, new BeanPropertyRowMapper(ObjetoRequisicaoRelatorioProdutoAlertaEstoqueDTO.class));
		
		return retorno;
	}

}
