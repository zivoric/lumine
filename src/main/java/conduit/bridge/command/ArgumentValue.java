package conduit.bridge.command;

public class ArgumentValue<T extends Argument<S>, S> {
	private S value;
	private final T type;
	public ArgumentValue(T arg, S value) {
		this.type = arg;
		this.value = value;
	}
	public S getValue() {
		return value;
	}
	public T getArg() {
		return type;
	};
}
