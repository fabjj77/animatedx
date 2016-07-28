package com.cs.avatar;

import com.cs.audit.AuditService;
import com.cs.persistence.NotFoundException;
import com.cs.persistence.Status;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * @author Omid Alaepour.
 */
public class AvatarServiceImplTest {
    @InjectMocks
    private AvatarServiceImpl avatarService;

    @Mock
    private AvatarBaseTypeRepository avatarBaseTypeRepository;
    @Mock
    private AvatarRepository avatarRepository;
    @Mock
    private AuditService auditService;

    @Before
    public void setup() {
        initMocks(this);
    }

    private Avatar createAvatar() {
        final Avatar avatar = new Avatar();
        avatar.setStatus(Status.ACTIVE);
        return avatar;
    }

    private AvatarBaseType createAvatarBaseType() {
        return new AvatarBaseType();
    }

    @Test
    public void getAvatarBaseTypes_ShouldReturnAvatarBaseTypes() {
        final List<AvatarBaseType> avatarBaseTypeList = new ArrayList<>();
        avatarBaseTypeList.add(createAvatarBaseType());

        when(avatarBaseTypeRepository.findByStatus(Status.ACTIVE)).thenReturn(avatarBaseTypeList);

        assertThat(avatarService.getAvatarBaseTypes(Status.ACTIVE), is(avatarBaseTypeList));
    }

    @Test(expected = NotFoundException.class)
    public void getAvatarBaseTypes_ShouldThrowNotFoundException() {
        final List<AvatarBaseType> avatarBaseTypeList = new ArrayList<>();

        when(avatarBaseTypeRepository.findByStatus(Status.ACTIVE)).thenReturn(avatarBaseTypeList);

        avatarService.getAvatarBaseTypes(Status.ACTIVE);
    }

    @Test
    public void getAvatars_ShouldReturnAvatars() {
        final List<Avatar> avatarList = new ArrayList<>();
        avatarList.add(createAvatar());

        when(avatarRepository.findByAvatarBaseTypeAndLevel(new AvatarBaseType(anyInt()), new Level(anyLong()))).thenReturn(avatarList);

        assertThat(avatarService.getAvatars(anyInt(), anyLong()), is(sameInstance(avatarList)));
    }

    @Test(expected = NotFoundException.class)
    public void getAvatars_ShouldThrowNotFoundException() {
        final List<Avatar> avatarList = new ArrayList<>();
        when(avatarRepository.findByAvatarBaseTypeAndLevel(new AvatarBaseType(anyInt()), new Level(anyLong()))).thenReturn(avatarList);

        avatarService.getAvatars(anyInt(), anyLong());
    }

    @Test
    public void getActiveAvatars_ShouldReturnActiveAvatars() {
        final List<Avatar> avatarList = new ArrayList<>();
        avatarList.add(createAvatar());

        when(avatarRepository.findByLevelAndStatus(new Level(anyLong()), eq(Status.ACTIVE))).thenReturn(avatarList);

        assertThat(avatarService.getActiveAvatars(eq(anyLong())), is(sameInstance(avatarList)));
    }

    @Test(expected = NotFoundException.class)
    public void getActiveAvatars_ShouldThrowNotFoundException() {
        final List<Avatar> avatarList = new ArrayList<>();
        when(avatarRepository.findByLevelAndStatus(new Level(anyLong()), eq(Status.ACTIVE))).thenReturn(avatarList);
        avatarService.getActiveAvatars(eq(anyLong()));
    }

    @Test
    public void getAvatar() {
        final Avatar avatar = createAvatar();
        when(avatarRepository.findOne(anyLong())).thenReturn(avatar);
        assertThat(avatarService.getAvatar(anyLong()), is(sameInstance(avatar)));
    }

    @Test(expected = NotFoundException.class)
    public void getAvatar_ShouldThrowNotFoundException() {
        when(avatarRepository.findOne(anyLong())).thenReturn(null);

        avatarService.getAvatar(anyLong());
    }

    @Test
    public void getNextLevelAvatar_ShouldReturnNextLevelAvatar() {
        final Avatar avatar = createAvatar();
        avatar.setAvatarBaseType(createAvatarBaseType());
        avatar.setHairColor(HairColor.DARK);
        avatar.setSkinColor(SkinColor.DARK);
        final Level level = new Level(5L);
        avatar.setLevel(level);

        final Avatar nextLevel = createAvatar();

        when(avatarRepository.findByAvatarBaseTypeAndHairColorAndSkinColorAndLevel((AvatarBaseType) anyObject(), (HairColor) anyObject(),
                                                                                   (SkinColor) anyObject(), (Level) anyObject()))
                .thenReturn(nextLevel);

        assertThat(avatarService.getAvatarForLevel(avatar, level), is(sameInstance(nextLevel)));
    }

    @Test(expected = NotFoundException.class)
    public void getNextLevelAvatar_ShouldThrowNotFoundException() {
        final Avatar avatar = createAvatar();
        avatar.setAvatarBaseType(createAvatarBaseType());
        avatar.setHairColor(HairColor.DARK);
        avatar.setSkinColor(SkinColor.DARK);
        final Level level = new Level(5L);
        avatar.setLevel(level);

        when(avatarRepository.findByAvatarBaseTypeAndHairColorAndSkinColorAndLevel((AvatarBaseType) anyObject(), (HairColor) anyObject(),
                                                                                   (SkinColor) anyObject(), (Level) anyObject()))
                .thenReturn(null);

        avatarService.getAvatarForLevel(avatar, level);
    }
}
