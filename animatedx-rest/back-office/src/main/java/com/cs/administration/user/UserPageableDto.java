package com.cs.administration.user;

import com.cs.user.User;

import org.springframework.data.domain.Page;

import javax.annotation.Nonnull;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

/**
 * @author Omid Alaepour
 */
@XmlRootElement
@XmlAccessorType(FIELD)
public class UserPageableDto {
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @XmlElement
    @Nonnull
    private List<UserDto> users;

    @XmlElement
    @Nonnull
    private Long count;

    @SuppressWarnings("UnusedDeclaration")
    public UserPageableDto() {
    }

    public UserPageableDto(@Nonnull final Page<User> users) {
        this.users = new ArrayList<>(users.getSize());
        for (final User user : users) {
            this.users.add(new UserDto(user));
        }
        count = users.getTotalElements();
    }
}
