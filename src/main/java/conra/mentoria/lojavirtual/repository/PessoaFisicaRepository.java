package conra.mentoria.lojavirtual.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import conra.mentoria.lojavirtual.model.PessoaFisica;

@Repository
public interface PessoaFisicaRepository extends CrudRepository<PessoaFisica, Long> {
	
	

	
}
