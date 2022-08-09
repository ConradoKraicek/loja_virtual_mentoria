package conra.mentoria.lojavirtual.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.mail.MessagingException;
import javax.validation.Valid;
import javax.xml.bind.DatatypeConverter;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.BufferedImageHttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import conra.mentoria.lojavirtual.ExceptionMentoriaJava;
import conra.mentoria.lojavirtual.model.Produto;
import conra.mentoria.lojavirtual.repository.ProdutoRepository;
import conra.mentoria.lojavirtual.service.ServiceSendEmail;

@Controller
@RestController
public class ProdutoController {
	
	@Autowired
	private ProdutoRepository produtoRepository;
	
	@Autowired
	private ServiceSendEmail serviceSendEmail;
	
	
	@ResponseBody /*Poder dar um retorno da API*/
	@PostMapping(value = "**/salvarProduto") /* Mapeando a url para rebeber o JSON */
	public ResponseEntity<Produto> salvarProduto(@RequestBody @Valid Produto produto ) throws ExceptionMentoriaJava, MessagingException, IOException { /*Recebe o JSON e converte pra Objeto*/
		
		if (produto.getTipoUnidade() == null || produto.getTipoUnidade().trim().isEmpty()) {
			throw new ExceptionMentoriaJava("Tipo de unidade deve ser informada");
		}
		
		if (produto.getNome().length() < 10) {
			throw new ExceptionMentoriaJava("Nome do produto deve ter mais de 10 letras.");
		}
		
		if (produto.getEmpresa() == null || produto.getEmpresa().getId() <= 0) {
			throw new ExceptionMentoriaJava("Empresa responsável deve ser informada");
		}
		
		if (produto.getId() == null) {
			
			List<Produto> produtos = produtoRepository.buscarProdutoNome(produto.getNome().toUpperCase(), produto.getEmpresa().getId());
			
			if (!produtos.isEmpty()) {
				throw new ExceptionMentoriaJava("Já existe Produto com a descrição:" + produto.getNome());
			}
		}
		
		if (produto.getCategoriaProduto() == null || produto.getCategoriaProduto().getId() <= 0) {
			throw new ExceptionMentoriaJava("Categoria deve ser informada");
		}
		
		if (produto.getMarcaProduto() == null || produto.getMarcaProduto().getId() <= 0) {
			throw new ExceptionMentoriaJava("Marca deve ser informada");
		}
		
		if (produto.getQtdEstoque() < 1) {
			throw new ExceptionMentoriaJava("O produto deve ter no mínimo 1 no estoque.");
		}
		
		if (produto.getImagens() == null || produto.getImagens().isEmpty() || produto.getImagens().size() == 0) {
			throw new ExceptionMentoriaJava("Deve ser informado imagens para o produto.");
		}
		
		if (produto.getImagens().size() < 3) {
			throw new ExceptionMentoriaJava("Deve ser informado pelo menos 3 imagens para o produto.");
		}
		
		if (produto.getImagens().size() > 6) {
			throw new ExceptionMentoriaJava("Deve ser informado no máximo 6 imagens para o produto.");
		}
		
		if (produto.getId() == null) {
			
			for (int i = 0; i < produto.getImagens().size(); i++) {
				
				produto.getImagens().get(i).setProduto(produto);
				produto.getImagens().get(i).setEmpresa(produto.getEmpresa());
				
				String base64Image = "";
				
				if (produto.getImagens().get(i).getImagemOriginal().contains("data:image")) {
					
					base64Image = produto.getImagens().get(i).getImagemOriginal().split(",")[1];
				} else {
					
					base64Image = produto.getImagens().get(i).getImagemOriginal();
				}
				
				byte[] imageBytes = DatatypeConverter.parseBase64Binary(base64Image); 
				
				BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imageBytes));
				
				if (bufferedImage != null) {
					
					int type = bufferedImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : bufferedImage.getType();
					int largura = Integer.parseInt("800");
					int altura = Integer.parseInt("600");
					
					BufferedImage resizedImage =  new BufferedImage(largura, altura, type);
					Graphics2D g = resizedImage.createGraphics();
					g.drawImage(bufferedImage, 0, 0, largura, altura, null);
					g.dispose();
					
					ByteArrayOutputStream baos= new ByteArrayOutputStream();
					ImageIO.write(resizedImage, "png", baos);
					
					String miniImgBase64 = "data:image/png;base64," + DatatypeConverter.printBase64Binary(baos.toByteArray());
					
					produto.getImagens().get(i).setImagemMiniatura(miniImgBase64);
					
					bufferedImage.flush();
					resizedImage.flush();
					baos.flush();
					baos.close();
				}
			}
		}
		
		Produto produtoSalvo = produtoRepository.save(produto);
		
		if (produto.getAlertaQtDeEstoque() && produto.getQtdEstoque() <= 1) {
			
			StringBuilder html = new StringBuilder();
			html.append("<h2>")
			.append("Produto: " + produto.getNome())
			.append(" com estoque baixo: " + produto.getQtdEstoque());
			html.append("<p> Id Prod:").append(produto.getId()).append("</p>");
			
			if (produto.getEmpresa().getEmail() != null) {
				
				serviceSendEmail.enviarEmailHtml("Produto sem estoque", html.toString(), produto.getEmpresa().getEmail());
			}
		}
		
		return new ResponseEntity<Produto>(produtoSalvo, HttpStatus.OK);
	}
	

	@ResponseBody /*Poder dar um retorno da API*/
	@PostMapping(value = "**/deleteProduto") /* Mapeando a url para rebeber o JSON */
	public ResponseEntity<?> deleteProduto(@RequestBody Produto produto ) { /*Recebe o JSON e converte pra Objeto*/
		
		produtoRepository.deleteById(produto.getId());
		
		return new ResponseEntity("Produto Removido", HttpStatus.OK);
	}
	
	//@Secured({"ROLE_GERENTE","ROLE_ADMIN"})
	@ResponseBody 
	@DeleteMapping(value = "**/deleteProdutoPorId/{id}") 
	public ResponseEntity<?> deleteProdutoPorId(@PathVariable("id") Long id) {
		produtoRepository.deleteById(id);
		
		return new ResponseEntity("Produto Removido", HttpStatus.OK);
	}
	
	
	@ResponseBody 
	@GetMapping(value = "**/obterProduto/{id}") 
	public ResponseEntity<Produto> obterProduto(@PathVariable("id") Long id) throws ExceptionMentoriaJava {
		
		Produto produto = produtoRepository.findById(id).orElse(null);
		
		if (produto == null) {
			throw new ExceptionMentoriaJava("Não encontrou Produto com código:" + id);
		}
		
		return new ResponseEntity<Produto>(produto, HttpStatus.OK);
	}
	
	
	@ResponseBody 
	@GetMapping(value = "**/buscarProdNome/{nome}") 
	public ResponseEntity<List<Produto>> buscarProdNome(@PathVariable("nome") String nome) {
		
		List<Produto> produto = produtoRepository.buscarProdutoNome(nome.toUpperCase());
		
		return new ResponseEntity<List<Produto>>(produto, HttpStatus.OK);
	}
}
