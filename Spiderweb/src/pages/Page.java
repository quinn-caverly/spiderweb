package pages;

public abstract class Page {
	
	private Integer id;
	
	
	public abstract String returnTextAsString();
	
	public abstract void savePage();
	
	public abstract void openPage();
	
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

}
