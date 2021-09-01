package conduit.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.Map.Entry;

public class ObfuscationMap {
	private final List<ObfuscationClass> classes;
	
	public ObfuscationMap(InputStream fileInput) {
		this();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(fileInput, StandardCharsets.UTF_8));
			JsonObject classesObj = new JsonParser().parse(br).getAsJsonObject().getAsJsonObject("classes");
			br.close();
			for(Entry<String, JsonElement> cl : classesObj.entrySet()) {
				JsonObject classObj = cl.getValue().getAsJsonObject();
				List<ObfuscationMethod> methods = new ArrayList<>();
				Map<String, String> fields = new HashMap<>();
				if (classObj.has("methods")) {
					JsonObject methodsObj = cl.getValue().getAsJsonObject().getAsJsonObject("methods");
					methodsObj.entrySet().forEach(entry -> {
						JsonArray selMethod = entry.getValue().getAsJsonArray();
						selMethod.forEach(element -> {
							JsonObject elementObj = element.getAsJsonObject();
							methods.add(new ObfuscationMethod(entry.getKey(), elementObj.get("obfName").getAsString(), elementObj.get("deobfDesc").getAsString()));
						});
					});
				}
				if (classObj.has("fields")) {
					JsonObject fieldsObj = cl.getValue().getAsJsonObject().getAsJsonObject("fields");
					fieldsObj.entrySet().forEach(entry -> {
						fields.put(entry.getKey(), entry.getValue().getAsString());
					});
				}
				classes.add(new ObfuscationClass(this, cl.getKey(), classObj.get("obf").getAsString(), methods, fields));
			}
			classes.forEach(cl -> {
				cl.methods().forEach(method -> {
					method.setOwner(cl);
				});
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public ObfuscationMap() {
		classes = new ArrayList<>();
	}
	
	public List<ObfuscationClass> classes() {
		return Collections.unmodifiableList(classes);
	}
	
	public ObfuscationClass fromDeobf(String deobfName) {
		for (ObfuscationClass cl : classes) {
			if (cl.deobfName().equals(deobfName))
				return cl;
		}
		return null;
	}
	
	public ObfuscationClass fromObf(String obfName) {
		for (ObfuscationClass cl : classes) {
			if (cl.obfName().equals(obfName))
				return cl;
		}
		return null;
	}
	
	public String obf(String deobfName) {
		ObfuscationClass cl = fromDeobf(deobfName);
		return cl==null ? deobfName : cl.obfName();
	}
	
	public String deobf(String obfName) {
		ObfuscationClass cl = fromObf(obfName);
		return cl==null ? obfName : cl.deobfName();
	}
	
	public String obfuscateString(String deobf) {
		String obf = "";
		while (deobf.indexOf('L') != -1 && deobf.indexOf(';') != -1 && deobf.indexOf('L') < deobf.indexOf(';')) {
			String deobfName = deobf.substring(deobf.indexOf('L')+1, deobf.indexOf(';'));
			obf += deobf.substring(0, deobf.indexOf('L')+1) + obf(deobfName) + ";";
			deobf = deobf.substring(deobf.indexOf(';')+1);
		}
		obf += deobf;
		return obf;
	}
	public String deobfuscateString(String obf) {
		String deobf = "";
		while (obf.indexOf('L') != -1 && obf.indexOf(';') != -1 && obf.indexOf('L') < obf.indexOf(';')) {
			String obfName = obf.substring(obf.indexOf('L')+1, obf.indexOf(';'));
			deobf += obf.substring(0, obf.indexOf('L')+1) + deobf(obfName) + ";";
			obf = obf.substring(obf.indexOf(';')+1);
		}
		deobf += obf;
		return deobf;
	}
}
