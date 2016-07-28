package com.cs.avatar;

import javax.annotation.Nonnull;
import javax.validation.constraints.Min;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

/**
 * @author Joakim Gottzén
 */
@XmlRootElement
@XmlAccessorType(FIELD)
public class AvatarDto {
    @XmlElement
    @Nonnull
    private Long id;

    @XmlElement
    @Nonnull
    @Min(value=1, message = "avatarDto.avatarBaseTypeId.min")
    private Integer avatarBaseTypeId;

    @XmlElement
    @Nonnull
    @Min(value=1, message = "avatarDto.level.min")
    private Long level;

    @XmlElement
    @Nonnull
    @Min(value = 1, message = "avatarDto.skinColor.notNull")
    private Short skinColor;

    @XmlElement
    @Nonnull
    @Min(value = 1, message = "avatarDto.hairColor.notNull")
    private Short hairColor;

    @XmlElement
    @Nonnull
    private String pictureUrl;

    @XmlElement
    @Nonnull
    private String nightBackgroundUrl;

    @XmlElement
    @Nonnull
    private String dayBackgroundUrl;

    @SuppressWarnings("UnusedDeclaration")
    public AvatarDto() {
    }

    public AvatarDto(@Nonnull final Avatar avatar) {
        id = avatar.getId();
        avatarBaseTypeId = avatar.getAvatarBaseType().getId();
        level = avatar.getLevel().getLevel();
        skinColor = avatar.getSkinColor().getColor();
        hairColor = avatar.getHairColor().getColor();
        pictureUrl = avatar.getPictureUrl();
    }

    public AvatarDto(@Nonnull final Avatar avatar, final Boolean night) {
        this(avatar);
        if (night){
            dayBackgroundUrl = avatar.getNightBackgroundUrl();
        } else {
            nightBackgroundUrl = avatar.getDayBackgroundUrl();
        }
    }

    public Avatar asAvatar() {
        final Avatar avatar = new Avatar(id);
        avatar.setAvatarBaseType(new AvatarBaseType(avatarBaseTypeId));
        avatar.setSkinColor(SkinColor.getFromColor(skinColor));
        avatar.setHairColor(HairColor.getFromColor(hairColor));
        return avatar;
    }
}
