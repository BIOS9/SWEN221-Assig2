// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a SWEN221 assignment.
// You may not distribute it in any other way without permission.
package swen221.tetris.logic;

import java.util.Arrays;
import java.util.Iterator;

import swen221.tetris.tetromino.ActiveTetromino;
import swen221.tetris.tetromino.O_Tetromino;
import swen221.tetris.tetromino.Tetromino;

/**
 * A Board instance represent a board configuration for a game of Tetris. It is
 * represented as an array of rows, where every row contains a given number of
 * columns.
 *
 * @author David J. Pearce
 * @author Marco Servetto
 */
public class Board {
	/**
	 * The width of the board in columns.
	 */
	private final int width;
	/**
	 * The height of the board in rows.
	 */
	private final int height;

	/**
	 * A row-major representation of the board. Each location contains a reference
	 * to the tetromino located there.
	 */
	private final Tetromino[] cells;

	/**
	 * The active tetromino is the one currently being controlled.
	 */
	private ActiveTetromino activeTetromino;

	public Board(Iterator<Tetromino> sequence, int width, int height) {
		this.width = width;
		this.height = height;
		this.cells = new Tetromino[width * height];
	}

	/**
	 * Create an identical copy of a given board.
	 *
	 * @param other The board being copied.
	 */
	public Board(Board other) {
		this.width = other.width;
		this.height = other.height;
		this.cells = Arrays.copyOf(other.cells, other.cells.length);
		this.activeTetromino = other.activeTetromino;
	}

	/**
	 * Get the width of this board.
	 *
	 * @return
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Get the height of this board.
	 *
	 * @return
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Get the active tetromino. This is the tetromino currently being manipulated
	 * on the board. This may be <code>null</code> if there is no active tetromino.
	 *
	 * @return
	 */
	public ActiveTetromino getActiveTetromino() {
		return activeTetromino;
	}

	/**
	 * Get any tetromino (including the active one) located at a given position on
	 * the board. If the position is out of bounds, an exception is raised.
	 * Likewise, if no Tetromino exists at that position then <code>null</code> is
	 * returned.
	 *
	 * @param x The x-coordinate of the cell to check
	 * @param y The y-coordinate of the cell to check
	 *
	 * @return is null if x and/or y points out of the board.
	 */
	public Tetromino getTetrominoAt(int x, int y) {
		if (activeTetromino != null && activeTetromino.isWithin(x, y)) {
			return activeTetromino;
		} else {
			return getPlacedTetrominoAt(x, y);
		}
	}

	/**
	 * Update the active tetromino for this board. If the tetromino has landed, it
	 * will be placed on the board and any full rows will be removed.
	 *
	 * @param tetromino
	 */
	public void setActiveTetromino(ActiveTetromino tetromino) {
		// Update the active tetromino
		this.activeTetromino = tetromino;
	}

	/**
	 * Get the placed tetromino (if any) located at a given position on the board.
	 * If the position is out of bounds, an exception is raised. Likewise, if no
	 * tetromino exists at that position then <code>null</code> is returned.
	 *
	 * @param x The x-coordinate of the cell to check
	 * @param y The y-coordinate of the cell to check
	 * @return is null if x and/or y points out of the board. *
	 */
	public Tetromino getPlacedTetrominoAt(int x, int y) {
		if (x < 0 || x >= width) {
			throw new IllegalArgumentException("Invalid column (" + x + ")");
		}
		if (y < 0 || y >= height) {
			throw new IllegalArgumentException("Invalid row (" + y + ")");
		}
		// Not part of active tetromino, so try placed ones.
		return cells[(y * width) + x];
	}

	/**
	 * Set the placed tetromino at a given position on the board. If the position is
	 * out of bounds, an exception is raised.
	 *
	 * @param x The x-coordinate of the cell to check
	 * @param y The y-coordinate of the cell to check
	 * @param t The tetromino to place, which can be <code>null</code> if the cell
	 *          is to be cleared.
	 */
	public void setPlacedTetrominoAt(int x, int y, Tetromino t) {
		if (x < 0 || x >= width) {
			throw new IllegalArgumentException("Invalid column (" + x + ")");
		}
		if (y < 0 || y >= height) {
			throw new IllegalArgumentException("Invalid row (" + y + ")");
		}
		cells[(y * width) + x] = t;
	}

