package com.cs.persistence;

import com.cs.item.PlayerItemId;

/**
 * @author Joakim Gottzén
 */
public class NotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public static final String NO_ENTITY_WITH_ID_FOUND = "No entity with id %d found";
    public static final String NO_ENTITY_WITH_COMPOUND_ID_FOUND = "No entity with id (%d, %d) found";
    public static final String NO_ENTITY_WITH_STATUS_ACTIVE_FOUND = "No entity with status %S found";

    public NotFoundException(final Long id) {
        super(String.format(NO_ENTITY_WITH_ID_FOUND, id));
    }

    public NotFoundException(final Status status) {
        super(String.format(NO_ENTITY_WITH_STATUS_ACTIVE_FOUND, status));
    }

    public NotFoundException(final String message) {
        super(message);
    }

    public NotFoundException(final PlayerItemId id) {
        super(String.format(NO_ENTITY_WITH_COMPOUND_ID_FOUND, id.getPlayer().getId(), id.getItem().getId()));
    }
}
