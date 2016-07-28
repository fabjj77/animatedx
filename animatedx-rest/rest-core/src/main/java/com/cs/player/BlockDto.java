package com.cs.player;

import javax.annotation.Nonnull;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

/**
 * @author Hadi Movaghar
 */
@XmlRootElement
@XmlAccessorType(FIELD)
public class BlockDto {
    @XmlElement
    @Nonnull
    @NotNull(message="blockDto.blockType.notNull")
    private BlockType blockType;

    @XmlElement
    @Nonnull
    @Min(value=7, message = "blockDto.days.min")
    private Integer days;

    public BlockDto() {
    }

    @Nonnull
    public BlockType getBlockType() {
        return blockType;
    }

    @Nonnull
    public Integer getDays() {
        return days;
    }
}
