package conduit.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ObfuscationClass {
	private final String className;
	private final String obfName;
	private final List<ObfuscationMethod> methods;
	private final Map<String, String> fields;
	private final ObfuscationMap owner;
	
	public ObfuscationClass(ObfuscationMap owner, String className, String obfName, List<ObfuscationMethod> methods, Map<String, String> fields) {
		this.owner = owner;
		this.className = className;
		this.obfName = obfName;
		this.methods = methods;
		for (ObfuscationMethod method : this.methods)
			method.setOwner(this);
		this.fields = fields;
	}
	
	public List<ObfuscationMethod> methods() {
		return Collections.unmodifiableList(methods);
	}
	
	public Map<String, String> fields() {
		return Collections.unmodifiableMap(fields);
	}
	
	public String deobfName() {
		return className;
	}
	
	public String obfName() {
		return obfName;
	}
	
	public List<ObfuscationMethod> getMethodsFromDeobf(String deobfName) {
		List<ObfuscationMethod> methods = new ArrayList<>();
		for (ObfuscationMethod method : this.methods) {
			if (method.deobfName().equals(deobfName))
				methods.add(method);
		}
		return methods;
	}
	public List<ObfuscationMethod> getMethodsFromObf(String obfName) {
		List<ObfuscationMethod> methods = new ArrayList<>();
		return methods;
	}
	
	public ObfuscationMethod getMethodFromDeobf(String deobfName, String deobfDesc) {
		for (ObfuscationMethod method : methods) {
			if (method.deobfName().equals(deobfName) && method.deobfDesc().equals(deobfDesc))
				return method;
		}
		return null;
	}
	
	public ObfuscationMethod getMethodFromObf(String obfName, String obfDesc) {
		for (ObfuscationMethod method : methods) {
			if (method.obfName().equals(obfName) && method.obfDesc().equals(obfDesc))
				return method;
		}
		return null;
	}
	
	public String methodToObf(String deobfName, String desc) {
		return getMethodFromDeobf(deobfName, desc).obfName();
	}
	
	public String methodToDeobf(String obfName, String desc) {
		return getMethodFromDeobf(obfName, desc).deobfName();
	}
	
	public String fieldToObf(String deobfName) {
		return fields.get(deobfName);
	}
	
	public String fieldToDeobf(String obfName) {
		for (Entry<String,String> entry : fields.entrySet()) {
			if (entry.getValue().equals(obfName))
				return entry.getKey();
		}
		return null;
	}
	
	public ObfuscationMap getMap() {
		return owner;
	}
}
