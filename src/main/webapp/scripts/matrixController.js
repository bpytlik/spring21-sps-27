var rowOpening = "<tr>";
var rowClosure = "</tr>";
var cellZoom = "style = \"width: 3em; height: 3em\"";
var zoomLevel = 0.5;
var alive = "<td class = \"matrixCell\"><img src = \"../images/alive normal cell.png\" " + cellZoom+"class = \"table-image\" ";
var death = "<td class = \"matrixCell\"><img src = \"../images/dead cell.png\" "+cellZoom+"class = \"table-image\" ";
var cellClosure = "></td>";
var rows = 0;
var columns = 0;
var selectedCellType = "deadCell"
var matrix = [[0,0,0],[0,0,0],[0,0,0]];
var playSpeed = 1000;
var cellWithMask = false;
var cellWithVaccine = false;
var playing = false; 
var timeController = null;
var cellDictionary ={
    0: "../images/dead cell.png",
    1: "../images/alive normal cell.png",
    2: "../images/alive mask.png",
    3: "../images/alive vaccine.png",
    4: "../images/alive mask vaccine.png",
    5: "../images/Infected Cell.png",
    6: "../images/infected mask.png",
    7: "../images/infected vaccine.png",
    8: "../images/infected mask vaccine.png"
};

var stateIndex = 0;

function generateRandomMatrix(){
    let oldMatrix = matrix;
    matrix =[];
    for(let row = 0; row < rows; row++){
        let newMatrixRow = [];
        for(let column = 0 ; column < columns ; column++ ){
            let nextAlive =  Math.floor( Math.random() * 2 );
            let nextCell =  (nextAlive == 1) ? Math.floor( Math.random() * 8 ) : 0;
            newMatrixRow.push(nextCell)
            document.getElementById(row.toString() + "," + column.toString()).src = cellDictionary[ nextCell ];
        }
        matrix.push(newMatrixRow);
    }
    if ( document.getElementById("matrixZoom").value != zoomLevel){
        updateZoom(true);
    }

    if ((document.getElementById("matrixRows").value != rows) || (document.getElementById("matrixColumns").value != columns)){
        updateMatrix(true);
    }
}

async function getNextIteration(continueWithGameLogic) {

    const body = {
        stringMatrix: matrixToString(matrix, rows, columns),
        IP_NO_PROTECTION: document.getElementById("IP_NO_PROTECTION").value,
        IP_WITH_MASK: document.getElementById("IP_WITH_MASK").value,
        IP_WITH_VACCINE: document.getElementById("IP_WITH_VACCINE").value
    };
    
    const response = await fetch("/updateMatrix", {
        method: "post",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(body)
    });


    const updatedStringMatrix = await response.json();
    
    matrix = stringToMatrix(updatedStringMatrix);
    
    if ( document.getElementById("matrixZoom").value != zoomLevel){
        updateZoom(true);
    }

    if ((document.getElementById("matrixRows").value != rows) || (document.getElementById("matrixColumns").value != columns)){
        updateMatrix(true);
    }

    
    if(continueWithGameLogic) {
        gameLogic();
    }
} 


function gameLogic(){
    if(playing){
        timeController = setTimeout(() => 
            {
                if(playing){
                    getNextIteration(true);
                }
            }, playSpeed);
    }
}

function loadPreviousStateHelper(){
    for(let row = 0; row < rows ; row ++ ){
        for(let column = 0; column < columns ; column++){
            document.getElementById(row.toString() + "," + column.toString()).src = cellDictionary[matrix[row][column]];
        }
    }
}

function startTheGame(){
    if(!playing){
        playing = true;
        gameLogic();
    }
}

function stopTheGame(){
    playing = false;
    clearInterval(timeController);

}

function updateCellModifier(target){
    if(target == "mask")
        cellWithMask = !cellWithMask;
    else
        cellWithVaccine = !cellWithVaccine;
}

function updateMatrix(update){
    if((!playing && !update) || (playing && update)){
        matrix = []
        rows = document.getElementById("matrixRows").value;
        columns = document.getElementById("matrixColumns").value;
        let matrixToTable = "";
        let updateFunction = "onclick = \"updateMatrixCell(this)\"";
        for(let row = 0; row < rows; row++){
            let tempRow = []
            matrixToTable += rowOpening;
            for(let column = 0; column < columns; column++){
                tempRow.push(0)
                matrixToTable += death + "id = \"" + row.toString() + "," + column.toString() + "\" " + updateFunction + cellClosure;
            }
            matrix.push(tempRow);
            matrixToTable += rowClosure;
        }
        document.getElementById("matrixTable").innerHTML = matrixToTable;
    }
}

function updateMatrixAfterCallingBackEnd(){
    for(let row = 0; row < rows; row++){
        for (let column = 0; column < columns; column++){
            document.getElementById(row.toString() + "," + column.toString()).src = cellDictionary[matrix[row][column]];
        }
    }
}


