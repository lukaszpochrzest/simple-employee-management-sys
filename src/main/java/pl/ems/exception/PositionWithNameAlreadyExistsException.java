package pl.ems.exception;

public class PositionWithNameAlreadyExistsException extends BadRequestException {
    public PositionWithNameAlreadyExistsException(String name) {
        super("Position with name " + name + " already exists");
    }
}
