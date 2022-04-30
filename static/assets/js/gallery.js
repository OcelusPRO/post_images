const filesNames = JSON.parse(document.getElementById("imgs").innerText);
const section = document.getElementById("sectionImages");
function showElements(files) {
    function makeRaw() {
        const rowTemplate = document.createElement("div");
        rowTemplate.classList.add("row");
        rowTemplate.style.marginRight = "1%";
        rowTemplate.style.marginLeft = "1%";
        return rowTemplate;
    }
    function makeColumn() {
        const colTemplate = document.createElement("div");
        colTemplate.classList.add("col");
        colTemplate.classList.add("d-flex");
        colTemplate.classList.add("d-xxl-flex");
        colTemplate.classList.add("flex-column");
        colTemplate.classList.add("justify-content-xxl-center");
        colTemplate.classList.add("align-items-xxl-center");
        colTemplate.style.padding = "1%";
        return colTemplate;
    }
    function makeInColumn(imgName) {
        const linkElement = document.createElement("a");
        linkElement.href = "/i/" + imgName;

        const inColTemplate = document.createElement("div");
        inColTemplate.classList.add("d-flex");
        inColTemplate.classList.add("flex-column");
        inColTemplate.classList.add("justify-content-xxl-center");
        inColTemplate.classList.add("align-items-xxl-center");
        inColTemplate.style.color = "rgb(246,246,246)";
        inColTemplate.style.background = "#18181B";
        inColTemplate.style.borderRadius = "18px";

        const img = document.createElement("img");
        img.src = `/${imgName}`;
        img.style.maxWidth = "100%";
        img.style.maxHeight = "100%";
        img.style.width = "100%";
        img.loading = "lazy";
        inColTemplate.appendChild(img);

        const h2 = document.createElement("h2");
        h2.style.color = "rgb(246,246,246)";
        h2.style.maxHeight = "100%";
        h2.innerHTML = imgName;
        inColTemplate.appendChild(h2);

        linkElement.appendChild(inColTemplate);

        return linkElement;
    }

    section.innerHTML = "";
    const mapElements = [];
    let currentTreatment = [];

    files.forEach( e => {
        currentTreatment.push(e);
        if (currentTreatment.length >= 3) {
            mapElements.push(currentTreatment);
            currentTreatment = [];
        }
    });
    mapElements.push(currentTreatment);

    mapElements.forEach( e => {
        const row = makeRaw();
        e.forEach( e => {
            const col = makeColumn();
            const inCol = makeInColumn(e);
            col.appendChild(inCol);
            row.appendChild(col);
        });
        section.appendChild(row);
    });
}
showElements(filesNames);