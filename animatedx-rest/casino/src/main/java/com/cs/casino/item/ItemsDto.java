package com.cs.casino.item;

import com.cs.item.PlayerItem;

import javax.annotation.Nonnull;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

/**
 * @author Joakim Gottzén
 */
@XmlRootElement
@XmlAccessorType(FIELD)
public class ItemsDto {
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @XmlElement
    @Nonnull
    private List<ItemDto> unusedItems;

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @XmlElement
    @Nonnull
    private List<ItemDto> usedItems;

    @SuppressWarnings("UnusedDeclaration")
    public ItemsDto() {
    }

    public ItemsDto(final List<PlayerItem> unusedItems, final List<PlayerItem> usedItems) {
        this.unusedItems = new ArrayList<>();
        for (final PlayerItem playerItem : unusedItems) {
            this.unusedItems.add(new ItemDto(playerItem));
        }
        this.usedItems = new ArrayList<>();
        for (final PlayerItem playerItem : usedItems) {
            this.usedItems.add(new ItemDto(playerItem));
        }
    }
}
