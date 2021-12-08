package conra.mentoria.lojavirtual.enums;

public enum StatusContaReceber {
	
	COBRANCA("Pagar"),
	VENCIDA("Vencida"),
	ABERTA("Aberta"),
	QUITADA("Quitada");
	
	private String descricao;

	private StatusContaReceber(String descricao) {
		this.descricao = descricao;
	}
	
	public String getDescriçao() {
		return descricao;
	}
	
	@Override
	public String toString() {
		return this.descricao;
	}
}
