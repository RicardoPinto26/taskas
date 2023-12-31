import getHomeHandler from "./handlers/getHomeHandler.js";
import getNotFoundHandler from "./handlers/getNotFoundHandler.js";

import getUsersFromBoardHandler from "./handlers/boards/getUsersFromBoardHandler.js";
import userSignupHandler from "./handlers/users/userSignupHandler.js";
import loginUserHandler from "./handlers/users/userLoginHandler.js";
import userDetailsHandler from "./handlers/users/userDetailsHandler.js"

import searchBoardHandler from "./handlers/boards/searchBoardHandler.js";
import searchBoardResultsHandler from "./handlers/boards/searchBoardResultsHandler.js";
import getBoardsHandler from "./handlers/boards/getBoardsHandler.js";
import createBoardHandler from "./handlers/boards/createBoardHandler.js";
import getBoardDetailsHandler from "./handlers/boards/getBoardDetailsHandler.js";

import createListHandler from "./handlers/lists/createListHandler.js";
import listDetailsHandler from "./handlers/lists/listDetailsHandler.js";

import createCardHandler from "./handlers/cards/createCardHandler.js";
import cardDetailsHandler from "./handlers/cards/cardDetailsHandler.js";

export const handlers = {
    getHomeHandler,
    getNotFoundHandler,
    userSignupHandler,
    loginUserHandler,
    userDetailsHandler,
    searchBoardResultsHandler,
    createBoardHandler,
    getBoardsHandler,
    getBoardDetailsHandler,
    searchBoardHandler,
    getUsersFromBoardHandler,
    createListHandler,
    listDetailsHandler,
    createCardHandler,
    cardDetailsHandler,
}

export default handlers