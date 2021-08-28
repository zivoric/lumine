package conduit.util;

public class ObfuscationMethod {
	private final String methodName;
	private final String obfName;
	private final String deobfDesc;
	private String obfDesc;
	private ObfuscationClass owner = null;
	
	public ObfuscationMethod(String methodName, String obfName, String deobfDesc) {
		this.methodName = methodName;
		this.obfName = obfName;
		this.deobfDesc = deobfDesc;
	}
	
	void setOwner(ObfuscationClass owner) {
		this.owner = owner;
		this.obfDesc = owner.getMap().obfuscateString(deobfDesc);
	}
	
	public String deobfName() {
		return methodName;
	}
	
	public String obfName() {
		return obfName;
	}
	
	public String deobfDesc() {
		return deobfDesc;
	}
	public String obfDesc() {
		return obfDesc;
	}
	public ObfuscationClass getObfClass() {
		return owner;
	}
}
