// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a SWEN221 assignment.
// You may not distribute it in any other way without permission.
package swen221.tetris.moves;

import swen221.tetris.logic.Board;
import swen221.tetris.logic.Rectangle;
import swen221.tetris.tetromino.ActiveTetromino;
import swen221.tetris.tetromino.Tetromino;

import java.rmi.activation.ActivationGroup;

/**
 * Implements a translation move.
 *
 * @author David J. Pearce
 * @author Marco Servetto
 *
 */
public abstract class AbstractTranslation extends AbstractMove implements Move {
	/**
	 * Amount to translate x-coordinate.
	 */
	private final int dx;
	/**
	 * Amount to translate y-coordinate.
	 */
	private final int dy;

	/**
	 * Construct new TranslationMove for a given amount of horizontal and vertical
	 * translation.
	 *
	 * @param dx
	 *            Amount to translate in horizontal direction.
	 * @param dy
	 *            Amount to translate in vertical direction.
	 */
	public AbstractTranslation(int dx, int dy) {
		this.dx = dx;
		this.dy = dy;
	}

	@Override
	public Board apply(Board board) {
		// Create copy of the board to prevent modifying its previous state.
		board = new Board(board);
		// Apply translation for this move
		ActiveTetromino tetromino = board.getActiveTetromino().translate(dx, dy);
		// Apply the move to the new board.
		board.setActiveTetromino(tetromino);
		// Return updated version of board
		return board;
	}

	@Override
	public Board step(Board board) {
		// Create copy of the board to prevent modifying its previous state.
		board = new Board(board);
		// Determine units of translation
		int x = toUnit(dx);
		int y = toUnit(dy);
		// Apply translation for this move
		ActiveTetromino tetromino = board.getActiveTetromino().translate(x, y);
		// Apply the move to the new board.
		board.setActiveTetromino(tetromino);
		// Return updated version of board
		return board;
	}

	@Override
	public boolean isValid(Board board) {
		if(!super.isValid(board))
			return false;

		// Create copy of the board where the move has been applied
		Board boardCopy = apply(board);

		ActiveTetromino t = boardCopy.getActiveTetromino();

		if(board.IsOutsideBoard(t))
			return false;

		// Check if move collides with existing placed tetrominos
		for(int x = t.getBoundingBox().getMinX(); x <= t.getBoundingBox().getMaxX(); ++x) {
			for(int y = t.getBoundingBox().getMinY(); y <= t.getBoundingBox().getMaxY(); ++y) {
				if(!t.isWithin(x, y)) // Skip cells that do not contain the tetromino
					continue;

				if(board.getPlacedTetrominoAt(x, y) != null)
					return false;
			}
		}

		return true;
	}

	private int toUnit(int value) {
		if(value < 0) {
			return -1;
		} else if(value > 0) {
			return 1;
		} else {
			return 0;
		}
	}
}
