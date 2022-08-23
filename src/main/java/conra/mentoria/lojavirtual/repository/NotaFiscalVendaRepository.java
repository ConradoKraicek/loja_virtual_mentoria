package conra.mentoria.lojavirtual.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import conra.mentoria.lojavirtual.model.NotaFiscalVenda;

@Repository
public interface NotaFiscalVendaRepository extends JpaRepository<NotaFiscalVenda, Long> {

}
