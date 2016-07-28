package com.cs.promotion;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.UserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.EnumSet;
import java.util.Set;

/**
 * @author Hadi Movaghar
 */
public class PromotionTriggerType implements UserType {
    private final Logger logger = LoggerFactory.getLogger(PromotionTriggerType.class);

    @Override
    public int[] sqlTypes() {
        return new int[]{Types.VARCHAR};
    }

    @Override
    public Class returnedClass() {
        return Set.class;
    }

    @Override
    public boolean equals(final Object x, final Object y)
            throws HibernateException {
        if (!(x instanceof Set && y instanceof Set)) {
            return false;
        }

        final Set xSet = (Set) x;
        final Set ySet = (Set) y;

        return xSet.equals(ySet);
    }

    @Override
    public int hashCode(final Object x)
            throws HibernateException {
        return x.hashCode();
    }

    @Nullable
    @Override
    public Object nullSafeGet(final ResultSet rs, final String[] names, final SessionImplementor session, final Object owner)
            throws HibernateException, SQLException {
        final String dbData = rs.getString(names[0]);
        if (Strings.isNullOrEmpty(dbData)) {
            return EnumSet.noneOf(PromotionTrigger.class);
        }

        final Iterable<String> strings = Splitter.on(";").split(dbData);
        final EnumSet<PromotionTrigger> triggers = EnumSet.noneOf(PromotionTrigger.class);
        for (final String trigger : strings) {
            try {
                triggers.add(PromotionTrigger.valueOf(trigger));
            } catch (final IllegalArgumentException | NullPointerException e) {
                logger.error("PromotionTrigger {} cannot be mapped to any of the PromotionTrigger values [{}]", trigger, PromotionTrigger.values());
            }
        }
        return triggers;
    }

    @Override
    public void nullSafeSet(final PreparedStatement st, final Object value, final int index, final SessionImplementor session)
            throws HibernateException, SQLException {
        if (value == null) {
            st.setNull(index, Types.VARCHAR);
            return;
        }

        @SuppressWarnings("unchecked") final Set<PromotionTrigger> triggers = (Set<PromotionTrigger>) value;
        final String joined = Joiner.on(";").join(triggers);
        st.setString(index, joined);
    }

    @Nullable
    @Override
    public Object deepCopy(final Object value)
            throws HibernateException {
        if (value == null) {
            return null;
        }

        @SuppressWarnings("unchecked") final Set<PromotionTrigger> triggers = (Set<PromotionTrigger>) value;
        return EnumSet.copyOf(triggers);
    }

    @Override
    public boolean isMutable() {
        return true;
    }

    @Override
    public Serializable disassemble(final Object value)
            throws HibernateException {
        return (Serializable) value;
    }

    @Override
    public Object assemble(final Serializable cached, final Object owner)
            throws HibernateException {
        return cached;
    }

    @Override
    public Object replace(final Object original, final Object target, final Object owner)
            throws HibernateException {
        return original;
    }
}
