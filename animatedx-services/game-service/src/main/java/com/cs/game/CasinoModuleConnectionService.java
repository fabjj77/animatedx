package com.cs.game;

import com.casinomodule.api.CasinoMCService;

import javax.xml.rpc.ServiceException;
import java.net.MalformedURLException;

/**
 * @author Hadi Movaghar
 */
public interface CasinoModuleConnectionService {
    CasinoMCService getCasinoMCService()
            throws MalformedURLException, ServiceException;
}
