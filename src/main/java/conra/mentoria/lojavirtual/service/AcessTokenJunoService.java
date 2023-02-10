package conra.mentoria.lojavirtual.service;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Service;

import conra.mentoria.lojavirtual.model.AcessTokenJunoAPI;

@Service
public class AcessTokenJunoService {
	
	@PersistenceContext
	private EntityManager entityManager;
	
	
	public AcessTokenJunoAPI buscaTokenAtivo() {
		
		try {
		
		AcessTokenJunoAPI acessTokenJunoAPI = 
				(AcessTokenJunoAPI) entityManager.createQuery("select a from AcessTokenJunoAPI a ")
		        .setMaxResults(1)
		        .getSingleResult();
		
		return acessTokenJunoAPI;
		
		} catch (NoResultException e) {
			return null;
		}
		
		
	} 

}
