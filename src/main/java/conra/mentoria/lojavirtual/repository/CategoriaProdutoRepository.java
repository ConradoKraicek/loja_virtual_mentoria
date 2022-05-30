package conra.mentoria.lojavirtual.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import conra.mentoria.lojavirtual.model.CategoriaProduto;

@Repository
public interface CategoriaProdutoRepository extends JpaRepository<CategoriaProduto, Long> {
	
	@Query(nativeQuery = true, value ="select count(1) > 0 from categoria_produto where upper(trim(nome_desc)) = upper(trim(?1))")
	public boolean existeCategoria(String nomeCategoria);
    
	@Query("select c from CategoriaProduto c where upper(trim(c.nomeDesc)) like %?1%")
	public List<CategoriaProduto> buscarCategoriaPorDescricao(String nomeDesc);

}
