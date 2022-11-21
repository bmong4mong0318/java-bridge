package bridge.controller;

import bridge.constant.BridgePhrase;

import bridge.BridgeMaker;
import bridge.BridgeRandomNumberGenerator;

import bridge.domain.service.BridgeGame;

import bridge.domain.resources.bridge.Bridge;
import bridge.domain.resources.bridge.BridgeSize;

import bridge.domain.resources.GameCommand;
import bridge.domain.resources.Move;

import bridge.domain.service.BridgeStateService;
import bridge.view.InputView;
import bridge.view.OutputView;

public class BridgeGameController {

    private static final boolean RETRY = true;
    private static final boolean QUIT = false;
    private final InputView inputView;
    private final OutputView outputView;
    private boolean button;
    private boolean escape;

    public BridgeGameController() {
        this.inputView = new InputView();
        this.outputView = new OutputView();
    }

    public void run() {
        BridgeMaker bridgeMaker = setBridgeMaker();
        BridgeSize bridgeSize = readBridgeSize();
        Bridge bridge = makeBridge(bridgeMaker, bridgeSize);
        BridgeGame bridgeGame = new BridgeGame();
        gameProcess(bridgeSize, bridge, bridgeGame);
        printFinalGameResult(bridgeGame);
        printSuccessOrNot(bridgeGame);
        printTotalAttempts(bridgeGame);
    }

    private BridgeMaker setBridgeMaker() {
        outputView.print(BridgePhrase.START_GAME_PHRASE);
        BridgeRandomNumberGenerator bridgeRandomNumberGenerator = new BridgeRandomNumberGenerator();
        return new BridgeMaker(bridgeRandomNumberGenerator);
    }

    private BridgeSize readBridgeSize() {
        return inputView.readBridgeSize();
    }

    private Bridge makeBridge(BridgeMaker bridgeMaker, BridgeSize bridgeSize) {
        return new Bridge(bridgeMaker.makeBridge(bridgeSize.getSize()));
    }

    private void gameProcess(BridgeSize bridgeSize, Bridge bridge, BridgeGame bridgeGame) {
        do {
            escape = moveUpOrDown(bridgeGame, bridge, bridgeSize);
            button = retryOrQuit(bridgeGame);
            if (escape && bridgeGame.checkFlag()) {
                break;
            }
            bridgeGame.isRetry(button);
        } while (button == RETRY);
    }

    private boolean moveUpOrDown(BridgeGame bridgeGame, Bridge bridge, BridgeSize bridgeSize) {
        bridgeGame.incrementTotalAttempts();
        while (bridgeGame.checkFlag() && untilTheEnd(bridgeGame, bridgeSize)) {
            move(bridgeGame, bridge);
            printMap(bridgeGame);
        }
        return !untilTheEnd(bridgeGame, bridgeSize);
    }

    public boolean untilTheEnd(BridgeGame bridgeGame, BridgeSize bridgeSize) {
        return bridgeGame.getStepDistance() != bridgeSize.getSize();
    }

    private void move(BridgeGame bridgeGame, Bridge bridge) {
        Move move = readMoving();
        bridgeGame.move(bridge, move);
    }

    private void printMap(BridgeGame bridgeGame) {
        outputView.printMap(bridgeGame);
    }

    private Move readMoving() {
        return inputView.readMoving();
    }

    private boolean retryOrQuit(BridgeGame bridgeGame) {
        if (!bridgeGame.checkFlag()) {
            GameCommand gameCommand = readGameCommand();
            return bridgeGame.retry(gameCommand);
        }
        return true;
    }

    private GameCommand readGameCommand() {
        return inputView.readGameCommand();
    }

    private void printFinalGameResult(BridgeGame bridgeGame) {
        outputView.print(BridgePhrase.GAME_RESULT);
        outputView.printResult(bridgeGame);
    }

    private void printSuccessOrNot(BridgeGame bridgeGame) {
        if (bridgeGame.checkFlag()) {
            outputView.print(BridgePhrase.GAME_SUCCESS);
        } else if (!bridgeGame.checkFlag()) {
            outputView.print(BridgePhrase.GAME_FAIL);
        }
    }

    private void printTotalAttempts(BridgeGame bridgeGame) {
        outputView.print(BridgePhrase.GAME_TRY_COUNT, bridgeGame.totalAttempts());
    }

}

