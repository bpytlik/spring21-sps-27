enum Cell {
    /*
    0 --> dead
    1 --> alive
    2 --> alive with mask
    3 --> alive with vaccine
    4 --> alive with mask and vaccine
    5 --> infected
    6 --> infected with mask
    7 --> infected with vaccine
    8 --> infected with mask and vaccine
    */

    DEAD ((byte) '0'),
    ALIVE ((byte) '1');
  
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

    public Matrix(String stringMatrix) {
        stringToMatrix(stringMatrix);
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
        int neighbors = getNumberOfNeighbors(row, column, tempMatrix);

        byte currentCellCode = tempMatrix[row][column];

        if (currentCellCode == Cell.ALIVE.getCode()) {
            if (neighbors < 2 || neighbors > 3) {
                this.matrix[row][column] = Cell.DEAD.getCode();
            }
        }
        else if (currentCellCode == Cell.DEAD.getCode()) {
            if (neighbors == 3) {
                this.matrix[row][column] = Cell.ALIVE.getCode();
            }
        }

    }

    private int getNumberOfNeighbors(int row, int column, byte[][] tempMatrix) {
        int neighbors = 0;

        neighbors += this.hasAliveNeighbor(row-1,column-1,tempMatrix) ? 1 : 0;
        neighbors += this.hasAliveNeighbor(row-1,column,tempMatrix) ? 1 : 0;
        neighbors += this.hasAliveNeighbor(row-1,column+1,tempMatrix) ? 1 : 0;
        neighbors += this.hasAliveNeighbor(row,column+1,tempMatrix) ? 1 : 0;
        neighbors += this.hasAliveNeighbor(row+1,column+1,tempMatrix) ? 1 : 0;
        neighbors += this.hasAliveNeighbor(row+1,column,tempMatrix) ? 1 : 0;
        neighbors += this.hasAliveNeighbor(row+1,column-1,tempMatrix) ? 1 : 0;
        neighbors += this.hasAliveNeighbor(row,column-1,tempMatrix) ? 1 : 0;

        return neighbors;
    }

    private boolean hasAliveNeighbor(int row, int column, byte[][] tempMatrix) {
        int rows = tempMatrix.length,
            columns = tempMatrix[0].length;

        boolean boundaryCheck = row >= 0 && row < rows && column >= 0 && column < columns;
        return boundaryCheck && tempMatrix[row][column] != Cell.DEAD.getCode(); 
    }


    private void stringToMatrix(String stringMatrix) {

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
    

    public static void main(String[] args) {
        Matrix matrix = new Matrix("4,4,1001101010001010");
        matrix.updateMatrix();
    }
}



