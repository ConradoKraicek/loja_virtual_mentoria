package conra.mentoria.lojavirtual.service;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import conra.mentoria.lojavirtual.model.VendaCompraLojaVirtual;
import conra.mentoria.lojavirtual.repository.Vd_Cp_Loja_Virt_Repository;

@Service
public class VendaService {
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private Vd_Cp_Loja_Virt_Repository vd_Cp_Loja_Virt_Repository;
	

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
	public List<VendaCompraLojaVirtual> consultaVendaFaixaData(String data1, String data2) throws ParseException {
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		Date date1 = dateFormat.parse(data1);
		Date date2 = dateFormat.parse(data2);
		
		return vd_Cp_Loja_Virt_Repository.consultaVendaFaixaData(date1, date2);
		
	}
	
//	@SuppressWarnings("unchecked")
//	public List<VendaCompraLojaVirtual> consultaVendaFaixaData(String data1, String data2) {
//		
//		/*HQL (Hibernate) ou JPQL(JPA OU SPRING DATA)*/
//		String sql = "select distinct(i.vendaCompraLojaVirtual) from ItemVendaLoja i"
//				+ " where i.vendaCompraLojaVirtual.excluido = false"
//				+ " and i.vendaCompraLojaVirtual.dataVenda >= '" + data1 + "'"
//				+ " and i.vendaCompraLojaVirtual.dataVenda <= '" + data2 + "'";
//		
//		
//		return entityManager.createQuery(sql).getResultList() ;
//		
//	}
	
	
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
