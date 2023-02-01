package conra.mentoria.lojavirtual.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import conra.mentoria.lojavirtual.model.AcessTokenJunoAPI;



@Repository
@Transactional
public interface AccesTokenJunoRepository extends JpaRepository<AcessTokenJunoAPI, Long> {

}