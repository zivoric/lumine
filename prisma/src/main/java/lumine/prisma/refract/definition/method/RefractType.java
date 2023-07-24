package lumine.prisma.refract.definition.method;

/*
    REPLACE + RETURN = old RETURN
    REPLACE + START = old REPLACE
    INSERT + RETURN = old INJECT(RETURN)
    INSERT + START = old INJECT(START)
 */
public enum RefractType {
    REPLACE, INSERT
}
