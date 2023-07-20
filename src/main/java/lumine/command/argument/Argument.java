package lumine.command.argument;

public abstract class Argument<T> {
	protected Argument(){}
	public abstract String getIdentifier();
	public abstract Class<T> getType();
}
