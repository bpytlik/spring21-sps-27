package com.google.sps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;
import java.util.List;
import java.util.SplittableRandom;

/** 
 * 
 */
enum Cell {

    DEAD ((byte) '0'),
    ALIVE ((byte) '1'),
    ALIVE_MASK ((byte) '2'),
    ALIVE_VACCINE ((byte) '3'),
    ALIVE_MASK_AND_VACCINE ((byte) '4'),
    INFECTED ((byte) '5'),
    INFECTED_MASK ((byte) '6'),
    INFECTED_VACCINE ((byte) '7'),
    INFECTED_MASK_AND_VACCINE ((byte) '8');

    private byte cellCode;
  
    public byte getCode() {
        return this.cellCode;
    }
  
    private Cell(byte cellCode) {
        this.cellCode = cellCode;
    }
}


public class Matrix {

    private byte[][] matrix;
    private InfectionProbability infectionProbability;

    public Matrix(String stringMatrix, InfectionProbability infectionProbability) {
        stringToMatrix(stringMatrix);
        this.infectionProbability = infectionProbability;
    }

    public void updateMatrix() {
        int rows = this.matrix.length,
            columns = this.matrix[0].length;

        byte[][] tempMatrix = getMatrixCopy(this.matrix);

        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                this.updateCell(row, column, tempMatrix);
            }
        }

        System.out.println();
        printMatrix(this.matrix);
    }

    private void updateCell(int row, int column, byte[][] tempMatrix) {
        List<Byte> neighbors = getNeighbors(row, column, tempMatrix);
        int numberOfNeighbors = neighbors.size();

        Cell currentCell = encodeCell(tempMatrix[row][column]);

        if (currentCell != Cell.DEAD) {
            List<Cell> aliveCells = Arrays.asList   (   
                                                        Cell.ALIVE, Cell.ALIVE_MASK, 
                                                        Cell.ALIVE_VACCINE, Cell.ALIVE_MASK_AND_VACCINE
                                                    );

            if (numberOfNeighbors < 2 || numberOfNeighbors > 3) {
                this.matrix[row][column] = Cell.DEAD.getCode();
            }
            else if (aliveCells.contains(currentCell)) {
                Cell infectedNeighbor = getInfectedNeighbor(neighbors);

                if (infectedNeighbor != null) {
                    boolean isCellInfected = this.isCellInfected(currentCell, infectedNeighbor);
                    if (isCellInfected) this.matrix[row][column] = getInfectedVersionOfCell(currentCell).getCode();
                }
            }
        }
        else {
            if (numberOfNeighbors == 3) {
                this.matrix[row][column] = Cell.ALIVE.getCode();
            }
        }
    }

    private boolean isCellInfected(Cell aliveCell, Cell infectedCell) {
        double  aliveProbability = this.getInfectionProbability(aliveCell),
                infectedProbability = this.getInfectionProbability(infectedCell),
                combinedInfectionProbability = aliveProbability + infectedProbability - (aliveProbability * infectedProbability);

        SplittableRandom random = new SplittableRandom();
        double randomProbability = random.nextDouble(1.0);
        
        return randomProbability < combinedInfectionProbability; 
    }

    private double getInfectionProbability(Cell cell) {
        if (cell == Cell.ALIVE || cell == Cell.INFECTED) return infectionProbability.NO_PROTECTION;
        else if (cell == Cell.ALIVE_MASK || cell == Cell.INFECTED_MASK) return infectionProbability.WITH_MASK;
        else if (cell == Cell.ALIVE_VACCINE || cell == Cell.INFECTED_VACCINE) return infectionProbability.WITH_VACCINE;
        else return infectionProbability.WITH_MASK_AND_VACCINE;
    }

    private Cell getInfectedVersionOfCell(Cell cell) {        
        if (cell == Cell.ALIVE) return Cell.INFECTED;
        else if (cell == Cell.ALIVE_MASK) return Cell.INFECTED_MASK;
        else if (cell == Cell.ALIVE_VACCINE) return Cell.INFECTED_VACCINE;
        else if (cell == Cell.ALIVE_MASK_AND_VACCINE) return Cell.INFECTED_MASK_AND_VACCINE;
        else return cell;
    }

    private Cell getInfectedNeighbor(List<Byte> neighbors) {
        int numberOfNeighbors = neighbors.size();


        List<Byte> infectedCells = Arrays.asList   (   Cell.INFECTED.getCode(), Cell.INFECTED_MASK.getCode(), 
                                                        Cell.INFECTED_VACCINE.getCode(), Cell.INFECTED_MASK_AND_VACCINE.getCode()
                                                    );

        List<Byte> infectedNeighbors = new ArrayList<Byte>();

        Map<Byte, Integer> frequencyMap = new HashMap<>();
        
        int maxFrequency = 0;

        for (byte neighbor: neighbors) {
            if (infectedCells.contains(neighbor)) {
                Integer count = frequencyMap.get(neighbor);
                if (count == null) count = 0;
 
                frequencyMap.put(neighbor, count + 1);
                maxFrequency = Math.max(maxFrequency, count + 1);
            }
        }

        for (byte neighbor: neighbors) {
            if (frequencyMap.containsKey(neighbor) && frequencyMap.get(neighbor) == maxFrequency) {
                infectedNeighbors.add(neighbor);
                frequencyMap.remove(neighbor);
            }
        }

        SplittableRandom random = new SplittableRandom();
        int randomNeighborIndex = random.nextInt(infectedNeighbors.size());

        System.out.println(infectedNeighbors.toString() + ", " + infectedNeighbors.size() + ", " + infectedNeighbors.get(randomNeighborIndex));

        return encodeCell(infectedNeighbors.get(randomNeighborIndex));
    }

    private Cell encodeCell(byte code) {
        for (Cell cell: Cell.values()) {
            if (code == cell.getCode()) return cell;
        }

        return null;
    }

    private List<Byte> getNeighbors(int row, int column, byte[][] tempMatrix) { //TODO: Use a For Loop
        List<Byte> neighbors = new ArrayList<Byte>();

        if (this.hasAliveNeighbor(row-1,column-1,tempMatrix)) {
            neighbors.add(tempMatrix[row-1][column-1]);
        }

        if (this.hasAliveNeighbor(row-1,column,tempMatrix)) {
            neighbors.add(tempMatrix[row-1][column]);
        }

        if (this.hasAliveNeighbor(row-1,column+1,tempMatrix)) {
            neighbors.add(tempMatrix[row-1][column+1]);
        }


        if (this.hasAliveNeighbor(row+1,column+1,tempMatrix)) {
            neighbors.add(tempMatrix[row][column+1]);
        }

        if (this.hasAliveNeighbor(row+1,column,tempMatrix)) {
            neighbors.add(tempMatrix[row+1][column]);
        }

        if (this.hasAliveNeighbor(row+1,column-1,tempMatrix)) {
            neighbors.add(tempMatrix[row+1][column-1]);
        }


        if (this.hasAliveNeighbor(row,column+1,tempMatrix)) {
            neighbors.add(tempMatrix[row][column+1]);
        }

        if (this.hasAliveNeighbor(row,column-1,tempMatrix)) {
            neighbors.add(tempMatrix[row][column-1]);
        }

        return neighbors;
    }

    private boolean hasAliveNeighbor(int row, int column, byte[][] tempMatrix) {
        int rows = tempMatrix.length,
            columns = tempMatrix[0].length;

        boolean boundaryCheck = row >= 0 && row < rows && column >= 0 && column < columns;
        return boundaryCheck && tempMatrix[row][column] != Cell.DEAD.getCode(); 
    }


    private void stringToMatrix(String stringMatrix) { // TODO: implement validation

        int[] commaIndexes = findCommaIndexes(stringMatrix);

        int rows = Integer.parseInt(stringMatrix.substring(0, commaIndexes[0])),
            columns = Integer.parseInt(stringMatrix.substring(commaIndexes[0]+1, commaIndexes[1]));

        int currentCell = commaIndexes[1]+1;

        this.matrix = new byte[rows][columns];

        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                this.matrix[row][column] = (byte) stringMatrix.charAt(currentCell++);
            }
        }

        this.printMatrix(this.matrix);

    }

    private int[] findCommaIndexes(String stringMatrix) {
        int[] commaIndexes = new int[2];

        boolean firstCommaFound = false;

        for (int i=0; i<stringMatrix.length(); i++) {
            if (stringMatrix.charAt(i) == ',') {
                if (!firstCommaFound) {
                    commaIndexes[0] = i;
                    firstCommaFound = true;
                }
                else {
                    commaIndexes[1] = i;
                    break;
                }
            }
        }
        return commaIndexes;

    }

    private void printMatrix(byte[][] matrix) { 
        int rows = matrix.length,
            columns = matrix[0].length;

        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                System.out.print((char) matrix[row][column] + " ");
            }
            System.out.println();
        }
    }

    private byte[][] getMatrixCopy(byte[][] matrix) {
        int rows = matrix.length,
            columns = matrix[0].length;

        byte[][] matrixCopy = new byte[rows][columns];

        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                matrixCopy[row][column] = matrix[row][column];
            }
        }

        return matrixCopy;
    }

    public String getStringMatrix() {
        int rows = this.matrix.length,
            columns = this.matrix[0].length;

        String stringMatrix = Integer.toString(rows) + "," + Integer.toString(columns) + ",";

        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                stringMatrix += (char) matrix[row][column];
            }
        }

        return stringMatrix;
    }
    

    public static void main(String[] args) {
        InfectionProbability ip = new InfectionProbability(.855, .206, .53);
        Matrix matrix = new Matrix("4,4,0000735000000000", ip);
        System.out.println(matrix.getStringMatrix());
        matrix.updateMatrix();
        System.out.println(matrix.getStringMatrix());
    }
}



