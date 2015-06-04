package com.yan.durak.server.ws;

import com.yan.durak.gamelogic.communication.connection.IRemoteClient;

/**
 * Created by yan.braslavsky on 6/4/2015.
 */
public interface IWsConnectionListener {
    void onRemoteWsClientConnected(IRemoteClient remoteClient);
}