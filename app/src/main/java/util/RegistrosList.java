package util;

public class RegistrosList {
	int id;
	private String ordem, sit;

	public RegistrosList(String ordem, String sit, int id) {
		super();
		this.id		= id;
		this.sit	= sit;
		this.ordem 	= ordem;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSit() {
		return sit;
	}

	public void setSit(String sit) {
		this.sit = sit;
	}

	public String getOrdem() {
		return ordem;
	}

	public void setOrdem(String ordem) {
		this.ordem = ordem;
	}
}
