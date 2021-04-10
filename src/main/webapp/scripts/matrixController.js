var rowOpening = "<tr>";
var rowClosure = "</tr>";
var cellZoom = "style = \"width: 3em; height: 3em\"";
var zoomLevel = 3;
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

function generateRandomMatrix(continueWithGameLogic){
    let oldMatrix = matrix;
    matrix =[];
    console.log("--------------------------");
    for(let row = 0; row < rows; row++){
        let newMatrixRow = [];
        for(let column = 0 ; column < columns ; column++ ){
            let nextCell =  Math.floor( Math.random() * 8 )
            console.log(row.toString() + " " + column.toString() + " " + nextCell.toString());
            newMatrixRow.push(nextCell)
            document.getElementById(row.toString() + column.toString()).src = cellDictionary[ nextCell ];
        }
        matrix.push(newMatrixRow);
    }
    console.log("--------------------------");
    if ( document.getElementById("matrixZoom").value != zoomLevel){
        updateZoom(true);
    }

    if ((document.getElementById("matrixRows").value != rows) || (document.getElementById("matrixColumns").value != columns)){
        updateMatrix(true);
    }


    if(continueWithGameLogic)
        gameLogic();
}

function gameLogic(){
    if(playing){
        timeController = setTimeout(() => 
            {
                if(playing){
                    generateRandomMatrix(true);
                }
            }, playSpeed);
    }
}

function loadPreviousStateHelper(){
    for(let row = 0; row < rows ; row ++ ){
        for(let column = 0; column < columns ; column++){
            document.getElementById(row.toString() + column.toString()).src = cellDictionary[matrix[row][column]];
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
    console.log(cellWithMask);
    console.log(cellWithVaccine);
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
                matrixToTable += death + "id = \"" + row.toString() + column.toString() + "\" " + updateFunction + cellClosure;
            }
            matrix.push(tempRow);
            matrixToTable += rowClosure;
        }
        document.getElementById("matrixTable").innerHTML = matrixToTable;
    }
}


function updateMatrixCell(object){
    let row = parseInt( object.id[0] );
    let column = parseInt( object.id[1] );
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
    if((!playing && !update) || (playing && update)){
        zoomLevel = document.getElementById("matrixZoom").value;
        document.getElementById('matrixZoomLabel').innerText = "Zoom: " + (zoomLevel - 2).toString();
        cellZoom = "style = \"width: "+zoomLevel+"em; height: "+zoomLevel+"em\"";
        alive = "<td class = \"matrixCell\"><img src = \"../images/alive normal cell.png\" " + cellZoom+"class = \"table-image\" ";
        death = "<td class = \"matrixCell\"><img src = \"../images/dead cell.png\" "+cellZoom+"class = \"table-image\" ";

        for(let row = 0; row < rows; row++){
            for( let column = 0; column < columns; column++){

                let cell = document.getElementById(row.toString() + column.toString());

                cell.style['width'] = zoomLevel.toString() + "em";
                cell.style['height'] = zoomLevel.toString() + "em";

            }
        }
    }
}