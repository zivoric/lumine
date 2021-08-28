package conduit.main;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;

import static org.objectweb.asm.Opcodes.*;

import conduit.util.ConduitUtils;
import conduit.util.ObfuscationClass;
import conduit.util.ObfuscationMap;
import conduit.util.ObfuscationMethod;
import net.minecraft.launchwrapper.IClassTransformer;

public class ConduitTransformer implements IClassTransformer {
	private ObfuscationMap map = ConduitUtils.getCurrentMap();
	@Override
	public byte[] transform(String name, String transformedName, byte[] inputClass) {
		ClassNode cn = new ClassNode();
		ClassReader cr = new ClassReader(inputClass);
		cr.accept(cn, 0);
		switch (map.deobf(name.replace('.', '/'))) {
		case "net/minecraft/resource/ServerResourceManager":
			serverResourceManager(name, cn);
			break;
		case "net/minecraft/server/MinecraftServer":
			minecraftServer(name, cn);
			break;
		default:
			return inputClass;
		}
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS + ClassWriter.COMPUTE_FRAMES);
		cn.accept(cw);
		return cw.toByteArray();
		
	}
	private void serverResourceManager(String name, ClassNode node) {
		ObfuscationClass obfClass = map.fromObf(name);
		for (MethodNode method : node.methods) {
			ObfuscationMethod reloadMethod = obfClass.getMethodsFromDeobf("reload").get(0);
			if (method.name.equals("<init>")) {
				/*for (AbstractInsnNode insn : method.instructions) {
					if (insn.getOpcode() == NEW && ((TypeInsnNode)insn).desc.equals(map.obf("net/minecraft/server/command/CommandManager"))) {
						Conduit.log("Case 1", ((TypeInsnNode)insn).desc);
						((TypeInsnNode)insn).desc = "conduit/bridge/command/ConduitCommandManager";
						Conduit.log(((TypeInsnNode)insn).desc);
					}
					if (insn.getOpcode() == INVOKESPECIAL && ((MethodInsnNode)insn).owner.equals(map.obf("net/minecraft/server/command/CommandManager"))) {
						Conduit.log("Case 2");
						MethodInsnNode assignVar = (MethodInsnNode)insn;
						assignVar.owner = "conduit/bridge/command/ConduitCommandManager";
						method.instructions.insert(assignVar, new TypeInsnNode(CHECKCAST, map.obf("net/minecraft/server/command/CommandManager")));
						Conduit.log(assignVar.owner, ((TypeInsnNode)assignVar.getNext()).desc);
						//Conduit.log("Desc: " + assignVar.desc, "Name: " + assignVar.name, "Owner: " + assignVar.owner);
					}
				}*/
			} else if (compare(method, reloadMethod)) {
				for (AbstractInsnNode insn : method.instructions) {
					if (insn.getOpcode() == NEW && ((TypeInsnNode)insn).desc.equals(map.obf("net/minecraft/resource/ServerResourceManager"))) {
						((TypeInsnNode)insn).desc = "conduit/bridge/ConduitSRM";
					} else if (insn.getOpcode() == INVOKESPECIAL && ((MethodInsnNode)insn).owner.equals(map.obf("net/minecraft/resource/ServerResourceManager"))) {
						MethodInsnNode assignVar = (MethodInsnNode)insn;
						assignVar.owner = "conduit/bridge/ConduitSRM";
						method.instructions.insert(assignVar, new TypeInsnNode(CHECKCAST, map.obf("net/minecraft/resource/ServerResourceManager")));
					}
				}
			}
		}
	}
	private void minecraftServer(String name, ClassNode node) {
		for (MethodNode method : node.methods) {
			if (method.name.equals("getServerModName")) {
				for (AbstractInsnNode insn : method.instructions) {
					if (insn instanceof LdcInsnNode)
						((LdcInsnNode)insn).cst = "conduit";
				}
			}
		}
	}
	
	private boolean compare(MethodNode method, ObfuscationMethod obfMethod) {
		return method.name.equals(obfMethod.obfName()) && method.desc.equals(obfMethod.obfDesc());
	}
}
