/* TITLE:  GAME OF LIFE SIMULATOR
 * PROGRAMMER: J. SCHATTMAN
 * LAST MODIFIED FEB 25, 2014
 */
package GameOfLifeAssignment;

import java.io.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import org.jdesktop.layout.GroupLayout;

public class GameOfLifeAssignment extends JFrame {

    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(0, 400, Short.MAX_VALUE));
        layout.setVerticalGroup(
                layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(0, 300, Short.MAX_VALUE));
        pack();
    }// </editor-fold>                        
    //global variables
    //size of JFrame
    static int width = 800;
    static int height = 800;
    static int borderWidth = 50;
    //initialising variables as global so all methods can access them
    static int numCellsX;
    static int numCellsY;
    static int cellSizeX;
    static int cellSizeY;
    static int labelX;
    static int labelY;
    static int numGenerations;
    static int currGeneration = 1;
    static Color aliveColor = Color.YELLOW;
    static Color deadColor = Color.BLUE;
    static boolean alive[][];
    static boolean aliveNext[][];
    static String fileName = "Initial cells.txt"; //input file
    static String setUpFile = "Game Setup.txt"; //setup file
    //sets the initial alive cells

    public void plantFirstGeneration() throws IOException {
        makeEveryoneDead();
        Scanner instruct=new Scanner(System.in);
        System.out.println("How would you like to plant initial cells?");
        System.out.println("(Options: file, centered square, glider)");
        String instruction=instruct.next();
        if(instruction.equalsIgnoreCase("file")){
            plantFromFile(fileName);
        }else if(instruction.equalsIgnoreCase("centred square")){
            System.out.println("Please enter size of centred square");
            plantCenteredSquare(instruct.nextInt());
        }else if(instruction.equalsIgnoreCase("glider")){
            System.out.println("Please enter startX, startY, direction(NW,NE,SW,SE)");
            plantGlider(instruct.nextInt(), instruct.nextInt(),instruct.next());
        }else{
            System.err.println("Error");
        }
       
    }

    //sets all cells to dead
    public void makeEveryoneDead() {
        //iterate through entire grid, making all cells dead
        for (int i = 0; i < numCellsX; i++) {
            for (int j = 0; j < numCellsY; j++) {
                alive[i][j] = false;
            }
        }
    }

    //reads the first generations' alive cells from a file
    public void plantFromFile(String fileName) throws IOException {

        FileReader f = new FileReader(fileName);
        Scanner s = new Scanner(f);
        int x, y;
        //for all cells given in file, make the cell at (x,y) alive
        while (s.hasNext()) {
            x = s.nextInt();
            y = s.nextInt();
            alive[x][y] = true;
        }
    }

    //plants a solid rectangle of alive cells
    public void plantBlock(int startX, int startY, int numColumns, int numRows) {
        //ensure that the block does not go out of bounds
        int endCol = Math.min(startX + numColumns, numCellsX);
        int endRow = Math.min(startY + numRows, numCellsY);

        for (int i = startX; i < endCol; i++) {
            for (int j = startY; j < endRow; j++) {
                alive[i][j] = true;
            }
        }
    }

    //plants a solid square of alive cells in the centre of the grid
    public void plantCenteredSquare(int size) {
        //find start and end indices to center block
        int startCol = (numCellsX - size) / 2;
        int endCol = (numCellsX + size) / 2;
        int startRow = (numCellsY - size) / 2;
        int endRow = (numCellsY + size) / 2;

        for (int i = startCol; i < endCol; i++) {
            for (int j = startRow; j < endRow; j++) {
                alive[i][j] = true;
            }
        }
    }

    //plants a "glider" group, which is a cluster of living cells that migrates across the grid from 1 generation to the next
    public void plantGlider(int startX, int startY, String direction) { //direction can be "SW", "NW", "SE", or "NE"
        //if the glider will be in bounds, then plant the glider.
        if (startX >= 0 && (startX + 2) < numCellsX && startY >= 0 && (startY + 2) < numCellsY) {
            //depending on direction, plant the cells differently (rotate pattern 90 degrees)
            switch (direction) {
                case "NE":
                    alive[startX][startY + 1] = true;
                    alive[startX + 1][startY] = true;
                    alive[startX + 2][startY] = true;
                    alive[startX + 2][startY + 1] = true;
                    alive[startX + 2][startY + 2] = true;
                    break;
                case "SW":
                    alive[startX][startY] = true;
                    alive[startX][startY + 1] = true;
                    alive[startX][startY + 2] = true;
                    alive[startX + 1][startY + 2] = true;
                    alive[startX + 2][startY + 1] = true;
                    break;
                case "SE":
                    alive[startX][startY + 2] = true;
                    alive[startX + 1][startY] = true;
                    alive[startX + 1][startY + 2] = true;
                    alive[startX + 2][startY + 2] = true;
                    alive[startX + 2][startY + 1] = true;
                    break;
                case "NW":
                    alive[startX][startY] = true;
                    alive[startX][startY + 1] = true;
                    alive[startX + 1][startY] = true;
                    alive[startX + 1][startY + 2] = true;
                    alive[startX + 2][startY] = true;
                    break;
            }
        }
        //if the glider will not be in bounds, give an error message
        else{
            System.err.println("Out of bounds");
        }
    }

    //applies the rules of The Game of Life to determine which cells will be alive in the next generation
    public void computeNextGeneration() {
        int a = 0;
        //iterate through all cells in the grid
        for (int i = 0; i < numCellsX; i++) {
            for (int j = 0; j < numCellsY; j++) {
                int neighbours = countLivingNeighbours(i, j);
                //if cell is alive, apply rules according to number of neighbours
                if (alive[i][j]) {
                    if (neighbours == 2 || neighbours == 3) {
                        aliveNext[i][j] = true;
                    } else {
                        aliveNext[i][j] = false;
                    }
                } //if cell is dead, apply rules according to number of neighbours
                else {
                    if (neighbours == 3) {
                        aliveNext[i][j] = true;
                    } else {
                        aliveNext[i][j] = false;
                    }
                }
            }
        }

    }

    //sets the current generation equal to the next generation
    public void plantNextGeneration() {
        //copy temp array into original array
        for (int i = 0; i < numCellsX; i++) {
            System.arraycopy(aliveNext[i], 0, alive[i], 0, numCellsY);
        }
    }

    //counts the number of living cells adjacent to cell (i, j)
    public int countLivingNeighbours(int i, int j) {
        int n = 0;
        //iterate through the 3x3 grid of cells, of which cell (i,j) is in the center
        for (int c = i - 1; c <= i + 1; c++) {
            for (int r = j - 1; r <= j + 1; r++) {
                if (c == i && r == j) {
                    //do nothing because the cell cannot be its own neighbour
                } 
                //only check inbound cells
                else if (c >= 0 && c < numCellsX && r >= 0 && r < numCellsY) {
                    if (alive[c][r]) {
                        n++; //if cell is alive, then add a neighbour
                    }
                }
            }
        }
        return n;
    }

    //makes the pause
    public static void sleep(int duration) {
        try {
            Thread.sleep(duration);
        } catch (Exception e) {
        }
    }

    //displays the statistics at the top of the screen
    void drawLabel(Graphics g, int state) {
        g.setColor(Color.black);
        g.fillRect(0, 0, width, borderWidth);
        g.setColor(Color.yellow);
        g.drawString("Generation: " + state, labelX, labelY);
    }

    //draws the current generation of living cells on the screen
    public void paint(Graphics g) {
        int x = borderWidth;
        int y;

        drawLabel(g, currGeneration);

        for (int i = 0; i < numCellsX; i++) {
            y = borderWidth; //starts each column, maintaining the border width
            for (int j = 0; j < numCellsY; j++) {
                if (alive[i][j]) {
                    g.setColor(aliveColor);
                } else {
                    g.setColor(deadColor);
                }
                //colors in cell with color based on state
                g.fillRect(x, y, cellSizeX, cellSizeY);
                g.setColor(Color.black);
                g.drawRect(x, y, cellSizeX, cellSizeY);
                //moves down one row
                y += cellSizeY;
            }
            //moves down one column
            x += cellSizeX;
        }
    }

    //sets up the JFrame screen
    public void initializeWindow() {
        setTitle("Game of Life Simulator");
        setSize(height, width);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setBackground(Color.black);
        setVisible(true);
    }
    
    //reads in size of grid and number of generations from a file
    public void setUp() throws IOException {
        Scanner scan = new Scanner(new FileReader(setUpFile));

        numCellsX = scan.nextInt(); //width of the grid (in cells)
        numCellsY = scan.nextInt(); //height of grid (in cells)
        //calculating individual cell size
        cellSizeX = (width - (2 * (borderWidth))) / numCellsX;
        cellSizeY = (height - (2 * (borderWidth))) / numCellsY;
        //label set-up info
        labelX = width / 2;
        labelY = borderWidth;
        //game info
        numGenerations = scan.nextInt();

        alive = new boolean[numCellsX][numCellsY]; //grid array
        aliveNext = new boolean[numCellsX][numCellsY]; //temporary grid array
    }
    
    //main algorithm
    public static void main(String args[]) throws IOException {
        GameOfLifeAssignment currGame = new GameOfLifeAssignment();
        currGame.setUp();
        currGame.plantFirstGeneration(); //Sets the initial generation of living cells
        currGame.initializeWindow();


        for (int i = 0; i < numGenerations; i++) {
            currGame.repaint();  //Draws the grid for the current generation
            sleep(100);//Pauses to allow viewer to see
            currGame.computeNextGeneration();   //Computes next generation
            currGame.plantNextGeneration(); //Copies temp array into original array
            currGeneration++;
        }
    }
}
