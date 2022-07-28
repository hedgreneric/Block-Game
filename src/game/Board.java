package hw3;

import static api.Direction.*;
import static api.Orientation.*;

import java.util.ArrayList;

import api.Cell;
import api.Direction;
import api.Move;
import api.Orientation;

/**
 * Represents a board in the Block Slider game. A board contains a 2D grid of
 * cells and a list of blocks that slide over the cells.
 * 
 * @author Eric Hedgren
 */
public class Board {
	/**
	 * 2D array of cells, the indexes signify (row, column) with (0, 0) representing
	 * the upper-left corner of the board.
	 */
	private Cell[][] grid;

	/**
	 * A list of blocks that are positioned on the board.
	 */
	private ArrayList<Block> blocks;

	/**
	 * A list of moves that have been made in order to get to the current position
	 * of blocks on the board.
	 */
	private ArrayList<Move> moveHistory;
	/**
	 * current block that is grabbed
	 */
	private Block grabbedBlock;
	
	/**
	 * currently grabbed cell
	 */
	private Cell grabbedCell;

	/**
	 * Constructs a new board from a given 2D array of cells and list of blocks. The
	 * cells of the grid should be updated to indicate which cells have blocks
	 * placed over them (i.e., setBlock() method of Cell). The move history should
	 * be initialized as empty.
	 * 
	 * @param grid   a 2D array of cells which is expected to be a rectangular shape
	 * @param blocks list of blocks already containing row-column position which
	 *               should be placed on the board
	 */
	public Board(Cell[][] grid, ArrayList<Block> blocks) {
		int row;
		int col;
		for (Block block : blocks) {
			row = block.getFirstRow();
			col = block.getFirstCol();
			
			//setBlock for the number cells in the length of the block
			for (int f = 0; f < block.getLength(); f++) {
				if (block.getOrientation() == Orientation.HORIZONTAL) {
					grid[row][col + f].setBlock(block);
				}
				else {
					grid[row + f][col].setBlock(block);
				}
			}
		}
		this.blocks = blocks;
		this.grid = grid;
		this.moveHistory = new ArrayList<>();
	}

	/**
	 * Constructs a new board from a given 2D array of String descriptions.
	 * <p>
	 * DO NOT MODIFY THIS CONSTRUCTOR
	 * 
	 * @param desc 2D array of descriptions
	 */
	public Board(String[][] desc) {
		this(GridUtil.createGrid(desc), GridUtil.findBlocks(desc));
	}

	/**
	 * Models the user grabbing a block over the given row and column. The purpose
	 * of grabbing a block is for the user to be able to drag the block to a new
	 * position, which is performed by calling moveGrabbedBlock(). This method
	 * records two things: the block that has been grabbed and the cell at which it
	 * was grabbed.
	 * 
	 * @param row row to grab the block from
	 * @param col column to grab the block from
	 */
	public void grabBlockAtCell(int row, int col) {
		grabbedBlock = grid[row][col].getBlock();
		grabbedCell = grid[row][col];
	}

	/**
	 * Set the currently grabbed block to null.
	 */
	public void releaseBlock() {
		grabbedBlock = null;
	}

	/**
	 * Returns the currently grabbed block.
	 * 
	 * @return the current block
	 */
	public Block getGrabbedBlock() {
		return grabbedBlock;
	}

	/**
	 * Returns the currently grabbed cell.
	 * 
	 * @return the current cell
	 */
	public Cell getGrabbedCell() {
		return grabbedCell;
	}

	/**
	 * Returns true if the cell at the given row and column is available for a block
	 * to be placed over it. Blocks can only be placed over floors and exits. A
	 * block cannot be placed over a cell that is occupied by another block.
	 * 
	 * @param row row location of the cell
	 * @param col column location of the cell
	 * @return true if the cell is available for a block, otherwise false
	 */
	public boolean canPlaceBlock(int row, int col) {
		if (grid[row][col].hasBlock() || grid[row][col].isWall()) {
			return false;
		}
		return true;
	}

	/**
	 * Returns the number of moves made so far in the game.
	 * 
	 * @return the number of moves
	 */
	public int getMoveCount() {
		return moveHistory.size();
	}

	/**
	 * Returns the number of rows of the board.
	 * 
	 * @return number of rows
	 */
	public int getRowSize() {
		return grid.length;
	}

	/**
	 * Returns the number of columns of the board.
	 * 
	 * @return number of columns
	 */
	public int getColSize() {
		return grid[0].length;
	}

	/**
	 * Returns the cell located at a given row and column.
	 * 
	 * @param row the given row
	 * @param col the given column
	 * @return the cell at the specified location
	 */
	public Cell getCell(int row, int col) {
		return grid[row][col];
	}

	/**
	 * Returns a list of all blocks on the board.
	 * 
	 * @return a list of all blocks
	 */
	public ArrayList<Block> getBlocks() {
		return blocks;
	}

