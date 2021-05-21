package Client;

public class UnitsException extends Exception {

    public UnitsException(){
        super();
    }

    public UnitsException(String message){
        super(message);
    }

    public UnitsException(String message, Throwable cause){
        super(message, cause);
    }

    public UnitsException(Throwable cause) {
        super(cause);
    }
}
