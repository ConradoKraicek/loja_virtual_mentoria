package conra.mentoria.lojavirtual;

import conra.mentoria.lojavirtual.util.ValidaCNPJ;
import conra.mentoria.lojavirtual.util.ValidaCPF;

public class TesteCPFCNPJ {
	
	public static void main(String[] args) {
		
		boolean isCnpj = ValidaCNPJ.isCNPJ("66.347.536/0001-96");
		
		System.out.println("Cnpj Válido: " + isCnpj);
		
		
		boolean isCpf = ValidaCPF.isCPF("255.326.610-30");
		
		System.out.println("Cpf Válido: " + isCpf);
		
	}

}
