package conduit.injection.util;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class MethodInfo {

    private final String owner;
    private final String name;
    private final String desc;
    private final String[] args;
    public MethodInfo(String owner, String name, String desc) {
        this.owner = owner;
        this.name = name;
        this.desc = desc;
        this.args = initArgs();
    }
    public MethodInfo(String name, String desc) {
        this(null, name, desc);
    }
    private String[] initArgs() {
        String parameters = desc.substring(desc.indexOf('(')+1, desc.indexOf(')'));
        List<String> args = new LinkedList<>();
        while (parameters.length() > 0) {
            if (InjectionUtils.UNBOXED.get(parameters.charAt(0)) != null) {
                args.add(parameters.substring(0,1));
                parameters = parameters.substring(1);
            } else {
                args.add(parameters.substring(0,parameters.indexOf(';')+1));
                parameters = parameters.substring(parameters.indexOf(';')+1);
            }
        }
        return args.toArray(new String[0]);
    }

    public static Class<?> typeFromString(String parameter) {
        Class<?> param = InjectionUtils.UNBOXED.get(parameter.charAt(0));
        if (parameter.charAt(0) == 'L') {
            int endIndex = parameter.indexOf(';');
            if (endIndex > -1) {
                try {
                    param = Class.forName(parameter.substring(1, endIndex).replace('/', '.'));
                } catch (ClassNotFoundException ignored) {}
            }
        } else if (parameter.charAt(0) == '[') {
            return typeFromString(parameter.substring(1)).arrayType();
        }
        return param;
    }

    public String name() {
        return name;
    }
    public String desc() {
        return desc;
    }
    public String owner() {
        return owner;
    }

    public static MethodInfo fromString(String method) {
        if (!method.contains("(")) return null;
        String name = method.substring(0, method.indexOf("("));
        String desc = method.substring(method.indexOf("("));
        return new MethodInfo(name, desc);
    }
    public static MethodInfo fromMethod(Method method) {
        /*try {
            Field[] fields = Method.class.getDeclaredFields();
            Conduit.log("Method fields length: "+fields.length);
            for (Field f : fields) {
                Conduit.log("Method field: " + f.getName() + ", " + f.getType().getName());
            }
            Field sigField = Method.class.getDeclaredField("signature");
            sigField.setAccessible(true);
            String sig = (String) sigField.get(method);
            if(sig != null) {
                String name = sig.substring(0, sig.indexOf("("));
                String desc = sig.substring(sig.indexOf("("));
                return new MethodInfo(name, desc);
            }
        } catch (IllegalAccessException|NoSuchFieldException e) {
            e.printStackTrace();
        }*/
        return fromTypes(method.getName(), method.getReturnType(), method.getParameterTypes());
    }

    public static MethodInfo fromTypes(String name, Class<?> returnType, Class<?>... parameterTypes) {
        StringBuilder desc = new StringBuilder("(");
        for (Class<?> cl : parameterTypes) {
            buildType(desc, cl);
        }
        desc.append(")");
        buildType(desc, returnType);
        return new MethodInfo(name, desc.toString());
    }

    public static StringBuilder buildType(StringBuilder desc, Class<?> cl) {
        Character symbol = InjectionUtils.UNBOXED.inverse().get(cl);
        if (symbol != null) {
            desc.append(symbol.charValue());
        } else {
            if (cl.getName().endsWith(";") || cl.getName().startsWith("[")) {
                desc.append(cl.getName().replace('.', '/'));
            } else {
                desc.append("L").append(cl.getName().replace('.', '/')).append(";");
            }
        }
        return desc;
    }
    public static String typeToString(Class<?> cl) {
        return buildType(new StringBuilder(), cl).toString();
    }

    public Class<?>[] args() {
        return Arrays.stream(stringArgs()).map(MethodInfo::typeFromString).toArray(Class<?>[]::new);
    }

    public String[] stringArgs() {
        return args;
    }

    public Class<?> type() {
        return typeFromString(strType());
    }

    public String strType() {
        return desc.substring(desc.indexOf(')') + 1);
    }

    @Override
    public String toString() {
        return name() + desc();
    }
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof MethodInfo otherPair)) return false;
        return otherPair.desc.equals(desc) && otherPair.name.equals(name);
    }
}