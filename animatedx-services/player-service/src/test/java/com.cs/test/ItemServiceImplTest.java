package com.cs.test;

import com.cs.game.GameService;
import com.cs.item.Item;
import com.cs.item.ItemServiceImpl;
import com.cs.item.ItemState;
import com.cs.item.PlayerItem;
import com.cs.item.PlayerItemId;
import com.cs.item.PlayerItemRepository;
import com.cs.persistence.NotFoundException;
import com.cs.player.Player;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * @author Omid Alaepour.
 */
public class ItemServiceImplTest {
    @Mock
    private GameService gameService;

    @Mock
    private PlayerItemRepository playerItemRepository;

    @InjectMocks
    private ItemServiceImpl itemServiceImpl;

    @Before
    public void setup() {
        initMocks(this);
    }

    private Item createFreeRoundsItem(){
        final Item item = new Item();
        //item.setItemType(ItemType.FREE_ROUNDS);
        return item;
    }

    private PlayerItem createUnusedPlayerItem(){
        final PlayerItem playerItem = new PlayerItem();
        playerItem.setPk(new PlayerItemId());
        playerItem.setItem(createFreeRoundsItem());
        playerItem.setItemState(ItemState.UNUSED);
        return playerItem;
    }

    //Todo fix ignored test
    @Test
    @Ignore
    public void useItem_ShouldReturnItemUsed(){
        final PlayerItem playerItem = createUnusedPlayerItem();

        when(playerItemRepository.findPlayerItem(any(Player.class), any(Item.class))).thenReturn(playerItem);

        assertThat(itemServiceImpl.useItem(1L, 5L).getItemState(), is(ItemState.USED));
    }

    //Todo fix ignored test
    @Test
    @Ignore
    public void useItem_ShouldReturnNotNullDate(){
        final PlayerItem playerItem = createUnusedPlayerItem();

        when(playerItemRepository.findPlayerItem(any(Player.class), any(Item.class))).thenReturn(playerItem);

        assertThat(itemServiceImpl.useItem(1L, 5L).getUsedDate(), is(not(nullValue())));
    }

    //Todo fix ignored test
    @Test(expected = NotFoundException.class)
    @Ignore
    public void useItem_ShouldReturnThrowNotFoundException(){
        when(playerItemRepository.findPlayerItem(any(Player.class), any(Item.class))).thenReturn(null);

        itemServiceImpl.useItem(1L, 5L);
    }

    //Todo fix ignored test
    @Test(expected = NotFoundException.class)
    @Ignore
    public void useItem_ShouldReturnThrowNotFoundExceptionWhenItemIsUsed(){
        final PlayerItem playerItem = createUnusedPlayerItem();
        playerItem.setItemState(ItemState.USED);

        when(playerItemRepository.findPlayerItem(any(Player.class), any(Item.class))).thenReturn(playerItem);

        itemServiceImpl.useItem(1L, 5L);
    }
}
