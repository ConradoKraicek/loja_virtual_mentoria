package conra.mentoria.lojavirtual.model.dto;

import java.io.Serializable;

public class Links implements Serializable {

	private static final long serialVersionUID = 1L;

	private Self self = new Self();

	public Self getSelf() {
		return self;
	}

	public void setSelf(Self self) {
		this.self = self;
	}

}
