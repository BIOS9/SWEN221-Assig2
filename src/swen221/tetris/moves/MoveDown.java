package swen221.tetris.moves;

import swen221.tetris.logic.Board;
import swen221.tetris.tetromino.ActiveTetromino;

/**
 * Move the active tetromino one square downwards.
 *
 * @author David J. Pearce
 *
 */

public class MoveDown extends AbstractTranslation {

	public MoveDown() {
		super(0,-1);
	}

	@Override
	public boolean isValid(Board board) {
		ActiveTetromino tetromino = board.getActiveTetromino();

		if(!super.isValid(board))
			return false;

		// Check if tetromino is off bottom edge of the screen
		if(tetromino.getBoundingBox().getMinY() <= 0)
			return false;

		return true;
	}
}