	/**
	 * Returns true if the player has completed the puzzle by positioning a block
	 * over an exit, false otherwise.
	 * 
	 * @return true if the game is over
	 */
	public boolean isGameOver() {
		//checks each cell to see if it is an exit and has a block then returns true
		for (int r = 0; r < grid.length; r++) {
			for (int c = 0; c < grid[r].length; c++) {
				if (grid[r][c].isExit() && grid[r][c].hasBlock()) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Moves the currently grabbed block by one cell in the given direction. A
	 * horizontal block is only allowed to move right and left and a vertical block
	 * is only allowed to move up and down. A block can only move over a cell that
	 * is a floor or exit and is not already occupied by another block. The method
	 * does nothing under any of the following conditions:
	 * <ul>
	 * <li>The game is over.</li>
	 * <li>No block is currently grabbed by the user.</li>
	 * <li>A block is currently grabbed by the user, but the block is not allowed to
	 * move in the given direction.</li>
	 * </ul>
	 * If none of the above conditions are meet, the method does the following:
	 * <ul>
	 * <li>Moves the block object by calling its move method.</li>
	 * <li>Sets the block for the grid cell that the block is being moved into.</li>
	 * <li>For the grid cell that the block is being moved out of, sets the block to
	 * null.</li>
	 * <li>Moves the currently grabbed cell by one cell in the same moved direction.
	 * The purpose of this is to make the currently grabbed cell move with the block
	 * as it is being dragged by the user.</li>
	 * <li>Adds the move to the end of the moveHistory list.</li>
	 * <li>Increment the count of total moves made in the game.</li>
	 * </ul>
	 * 
	 * @param dir the direction to move
	 */
	public void moveGrabbedBlock(Direction dir) {
		if (isGameOver() || getGrabbedBlock() == null) {
			return;
		}
		
		//if statements sees if the move that the player is trying to make is apart of the possible moves 
		//if true then sets block that it is moving from to null then the new block to have a block
		for (Move move : getAllPossibleMoves()) {
			int row = getGrabbedBlock().getFirstRow();
			int col = getGrabbedBlock().getFirstCol();
			
			if (dir == LEFT && dir == move.getDirection() && (row == move.getBlock().getFirstRow()) && (col == move.getBlock().getFirstCol())) {
				grid[row][col + getGrabbedBlock().getLength() - 1].setBlock(null);
				grid[row][col - 1].setBlock(getGrabbedBlock());
				getGrabbedBlock().setFirstCol(col - 1);
				moveHistory.add(move);
				releaseBlock();
				grabBlockAtCell(move.getBlock().getFirstRow(), move.getBlock().getFirstCol());
			}
			else if (dir == RIGHT && dir == move.getDirection() && (row == move.getBlock().getFirstRow()) && (col == move.getBlock().getFirstCol())) {
				grid[row][col].setBlock(null);
				grid[row][col + 1].setBlock(getGrabbedBlock());
				getGrabbedBlock().setFirstCol(col + 1);
				moveHistory.add(move);
				releaseBlock();
				grabBlockAtCell(move.getBlock().getFirstRow(), move.getBlock().getFirstCol());
			}
			else if (dir == DOWN && dir == move.getDirection() && (row == move.getBlock().getFirstRow()) && (col == move.getBlock().getFirstCol())) {
				grid[row][col].setBlock(null);
				grid[row + getGrabbedBlock().getLength() - 1][col].setBlock(getGrabbedBlock());
				getGrabbedBlock().setFirstRow(row + 1);
				moveHistory.add(move);
				releaseBlock();
				grabBlockAtCell(move.getBlock().getFirstRow(), move.getBlock().getFirstCol());
			}
			else if (dir == UP && dir == move.getDirection() && (row == move.getBlock().getFirstRow()) && (col == move.getBlock().getFirstCol())) {
				grid[row + getGrabbedBlock().getLength() - 1][col].setBlock(null);
				grid[row - 1][col].setBlock(getGrabbedBlock());
				getGrabbedBlock().setFirstRow(row - 1);
				moveHistory.add(move);
				releaseBlock();
				grabBlockAtCell(move.getBlock().getFirstRow(), move.getBlock().getFirstCol());
			}
		}
	}

	/**
	 * Resets the state of the game back to the start, which includes the move
	 * count, the move history, and whether the game is over. The method calls the
	 * reset method of each block object. It also updates each grid cells by calling
	 * their setBlock method to either set a block if one is located over the cell
	 * or set null if no block is located over the cell.
	 */
	public void reset() {
		moveHistory = new ArrayList<Move>();
		
		//resets all the blocks to their original firstRow and firstCol
		for(Block b : blocks) {
			b.reset();
		}
		
		//clears the entire board of all blocks
		for (int r = 0; r < grid.length; r++) {
			for (int c = 0; c < grid[r].length; c++) {
				grid[r][c].setBlock(null);
			}
		}
		
		//places the blocks onto the board
		for (Block block : blocks) {
			int firstRow = block.getFirstRow();
			int firstCol = block.getFirstCol();
			for (int i = 0; i < block.getLength(); i++) {
				if (block.getOrientation() == VERTICAL) {
					grid[firstRow + i][firstCol].setBlock(block);
				}
				else if (block.getOrientation() == HORIZONTAL) {
					grid[firstRow][firstCol + i].setBlock(block);
				}
			}
		}
	}

	/**
	 * Returns a list of all legal moves that can be made by any block on the
	 * current board. If the game is over there are no legal moves.
	 * 
	 * @return a list of legal moves
	 */
	public ArrayList<Move> getAllPossibleMoves() {
		ArrayList<Move> legalMoves = new ArrayList<>();
		
		//runs through all the blocks and sees all the possible ways that it could move
		//possible moves are ones that have a floor, exit, and don't have a block
		//then adds that move to legalMoves
		for (Block block : blocks) {
			int row = block.getFirstRow();
			int col = block.getFirstCol();
			if (block.getOrientation() == VERTICAL) {
				if (row > 0 && !grid[row - 1][col].hasBlock() &&
						(grid[row - 1][col].isFloor() || grid[row - 1][col].isExit())) {
					Move move = new Move(block, UP);
					legalMoves.add(move);
				}
				if (row + block.getLength() - 1 < grid.length - 1 && !grid[row + block.getLength()][col].hasBlock()
						&& (grid[row + block.getLength()][col].isExit() || grid[row + block.getLength()][col].isFloor())) {
					Move move = new Move(block, DOWN);
					legalMoves.add(move);
				}
			}
			else if (block.getOrientation() == HORIZONTAL) {
				if (col > 0 && !grid[row][col - 1].hasBlock() &&
						(grid[row][col - 1].isFloor() || grid[row][col - 1].isExit())) {
					Move move = new Move(block, LEFT);
					legalMoves.add(move);
				}
				if (col + block.getLength() - 1 < grid[0].length - 1 && !grid[row][col + block.getLength()].hasBlock()
						&& (grid[row][col + block.getLength()].isExit() || grid[row][col + block.getLength()].isFloor())) {
					Move move = new Move(block, RIGHT);
					legalMoves.add(move);
				}
			}
		}
		return legalMoves;
	}

	/**
	 * Gets the list of all moves performed to get to the current position on the
	 * board.
	 * 
	 * @return a list of moves performed to get to the current position
	 */
	public ArrayList<Move> getMoveHistory() {
		return moveHistory;
	}

	/**
	 * EXTRA CREDIT 5 POINTS
	 * <p>
	 * This method is only used by the Solver.
	 * <p>
	 * Undo the previous move. The method gets the last move on the moveHistory list
	 * and performs the opposite actions of that move, which are the following:
	 * <ul>
	 * <li>grabs the moved block and calls moveGrabbedBlock passing the opposite
	 * direction</li>
	 * <li>decreases the total move count by two to undo the effect of calling
	 * moveGrabbedBlock twice</li>
	 * <li>if required, sets is game over to false</li>
	 * <li>removes the move from the moveHistory list</li>
	 * </ul>
	 * If the moveHistory list is empty this method does nothing.
	 */
	public void undoMove() {
		if (moveHistory.size() == 0) {
			return;
		}
		//does the reverse move of the previous move
		if (moveHistory.get(moveHistory.size() - 1).getDirection() == UP) {
			moveHistory.get(moveHistory.size() - 1).getBlock().move(DOWN);
		}
		else if (moveHistory.get(moveHistory.size() - 1).getDirection() == DOWN) {
			moveHistory.get(moveHistory.size() - 1).getBlock().move(UP);
		}
		else if (moveHistory.get(moveHistory.size() - 1).getDirection() == RIGHT) {
			moveHistory.get(moveHistory.size() - 1).getBlock().move(LEFT);
		}
		else if (moveHistory.get(moveHistory.size() - 1).getDirection() == LEFT) {
			moveHistory.get(moveHistory.size() - 1).getBlock().move(RIGHT);
		}
		
		//removes the previous move and the reversed move from moveHistory
		moveHistory.remove(moveHistory.size() - 1);
		moveHistory.remove(moveHistory.size() - 1);
	}

	@Override
	public String toString() {
		StringBuffer buff = new StringBuffer();
		boolean first = true;
		for (Cell row[] : grid) {
			if (!first) {
				buff.append("\n");
			} else {
				first = false;
			}
			for (Cell cell : row) {
				buff.append(cell.toString());
				buff.append(" ");
			}
		}
		return buff.toString();
	}
}
