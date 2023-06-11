import createCardData from "../../data/cards/createCardData.js";
import createCardView from "../../views/cards/createCardView.js";

function createCardHandler(mainContent, listID) { //dd-mm-yyyy

    const today = new Date().toJSON().slice(0, 10)

    const createCardFunction = createCardData(listID, today)
    const view = createCardView(createCardFunction)

    mainContent.replaceChildren(view)
    document.getElementById("idInitDate").value = today
}

export default createCardHandler