package com.cs.game;

import com.casinomodule.api.CasinoMCAPILocator;
import com.casinomodule.api.CasinoMCService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.xml.rpc.ServiceException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Hadi Movaghar
 */
@Service
public class CasinoModuleConnectionServiceImpl implements CasinoModuleConnectionService {
    @Value("${game.casino-soap-multi-currency-service-url}")
    private String casinoSoapMultiCurrencyServiceUrl;

    @Override
    public CasinoMCService getCasinoMCService()
            throws MalformedURLException, ServiceException {
        return new CasinoMCAPILocator().getCasinoMC(new URL(casinoSoapMultiCurrencyServiceUrl));
    }
}
