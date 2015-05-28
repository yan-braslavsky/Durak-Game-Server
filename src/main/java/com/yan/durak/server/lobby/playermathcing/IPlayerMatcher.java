package com.yan.durak.server.lobby.playermathcing;

/**
 * Created by Yan-Home on 3/11/2015.
 */
public interface IPlayerMatcher {

    /**
     * Reserves a place on the table for the player.
     *
     * @param payload contains JSON encoded information needed for matching
     * @return socket connection address of the table
     */
    String matchPlayer(String payload);
}
