package pt.isel.ls.database.memory

import pt.isel.ls.database.AppDatabase
import pt.isel.ls.domain.Board
import pt.isel.ls.domain.Card
import pt.isel.ls.domain.SimpleBoard
import pt.isel.ls.domain.SimpleList
import pt.isel.ls.domain.User
import pt.isel.ls.domain.UserBoard
import pt.isel.ls.domain.toSimpleBoard
import java.sql.Date

class TasksDataMem : AppDatabase {

    private var userId: Int = 0
    private var boardId: Int = 0
    private var listId: Int = 0
    private var cardId: Int = 0
    private var userBoardId: Int = 0

    val users: MutableMap<Int, User> = mutableMapOf()
    val boards: MutableMap<Int, Board> = mutableMapOf()
    val userBoard: MutableMap<Int, UserBoard> = mutableMapOf()
    val taskLists: MutableMap<Int, SimpleList> = mutableMapOf()
    val cards: MutableMap<Int, Card> = mutableMapOf()

    /**
     * Creates a new user
     *
     * @param name user's name
     * @param email user's email, must be unique
     *
     * @throws EmailAlreadyExistsException if the email already exists
     * @return the created User()
     */

    override fun createUser(token: String, name: String, email: String, password: String): Int {
        val id = userId.also { userId += 1 }
        users[id] = User(id, name, email, token, password)
        return id
    }

    override fun loginUser(email: String): User {
        val u = users.values.firstOrNull { it.email == email } ?: throw UserNotFoundException
        return User(u.id, u.name, u.email, u.token, u.password)
    }

    /**
     * Get user details
     *
     * @param uid user's unique identifier
     *
     * @throws UserNotFoundException if the User was not found
     * @return the User() details
     */
    override fun getUserDetails(uid: Int): User = users[uid] ?: throw UserNotFoundException

    /**
     * Get the user boards
     *
     * @param bid board's unique identifier
     *
     * @return the list of users from that specific board
     */
    override fun getUsersFromBoard(bid: Int, skip: Int, limit: Int): List<User> =
        userBoard.values
            .filter { it.bId == bid }
            .drop(skip)
            .take(limit)
            .map { getUserDetails(it.uId) }

    override fun checkEmailAlreadyExists(email: String) = users.values.any { it.email == email }

    override fun getAllAvailableUser(): List<User> = users.values.map { getUserDetails(it.id) }

    /**
     * Creates a new board
     *
     * @param uid user's unique identifier
     * @param name board's unique name
     * @param description board's descritpion
     *
     * @throws UserNotFoundException if the user was not found
     * @throws BoardNameAlreadyExistsException if the board name already exists
     * @return the created Board()
     */
    override fun createBoard(uid: Int, name: String, description: String): Int {
        val id = boardId.also { boardId += 1 }
        boards[id] = Board(id, name, description, emptyList())
        val ubID = userBoardId.also { userBoardId += 1 }
        userBoard[ubID] = UserBoard(uid, id)
        return id
    }

    /**
     * Get user details
     *
     * @param bid board's unique identifier
     * @throws BoardNotFoundException if the board was not found
     * @return the Board() details
     */
    override fun getBoardDetails(bid: Int): SimpleBoard = boards[bid]?.toSimpleBoard() ?: throw BoardNotFoundException

    /**
     * Add a User to a Board
     *
     * @param uid user's unique identifier
     * @param bid board's unique identifier
     *
     */
    override fun addUserToBoard(uid: Int, bid: Int) {
        val id = userBoardId.also { userBoardId += 1 }
        userBoard[id] = UserBoard(uid, bid)
    }

    /**
     * Get the boards from a User
     *
     * @param uid user's unique identifier
     *
     * @return list of boards from that User
     */
    override fun getBoardsFromUser(uid: Int, skip: Int, limit: Int): List<SimpleBoard> =
        userBoard.values
            .filter { it.uId == uid }
            .drop(skip)
            .take(limit)
            .map { board ->
                getBoardDetails(board.bId)
            }

    override fun checkUserAlreadyExistsInBoard(uid: Int, bid: Int): Boolean {
        return userBoard.values.any { it.bId == bid && it.uId == uid }
    }

