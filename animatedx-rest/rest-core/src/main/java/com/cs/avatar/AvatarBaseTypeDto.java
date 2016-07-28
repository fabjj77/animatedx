package com.cs.avatar;

import com.cs.avatar.AvatarBaseType;

import org.hibernate.validator.constraints.NotEmpty;

import javax.annotation.Nonnull;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

/**
 * @author Omid Alaepour
 */
@XmlRootElement
@XmlAccessorType(FIELD)
public class AvatarBaseTypeDto {
    @XmlElement
    @Nonnull
    private Integer id;

    @Nonnull
    @NotEmpty(message = "avatarBaseTypeDto.name.notEmpty")
    private String name;

    public AvatarBaseTypeDto(final AvatarBaseType avatarBaseType) {
        id = avatarBaseType.getId();
        name = avatarBaseType.getName();
    }

    public AvatarBaseType asAvatarBaseType() {
        final AvatarBaseType avatarBaseType = new AvatarBaseType(id);
        avatarBaseType.setName(name);
        return avatarBaseType;
    }
}
