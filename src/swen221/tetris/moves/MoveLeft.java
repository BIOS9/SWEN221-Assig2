package swen221.tetris.moves;

import swen221.tetris.logic.Board;
import swen221.tetris.tetromino.ActiveTetromino;

/**
 * Move the active tetromino one square to the left.
 *
 * @author David J. Pearce
 *
 */
public class MoveLeft extends AbstractTranslation {

	public MoveLeft() {
		super(-1,0);
	}

	@Override
	public boolean isValid(Board board) {
		ActiveTetromino tetromino = board.getActiveTetromino();

		if(!super.isValid(board))
			return false;

		// Check if tetromino is off left edge of screen
		if(tetromino.getBoundingBox().getMinX() <= 0)
			return false;

		return true;
	}
}
