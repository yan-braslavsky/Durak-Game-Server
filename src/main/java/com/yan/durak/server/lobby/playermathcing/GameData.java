package com.yan.durak.server.lobby.playermathcing;

/**
 * Created by Yan-Home on 4/2/2015.
 */
public class GameData {

    public enum GameType {
        ONE_PLAYER_TWO_BOTS(1),
        TWO_PLAYERS_ONE_BOT(2),
        THREE_PLAYERS(3);

        private final int mNumberOfHumanPlayers;

        GameType(int numOfHumanPlayers) {
            mNumberOfHumanPlayers = numOfHumanPlayers;
        }

        /**
         * Maps the payload received gametype to {@link GameType}
         *
         * @param gameType
         * @return
         */
        public static GameType fromPayloadValue(String gameType) {
            switch (gameType) {
                case "ONE_PLAYER_TWO_BOTS":
                    return ONE_PLAYER_TWO_BOTS;
                case "TWO_PLAYERS_ONE_BOT":
                    return TWO_PLAYERS_ONE_BOT;
                case "THREE_PLAYERS":
                    return THREE_PLAYERS;
                default:
                    return ONE_PLAYER_TWO_BOTS;
            }
        }

        public int getNumberOfHumanPlayers() {
            return mNumberOfHumanPlayers;
        }
    }

    private String mPlayerZeroId;
    private String mPlayerOneId;
    private String playerTwoId;
    private GameData mGameData;

    public String getPlayerZeroId() {
        return mPlayerZeroId;
    }

    public String getPlayerOneId() {
        return mPlayerOneId;
    }

    public String getPlayerTwoId() {
        return playerTwoId;
    }

    public void setPlayerZeroId(String playerZeroId) {
        this.mPlayerZeroId = playerZeroId;
    }

    public void setPlayerOneId(String playerOneId) {
        this.mPlayerOneId = playerOneId;
    }

    public void setPlayerTwoId(String playerTwoId) {
        this.playerTwoId = playerTwoId;
    }
}
