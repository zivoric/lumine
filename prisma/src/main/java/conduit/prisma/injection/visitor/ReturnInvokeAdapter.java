package conduit.prisma.injection.visitor;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;
import org.objectweb.asm.tree.InsnList;

public class ReturnInvokeAdapter extends AdviceAdapter {
    private final InsnList insns;
    public ReturnInvokeAdapter(MethodVisitor methodVisitor, int access, String name, String descriptor, InsnList invokeInsns) {
        super(Opcodes.ASM9, methodVisitor, access, name, descriptor);
        insns = invokeInsns;
    }
    @Override
    protected void onMethodExit(int opcode) {
        if (opcode != Opcodes.ATHROW)
            insns.accept(mv);
    }
}
