package users;

public enum Role {
	MANAGER("@manager"),
	CLERK("@clerk");
	
	private final String type;
	
	Role(String type){
		this.type=type;
	}
	
	public String getType() {
		return type;
	}
}
