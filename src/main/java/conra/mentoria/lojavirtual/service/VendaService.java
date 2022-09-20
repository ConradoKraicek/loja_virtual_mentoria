package conra.mentoria.lojavirtual.service;


import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import conra.mentoria.lojavirtual.model.VendaCompraLojaVirtual;

@Service
public class VendaService {
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	

	public void exclusaoTotalVendaBanco(Long idVenda) {
		
		/*SQL PURO*/
		String value = " begin;"
			       + "update nota_fiscal_venda set venda_compra_loja_virt_id = null where venda_compra_loja_virt_id = "+idVenda+"; "
			       + "delete from nota_fiscal_venda where venda_compra_loja_virt_id = "+idVenda+"; "
			       + "delete from item_venda_loja where venda_compra_loja_virtual_id = "+idVenda+"; "
			       + "delete from status_rastreio where venda_compra_loja_virt_id = "+idVenda+"; "
			       + "delete from vd_cp_loja_virt where id = "+idVenda+"; "
			       + "commit;";
		
		
		jdbcTemplate.execute(value);
		
	}
	
	
	@SuppressWarnings("unchecked")
	public List<VendaCompraLojaVirtual> consultaVendaFaixaData(String data1, String data2) {
		
		/*HQL (Hibernate) ou JPQL(JPA OU SPRING DATA)*/
		String sql = "select distinct(i.vendaCompraLojaVirtual) from ItemVendaLoja i"
				+ " where i.vendaCompraLojaVirtual.excluido = false"
				+ " and i.vendaCompraLojaVirtual.dataVenda >= '" + data1 + "'"
				+ " and i.vendaCompraLojaVirtual.dataVenda <= '" + data2 + "'";
		
		
		return entityManager.createQuery(sql).getResultList() ;
		
	}
	
	
	public void exclusaoTotalVendaBanco2(Long idVenda) {
		
		/*SQL PURO*/
		String sql = "begin; update vd_cp_loja_virt set excluido = true where id = " + idVenda + "; commit;";
		
		jdbcTemplate.execute(sql);
		
	}


	public void ativaRegistroVendaBanco(Long idVenda) {
		
		/*SQL PURO*/
        String sql = "begin; update vd_cp_loja_virt set excluido = false where id = " + idVenda + "; commit;";
		
		jdbcTemplate.execute(sql);
		
	}
	
}
