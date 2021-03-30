var rowOpening = "<tr>";
var rowClosure = "</tr>";
var cellZoom = "style = \"width: 3em; height: 3em\"";
var alive = "<td class = \"matrixCell\"><img src = \"../images/alive normal cell.png\" " + cellZoom+"class = \"table-image\"></td>";
var death = "<td class = \"matrixCell\"><img src = \"../images/dead cell.png\" "+cellZoom+"class = \"table-image\"></td>";
var rows = 0;
var columns = 0;
var matrix = "";


function updateMatrix(){

    rows = document.getElementById("matrixRows").value;
    columns = document.getElementById("matrixColumns").value;
    matrix = "";
    for(let row = 0; row < rows; row++){
        matrix += rowOpening;
        for(let column = 0; column < columns; column++){
            matrix += death;
        }
        matrix += rowClosure;
    }
    document.getElementById("matrixTable").innerHTML = matrix;
}

function updateZoom(){
    let newZoomLevel = document.getElementById("matrixZoom").value;
    cellZoom = "style = \"width: "+newZoomLevel+"em; height: "+newZoomLevel+"em\"";
    alive = "<td class = \"matrixCell\"><img src = \"../images/alive normal cell.png\" " + cellZoom+"class = \"table-image\"></td>";
    death = "<td class = \"matrixCell\"><img src = \"../images/dead cell.png\" "+cellZoom+"class = \"table-image\"></td>";
    updateMatrix();
}