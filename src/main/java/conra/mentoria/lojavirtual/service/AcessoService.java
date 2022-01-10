package conra.mentoria.lojavirtual.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import conra.mentoria.lojavirtual.model.Acesso;
import conra.mentoria.lojavirtual.repository.AcessoRepository;

@Service
public class AcessoService {
	
	@Autowired
	private AcessoRepository acessoRepository;
	
	public Acesso save(Acesso acesso) {
		
		/* Qualquer tipo de validação */
		return acessoRepository.save(acesso);
	}

}