	/**
	 * Check whether we can place a tetromino on the board. That is, whether or not
	 * the cells occupied by the tetromino are currently free and used by another
	 * placed tetromino. This is useful, for example, to detect the game is over as
	 * we cannot place a new tetromino on the board.
	 *
	 * @param t
	 * @return
	 */
	public boolean canPlaceTetromino(Tetromino t) {
		if(t == null)
			return false;

		Rectangle r = t.getBoundingBox();
		//
		for (int x = r.getMinX(); x <= r.getMaxX(); ++x) {
			for (int y = r.getMinY(); y <= r.getMaxY(); ++y) {
				int id = (y * width) + x;
				if (t.isWithin(x, y) && (id < 0 || id >= cells.length || cells[id] != null)) {
					return false;
				}
			}
		}
		return true;
	}


	/**
	 * Place a given Tetromino on the board by filling out each square it contains
	 * on the board.
	 *
	 * @param t Tetromino to place; cannot be null
	 */
	public void placeTetromino(Tetromino t) {
		Rectangle r = t.getBoundingBox();
		//
		for (int x = r.getMinX(); x <= r.getMaxX(); ++x) {
			for (int y = r.getMinY(); y <= r.getMaxY(); ++y) {
				if (t.isWithin(x, y)) {
					cells[(y * width) + x] = t;
				}
			}
		}
	}

	/**
	 * Checks for and removes completed lines in the game board
	 */
	public void ProcessLines() {
		for(int y = height - 1;  y >= 0; --y) {
			if(isLineFull(y))
				cascadeLines(y);
		}
	}

	/**
	 * Checks if a row is fully populated by tetromino pieces
	 * @param y
	 * @return
	 */
	private boolean isLineFull(int y) {
		for(int x = 0; x < width; ++x) {
			if(getPlacedTetrominoAt(x, y) == null)
				return false;
		}
		return true;
	}

	/**
	 * Removes the specified line and moves all the lines above down by one
	 * @param startY
	 */
	private void cascadeLines(int startY) {
		for(int currentY = startY; currentY < height - 1; ++currentY) {
			// Move the line above into the current line
			for(int x = 0; x < width; ++x) {
				Tetromino t = getPlacedTetrominoAt(x, currentY + 1);
				setPlacedTetrominoAt(x, currentY, t);
			}
		}

		// Clear last line
		for(int x = 0; x < width; ++x) {
			setPlacedTetrominoAt(x, height - 1, null);
		}
	}

	/**
	 * Checks if any part of the specified tetromino is outside of the bounds of the board
	 * @param activeTetromino
	 * @return
	 */
	public boolean IsOutsideBoard(ActiveTetromino activeTetromino) {
		// Check left edge
		if(activeTetromino.getBoundingBox().getMinX() < 0)
			return true;

		// Check right edge
		if(activeTetromino.getBoundingBox().getMaxX() >= width)
			return true;

		// Check bottom
		if(activeTetromino.getBoundingBox().getMinY() < 0)
			return true;

		return false;
	}

	/**
	 * Checks if the specified tetromino is touching the bottom of the board or if it has another tetromino in direct contact below it
	 *
	 * @param activeTetromino
	 * @return Boolean indicating if the tetromino has landed
	 */
	public boolean HasLanded(ActiveTetromino activeTetromino) {
		if(activeTetromino == null)
			return false;

		// Check if tetromino is touching the floor
		for(int x = 0; x < getWidth(); ++x) {
			if (activeTetromino.isWithin(x, 0))
				return true;
		}

		// Check if there are any pieces below the current piece
		Rectangle bounds = activeTetromino.getBoundingBox();
		for(int x = bounds.getMinX(); x <= bounds.getMaxX(); ++x)
			for(int y = bounds.getMinY(); y <= bounds.getMaxY(); ++y) {
				// Skip empty squares in the bounding box
				if(!activeTetromino.isWithin(x, y))
					continue;

				Tetromino t = getTetrominoAt(x, y-1);
				if(t != null && t != activeTetromino)
					return true;
			}
		return false;
	}

	@Override
	public String toString() {
		StringBuffer res = new StringBuffer();
		for (int y = height - 1; y >= 0; y -= 1) {
			res.append("|");
			for (int x = 0; x < width; x += 1) {
				Tetromino tetromino = getTetrominoAt(x, y);
				if (tetromino == null) {
					res.append("_");
				} else {
					res.append(tetromino.getColor().toString().charAt(0));
				}
				res.append("|");
			}
			res.append("\n");
		}
		return res.toString();
	}
}
