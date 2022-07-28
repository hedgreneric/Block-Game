package hw3;

import static api.Orientation.*;
import static api.CellType.*;

import java.util.ArrayList;

import api.Cell;

/**
 * Utilities for parsing string descriptions of a grid.
 * 
 * @author Eric Hedgren
 */
public class GridUtil {
	/**
	 * Constructs a 2D grid of Cell objects given a 2D array of cell descriptions.
	 * String descriptions are a single character and have the following meaning.
	 * <ul>
	 * <li>"*" represents a wall.</li>
	 * <li>"e" represents an exit.</li>
	 * <li>"." represents a floor.</li>
	 * <li>"[", "]", "^", "v", or "#" represent a part of a block. A block is not a
	 * type of cell, it is something placed on a cell floor. For these descriptions
	 * a cell is created with CellType of FLOOR. This method does not create any
	 * blocks or set blocks on cells.</li>
	 * </ul>
	 * The method only creates cells and not blocks. See the other utility method
	 * findBlocks which is used to create the blocks.
	 * 
	 * @param desc a 2D array of strings describing the grid
	 * @return a 2D array of cells the represent the grid without any blocks present
	 */
	public static Cell[][] createGrid(String[][] desc) {
		//runs through each element to see what string it has depending on the string it sets
		//the cell to wall, exit, or floor
		Cell[][] cellGrid = new Cell[desc.length][desc[0].length];
		for (int i = 0; i < desc.length; i++) {
			for (int j = 0; j < desc[i].length; j++) {
				if (desc[i][j].equals("*")) {
					cellGrid[i][j] = new Cell(WALL, i, j);
				}
				else if (desc[i][j].equals("e")) {
					cellGrid[i][j] = new Cell(EXIT, i, j);
				}
				else {
					cellGrid[i][j] = new Cell(FLOOR, i, j);
				}
			}
		}
		return cellGrid;
	}

	/**
	 * Returns a list of blocks that are constructed from a given 2D array of cell
	 * descriptions. String descriptions are a single character and have the
	 * following meanings.
	 * <ul>
	 * <li>"[" the start (left most column) of a horizontal block</li>
	 * <li>"]" the end (right most column) of a horizontal block</li>
	 * <li>"^" the start (top most row) of a vertical block</li>
	 * <li>"v" the end (bottom most column) of a vertical block</li>
	 * <li>"#" inner segments of a block, these are always placed between the start
	 * and end of the block</li>
	 * <li>"*", ".", and "e" symbols that describe cell types, meaning there is not
	 * block currently over the cell</li>
	 * </ul>
	 * 
	 * @param desc a 2D array of strings describing the grid
	 * @return a list of blocks found in the given grid description
	 */
	public static ArrayList<Block> findBlocks(String[][] desc) {
		//find start of block then counts number of cells until the end of block
		//adds a new block to blocks array with firstRow, firstCol, and length
		ArrayList<Block> blocks = new ArrayList<Block>();
		int count;
		for (int i = 0; i < desc.length; i++) {
			for (int j = 0; j < desc[i].length; j++) {
				int firstRow = i;
				int firstCol = j;
				if (desc[i][j].equals("[")) {
					j++;
					count = 2;
					while (!desc[i][j].equals("]")) {
						count++;
						j++;
					}
					blocks.add(new Block(firstRow, firstCol, count, HORIZONTAL));
				}
				else if (desc[i][j].equals("^")) {
					int k = i + 1;
					count = 2;
					while (!desc[k][j].equals("v")) {
						count++;
						k++;
					}
					blocks.add(new Block(firstRow, firstCol, count, VERTICAL));
				}
			}
		}
		return blocks;
	}
}
