package conra.mentoria.lojavirtual;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EntityScan(basePackages = "conra.mentoria.lojavirtual.model")
@ComponentScan(basePackages = {"conra.*"})
@EnableJpaRepositories(basePackages = {"conra.mentoria.lojavirtual.repository"})
@EnableTransactionManagement
public class LojaVirtualMentoriaApplication {

	public static void main(String[] args) {
		
		//System.out.println(new BCryptPasswordEncoder().encode("123")); 
		
		SpringApplication.run(LojaVirtualMentoriaApplication.class, args);
	}

}