function updateMatrixCell(object){
    let point = object.id.split(",");
    let row = parseInt( point[0] );
    let column = parseInt( point[1] );
    switch( selectedCellType ){
        case "deadCell":
            object.src = "../images/dead cell.png";
            matrix[row][column] = 0;
            break;
        
        case "aliveCell":
            if (!cellWithMask && !cellWithVaccine){
                object.src = "../images/alive normal cell.png";
                matrix[row][column] = 1;
            }else if (cellWithMask && cellWithVaccine){
                object.src = "../images/alive mask vaccine.png";
                matrix[row][column] = 4;
            }else if (cellWithMask){
                object.src = "../images/alive mask.png";
                matrix[row][column] = 2;
            }else{
                object.src = "../images/alive vaccine.png";
                matrix[row][column] = 3;
            }
            
            break;
        
        case "infectedCell":
            if (!cellWithMask && !cellWithVaccine){
                object.src = "../images/Infected Cell.png";
                matrix[row][column] = 5;
            }else if (cellWithMask && cellWithVaccine){
                object.src = "../images/infected mask vaccine.png";
                matrix[row][column] = 8;
            }else if (cellWithMask){
                object.src = "../images/infected mask.png";
                matrix[row][column] = 6;
            }else{
                object.src = "../images/infected vaccine.png";
                matrix[row][column] = 7;
            }
            break;
    }
}

function updateMaskProbability(object){
    document.getElementById("maskProbabilityLabel").innerText = "Probability to get infected with mask: " + object.value.toString();
}

function updateSelectedCellType(object){
    selectedCellType = object.id;
    switch(selectedCellType)
    {
        case "deadCell":
            object.src = "../images/dead selected.png";
            document.getElementById("aliveCell").src = "../images/alive normal cell.png";
            document.getElementById("infectedCell").src = "../images/Infected Cell.png";
            break;
        
        case "aliveCell":
            object.src = "../images/live selected.png";
            document.getElementById("deadCell").src = "../images/dead cell.png";
            document.getElementById("infectedCell").src = "../images/Infected Cell.png";
            break;
        
        case "infectedCell":
            object.src = "../images/infected selected.png";
            document.getElementById("deadCell").src = "../images/dead cell.png";
            document.getElementById("aliveCell").src = "../images/alive normal cell.png";
            break;
    }
}

function updateSpeed(object){
    playSpeed = 1000 - ( (parseInt(object.value) - 1) * 100 );
    document.getElementById("matrixSpeedLabel").innerText = "Speed: " + object.value.toString();
}

function updateVaccineProbability(object){
    document.getElementById("matrixSpeedLabel").innerText = "Probability to get infected with vaccine: " + object.value.toString();
}

function updateZoom(update){
    let level = 0.5;
    if((!playing && !update) || (playing && update)){
        zoomLevel = document.getElementById("matrixZoom").value * level;
        document.getElementById('matrixZoomLabel').innerText = "Zoom: " + (zoomLevel).toString();
        cellZoom = "style = \"width: "+zoomLevel+"em; height: "+zoomLevel+"em\"";
        alive = "<td class = \"matrixCell\"><img src = \"../images/alive normal cell.png\" " + cellZoom+"class = \"table-image\" ";
        death = "<td class = \"matrixCell\"><img src = \"../images/dead cell.png\" "+cellZoom+"class = \"table-image\" ";
        
        
        for(let row = 0; row < rows; row++){
            for( let column = 0; column < columns; column++){

                let cell = document.getElementById(row.toString() + "," + column.toString());

                cell.style['width'] = zoomLevel.toString() + "em";
                cell.style['height'] = zoomLevel.toString() + "em";

            }
        }
    }
}

function matrixToString(matrix, n, m) {
    let stringMatrix = n + "," + m + ",";

    for(let i = 0; i < n; i++){
        for(let j = 0; j < m; j++){
            stringMatrix += matrix[i][j];
        }
    }

    return stringMatrix;
}

function stringToMatrix(stringMatrix) { // TODO: implement validation
    const commaIndexes = findCommaIndexes(stringMatrix);

    const rows = parseInt(stringMatrix.substr(0, commaIndexes[0] + 1));
    const columns = parseInt(stringMatrix.substr(commaIndexes[0] + 1, commaIndexes[1] - commaIndexes[0] - 1));

    var currentCell = commaIndexes[1] + 1;

    var matrix = [];

    for (let row = 0; row < rows; row++) {
        var currentRow = [];
        for (let column = 0; column < columns; column++) {
            const nextCell = parseInt(stringMatrix.charAt(currentCell++))
            currentRow.push(nextCell);
            document.getElementById(row.toString() + "," + column.toString()).src = cellDictionary[ nextCell ];
        }
        matrix.push(currentRow);
    }
    return matrix;

}

function findCommaIndexes(stringMatrix) { 
    var commaIndexes = [0,0];

    var firstCommaFound = false;

    for (let i=0; i<stringMatrix.length; i++) {
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
