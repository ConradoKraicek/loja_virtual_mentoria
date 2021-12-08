package conra.mentoria.lojavirtual.enums;

public enum StatusContaPagar {
	
	COBRANCA("Pagar"),
	VENCIDA("Vencida"),
	ABERTA("Aberta"),
	QUITADA("Quitada"),
	NEGOCIADA("Renegociada");
	
	private String descricao;

	private StatusContaPagar(String descricao) {
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
