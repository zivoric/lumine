package lumine.prisma.injection.visitor;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;

public class ReplaceVisitor extends MethodVisitor {
    private final InsnList insns;

    public ReplaceVisitor(MethodVisitor methodVisitor, InsnList insns) {
        super(Opcodes.ASM9, methodVisitor);
        this.insns = insns;
    }

    @Override
    public void visitCode() {
        mv.visitCode();
        insns.accept(mv);
        mv.visitEnd();
    }
}
