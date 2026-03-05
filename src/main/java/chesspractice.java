/* chess Requirement :
 8*8 board -> standard board
 2 players
 each player has 16 pieces -> Rook, Knight, Bishop, Queen, King, Pawn


Game -> Board -> cell -> piece -> Move

enum GameStatus{}

enum PieceType {
ROOK, Knight, Bishop, Queen, King, Pawn
}

enum Colour {
WHITE,
BLACK
}

Game
-------
- Board board = Board.getInstance()
- Player player1
- Player player2
- currentTurn = Colour.WHITE
-----------------
+ start()
+ makeMove()
+ changeCurrentTurn()


Board
------------------
- Cell cells[][]
- Board instance
+ intialize()
+ getInstance()


Cell
----------
- int row
- int col
- Piece piece
+ setPiece(Piece piece)
+ getPiece()
+ getRow()
+ getCol()

pieceFactory
----------------
+ createPiece(PieceType type , Colour colour)

interface moveStrategy
+ isValidMove(Board board, PieceType type, Move move)

RookStrategy implements moveStrategy
KnightStrategy implements moveStrategy
BishopStrategy implements moveStrategy
QueenStrategy implements moveStrategy
KingStrategy implements moveStrategy
PawnStrategy implements moveStrategy


abstract class Piece
--------------------------
- Colour colour
- PieceType type
- MoveStrategy moveStrategy
+ getColour()
+ canMove(Board board, Move move) { + moveStrategy.isValidMove(Board board, PieceType type, Move move) }

class Rook extends Piece
class Knight extends Piece
class Bishop extends Piece
class Queen extends Piece
class King extends Piece
class Pawn extends Piece

Move
------
- int fr
- int fc
- int tr
- int tc
-------
*




 */

/*
enum Colour {WHITE , BLACK}
enum pieceType =
{
Rook, knight, bishop, queen, king, pawn
}
enum GameStatus {
    ACTIVE,
    CHECK,
    STALEMATE,
    CHECKMATE
}

Player
-------
Colour colour


Game
---------------
-Board board
- Player player1
- Player player2
- Colour currentPlayer = Colour.WHITE
- MoveValidator moveValidator = new MoveValidator()

+ Start()
+ makeMove(Move move) {
    if(moveValidator.isValid(board, move)){
        get the start cell
        get the end cell
        place the piece in the end cell
        empty the start cell

        changeCurrentTurn()
    }
}
+ changeCurrentTurn()

Board
---------------
- Cell cell[][]
- instance
+ intialize()
+ getInstance()
+ getCell()

MoveValidator
----------------
isValid(Board board, Move move, Colour currentPlayer)

Cell
-------------
- int row
- int col
- Piece piece
+ getPiece()
+ setPiece()

PieceCreationFactory
+ createPiece(PieceType type, Colour colour) case 'PAWN' : return new Pawn(colour, new PawnStrategy())

interface PieceMovementStrategy
------------------------------------
+ isValidMove(Board board, Move move, Colour colour)

RookStrategy implements PieceMovementStrategy
KnightStrategy implements PieceMovementStrategy
BishopStrategy implements PieceMovementStrategy
KingStrategy implements PieceMovementStrategy
QueenStrategy implements PieceMovementStrategy
PawnStrategy implements PieceMovementStrategy

abstract Piece
----------------
- Colour colour
- PieceMovementStrategy pieceMovementStrategy
- PieceType type
+ canMove(Board board, Move move) {
pieceMovementStrategy.isValidMove(board, move, colour)
}

Rook extends Piece
Knight extends Piece
Bishop extends Piece
King extends Piece
Queen extends Piece
Pawn extends Piece



Move
--------
- int fr
- int fc
- int tr
- int tc

Driver code
----------------
player1 = new Player(Colour.WHITE)
player2 = new Player(Colour.BLACK)
Game = new Game(player1, player2)
game.start()
Move move = new Move(5, 0, 6, 0)
game.makeMove(move)



 */
