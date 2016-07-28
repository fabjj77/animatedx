package com.cs.control;

import com.cs.persistence.NotFoundException;
import com.cs.security.AccessDeniedException;
import com.cs.system.SystemControl;
import com.cs.system.WhiteListedEmail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;

/**
 * @author Omid Alaepour
 */
@Service
@Transactional(isolation = READ_COMMITTED)
public class SystemControlServiceImpl implements SystemControlService {

    private final SystemControlRepository systemControlRepository;

    @Autowired
    public SystemControlServiceImpl(final SystemControlRepository systemControlRepository) {
        this.systemControlRepository = systemControlRepository;
    }

    private SystemControl getSystemControl() {
        final SystemControl systemControl = systemControlRepository.findOne(SystemControl.ID);

        if (systemControl == null) {
            throw new NotFoundException("System control settings not available");
        }
        return systemControl;
    }

    @Override
    public void validateRegistrationAllowed(final String email) {
        if (checkEmail(email)) {
            return;
        }
        if (!getSystemControl().getRegistrationEnabled()) {
            throw new AccessDeniedException("Registration is disabled");
        }
    }

    @Override
    public boolean validateLoginAllowed(final String email) {
        return checkEmail(email) ? true : getSystemControl().getLoginEnabled();
    }

    @Override
    public boolean isBrontoEnabled() {
        return getSystemControl().getBrontoEnabled();
    }

    private boolean checkEmail(final String email) {
        return WhiteListedEmail.isWhiteListed(email);
    }
}
