package conra.mentoria.lojavirtual.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import conra.mentoria.lojavirtual.model.ContaReceber;

@Repository
public interface ContaReceberRepository extends JpaRepository<ContaReceber, Long> {

}
