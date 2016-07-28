package com.cs.casino.bonus;

import javax.annotation.Nonnull;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

/**
 * @author Omid Alaepour
 */
@XmlRootElement
@XmlAccessorType(FIELD)
public class FreeRoundDto {

    @XmlElement
    @Nonnull
    @NotNull(message = "FreeRoundDto.name.notNull")
    private List<String> gameName;

    @SuppressWarnings("UnusedDeclaration")
    public FreeRoundDto() {
    }

    public FreeRoundDto(@Nonnull final List<String> activeFreeRounds) {
        gameName = activeFreeRounds;
    }
}