    override fun checkUserTokenExistsInBoard(token: String, bid: Int): Boolean {
        val uid = tokenToId(token)
        return checkUserAlreadyExistsInBoard(uid, bid)
    }

    override fun checkBoardExists(bid: Int): Boolean {
        return boards.values.any { it.id == bid }
    }

    override fun checkBoardNameAlreadyExists(name: String): Boolean {
        return boards.values.any { it.name == name }
    }

    override fun searchBoardsFromUser(uid: Int, skip: Int, limit: Int, searchQuery: String) =
        userBoard.values
            .filter { it.uId == uid && boards.values.any { b -> b.name.lowercase().contains(searchQuery.lowercase()) } }
            .drop(skip)
            .take(limit)
            .map { board ->
                getBoardDetails(board.bId)
            }

    /**
     * Creates a new List
     *
     * @param bid board's unique identifier
     * @param name list's name
     *
     * @return the created TaskList()
     */
    override fun createList(bid: Int, name: String): Int {
        val id = listId.also { listId += 1 }
        taskLists[id] = SimpleList(id, bid, name)

        return id
    }

    /**
     * Get the lists from a Board
     *
     * @param bid board's unique identifier
     *
     * @return the list of TaskList from that Board
     */
    override fun getListsFromBoard(bid: Int, skip: Int, limit: Int): List<SimpleList> {
        return taskLists.values
            .filter { it.bid == bid }
            .drop(skip)
            .take(limit)
            .map {
                SimpleList(it.id, bid, it.name)
            }
    }

    /**
     * Get a TaskList details
     *
     * @param lid taskList's unique identifier
     *
     * @throws ListNotFoundException if the list was not found
     * @return the TaskList() details
     */
    override fun getListDetails(lid: Int): SimpleList = taskLists[lid] ?: throw ListNotFoundException

    override fun deleteList(lid: Int) {
        taskLists.values.filter { it.id != lid }
    }

    override fun checkListAlreadyExistsInBoard(name: String, bid: Int): Boolean {
        return taskLists.values.any { it.name == name && it.bid == bid }
    }

    /**
     * Creates a new Card
     *
     * @param lid taskList's unique identifier
     *
     * @return the created Card()
     */
    override fun createCard(lid: Int, name: String, description: String, initDate: Date, dueDate: Date): Int {
        if (cards.values.filter { it.lid == lid }.any { it.name == name }) throw CardNameAlreadyExistsException
        val id = cardId.also { cardId += 1 }
        val newCard = Card(id, getListDetails(lid).bid, lid, name, description, initDate, dueDate)

        cards[id] = newCard
        return newCard.id
    }

    /**
     * Get the cards from a TaskList
     *
     * @param lid taskList's unique identifier
     *
     * @throws ListNotFoundException if the list was not found
     * @return list of cards from a TaskList
     */

    override fun getCardsFromList(lid: Int, bid: Int, skip: Int, limit: Int): List<Card> {
        return cards.values
            .filter { it.lid == lid }
            .drop(skip)
            .take(limit)
            .map {
                getCardDetails(it.id)
            }
    }

    /**
     * Get card details
     *
     * @param cid card's unique identifier
     *
     * @throws CardNotFoundException if the card was not found
     * @return the Card() details
     */
    override fun getCardDetails(cid: Int): Card = cards[cid] ?: throw CardNotFoundException

    /**
     * Move a card to another TaskList
     *
     * @param cid card's unique identifier
     * @param lid taskList's unique identifier
     *
     * @throws ListNotFoundException if the list was not found
     */
    override fun moveCard(cid: Int, lid: Int, cix: Int) {
        val c = getCardDetails(cid)
        if (!taskLists.values.any { it.id == lid }) throw ListNotFoundException
        cards[cid] = c.copy(lid = lid)
    }

    override fun deleteCard(cid: Int) {
        cards.values.filter { it.id != cid }
    }

    /**
     * Get the User id given the bearer token
     *
     * @param bToken user's bearer token
     *
     * @throws UserNotFoundException if the user was not found
     * @return user unique identifier
     */
    override fun tokenToId(bToken: String): Int {
        return users.values.firstOrNull { it.token == bToken }?.id ?: throw UserNotFoundException
    }
}
