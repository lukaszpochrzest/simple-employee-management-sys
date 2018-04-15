package pl.ems.util;

import pl.ems.exception.BadRequestException;

import java.util.UUID;

public class Util {

    public static UUID parseUUID(String uuid) {
        try {
            return UUID.fromString(uuid);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid uuid");
        }
    }

}
