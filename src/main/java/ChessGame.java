/*
Chess Game requirement:
1. The chess game should follow the standard rules of chess.
2. The game should support two players, each controlling their own set of pieces.
3. The game board should be represented as an 8x8 grid, with alternating black and white squares.
4. Each player should have 16 pieces: 1 king, 1 queen, 2 rooks, 2 bishops, 2 knights, and 8 pawns.
5. The game should validate legal moves for each piece and prevent illegal moves.
6. The game should detect checkmate and stalemate conditions.
7. The game should handle player turns and allow players to make moves alternately.
8. The game should provide a user interface for players to interact with the game.
 */

/*
Game
  ↓
Board
  ↓
Cell
  ↓
Piece
*/

/*

GameStatus
__________
ACTIVE
CHECK
CHECKMATE

Color
----------
WHITE
BLACK


        Game
----------------------------
- Board board
- Colour currentTurn
- player player1
- Player player2
- MoveValidator moveValidator;
- GameStatus gameStatus
------------------------
+ start()
+ makeMove(move)
- Colour changeTurn()
- void updateGameStatus()
- isCheck(Colour colour)
- Cell findKing(Colour colour)
- boolean isSquareUnderAttack(Cell targetCell, Colour attackedColour)
- boolean hasLegalMove(currentTurn)
- boolean causesCheck(Move move, Colour colour)



        Board <<Singleton>>
----------------------------------
- Cell[][] cells(8*8)
------------------
+ initialize()
+ getCell(X, Y)


    Cell
------------------
int X
int Y
Piece piece

        Piece (abstract) <<PieceFactory>>
----------------------------------
- colour
------------------------
+ abstract boolean isValidMove(Board board, Cell start, Cell end)  <<Strategy>>
  /      |      |    \
 /     	 |      |     \
King    Queen  Rook    Pawn ...


makeMove()
↓
updateGameStatus()
↓
isCheck()
↓
hasAnyLegalMove()
↓
CHECK / CHECKMATE / STALEMATE


*/

/*
Game
 ├── Board (Singleton)
 │      └── Cell[][]
 │
 ├── MoveValidator
 │
 ├── PieceFactory
 │
 └── Pieces
        └── Strategy
              ├── RookStrategy
              ├── KnightStrategy
              ├── BishopStrategy
              ├── QueenStrategy
              ├── KingStrategy
              └── PawnStrategy
 */
/*
Time Complexity of Check Detection

8x8 board
for each piece
try moves

Worst case:
O(64 × 64)
≈ O(4096)

Which is perfectly fine for chess.
 */




enum GameStatus {
    ACTIVE,
    CHECK,
    STALEMATE,
    CHECKMATE,
}

enum Colour {
    WHITE,
    BLACK,
}

enum PieceType {
    ROOK,
    KNIGHT,
    BISHOP,
    QUEEN,
    KING,
    PAWN
}
class MoveValidator{
    public void isValid(Board board, Move move, Colour colour) {

    }
}
class Player {
    Colour colour;

    Player(Colour colour) {
        this.colour = colour;
    }
}

/* ---------- Game ------------- */
class Game {
    private final Board board;
    private Colour currentTurn;
    MoveValidator moveValidator;
    private GameStatus status;
    Player player1;
    Player player2;
    Game(Player player1, Player player2) {
        this.board = Board.getInstance();
        currentTurn = Colour.WHITE;
        moveValidator = new MoveValidator();
        this.player1 = player1;
        this.player2 = player2;
        status = GameStatus.ACTIVE;
    }

    void start() {;
        board.initialize();
    }

    void makeMove(Move move) {
        Cell start = board.getCell(move.fr, move.fc);
        Cell end = board.getCell(move.tr, move.tc);

        Piece piece = start.getPiece();

        if (piece == null) {
            System.out.println("No piece at start");
            return;
        }

        if (piece.getColour() != currentTurn) {
            System.out.println("Not your turn");
            return;
        }

        if (end.getPiece() != null && end.getPiece().getColour() == piece.getColour()) {
            System.out.println("Cannot capture own piece");
            return;
        }

        if (!piece.canMove(board, move)) {
            System.out.println("Invalid move");
            return;
        }
        if (causesCheck(move, currentTurn)) {
            System.out.println("Move leaves king in check");
            return;
        }

        end.setPiece(piece);
        start.setPiece(null);
        System.out.println("Move successful");

        currentTurn = changeTurn(currentTurn);

        updateGameStatus();
    }

    private Colour changeTurn(Colour colour) {
         return colour == Colour.WHITE ? Colour.BLACK : Colour.WHITE;

    }

    private void updateGameStatus() {
        if(isCheck(currentTurn)) {
            if(!hasAnyLegalMove(currentTurn)) {
                status = GameStatus.CHECKMATE;
                System.out.println("CHECKMATE! " + currentTurn + " loses");
            }
            else {
                status = GameStatus.CHECK;
                System.out.println(currentTurn + " is in CHECK");
            }
        }
        else {
            if(!hasAnyLegalMove(currentTurn)) {
                status = GameStatus.STALEMATE;
                System.out.println("STALEMATE");
            }
            else {
                status = GameStatus.ACTIVE;

            }
        }

}

    private boolean isCheck(Colour colour) {

        Cell kingCell = findKing(colour);
        if (kingCell == null) return false;

        Colour opponent = changeTurn(colour);

        return isSquareUnderAttack(kingCell, opponent);
    }

    private Cell findKing(Colour colour) {
        for(int i = 0; i <8; i++){
            for(int j = 0; j <8; j++){
                Cell cell = board.getCell(i, j);

                if(cell.isOccupied()) {
                    Piece piece = cell.getPiece();

                    if(piece.type == PieceType.KING && piece.getColour() == colour) {
                        return cell;
                    }
                }
            }
        }

        return null;
    }

    private boolean isSquareUnderAttack(Cell targetCell, Colour attackerColour) {
        for(int r = 0; r <8; r++){
            for(int c = 0; c <8; c++) {
                Cell cell = board.getCell(r, c);

                if(cell.isOccupied() && cell.getPiece().getColour() == attackerColour) {
                    Move move = new Move(r, c, targetCell.getRow(), targetCell.getCol());
                    if(cell.getPiece().canMove(board, move)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean hasAnyLegalMove(Colour colour) {
        for(int r = 0; r <8; r++){
            for(int c = 0; c <8; c++) {
                Cell start = board.getCell(r, c);

                if(start.isOccupied() && start.getPiece().getColour() == colour) {
                    for (int tr = 0; tr < 8; tr++) {
                        for (int tc = 0; tc < 8; tc++) {
                            Move move = new Move(r, c, tr, tc);
                            if(start.getPiece().canMove(board, move)) {
                                if (!causesCheck(move, colour)) {
                                    return true;
                                }
                            }
                        }

                    }
                }

            }
        }
        return false;
    }

    private boolean causesCheck(Move move, Colour colour) {
        Cell start = board.getCell(move.fr, move.fc);
        Cell end = board.getCell(move.tr, move.tc);

        Piece captured = end.getPiece();

        end.setPiece(start.getPiece());
        start.setPiece(null);

        boolean check = isCheck(colour);
        start.setPiece(end.getPiece());
        end.setPiece(captured);

        return check;
    }

}

/* -------- Board ---------- */
class Board {
    private static Board instance; // single obj
    private final Cell[][] cell = new Cell[8][8];

    public static synchronized Board getInstance() {
        if(instance == null) {
            instance = new Board(); // singleton object
        }
        return instance;
    }

    public void initialize(){
        for(int r = 0; r< 8; r++) {
            for(int c = 0; c < 8; c++) {
                cell[r][c] = new Cell(r, c);
            }
        }

        for(int c= 0; c < 8 ; c++) {
            cell[1][c].setPiece(PieceFactory.createPiece(PieceType.PAWN, Colour.BLACK));
            cell[6][c].setPiece(PieceFactory.createPiece(PieceType.PAWN, Colour.WHITE));
        }

        // place rooks
        cell[0][0].setPiece(PieceFactory.createPiece(PieceType.ROOK, Colour.BLACK));
        cell[0][7].setPiece(PieceFactory.createPiece(PieceType.ROOK, Colour.BLACK));
        cell[7][0].setPiece(PieceFactory.createPiece(PieceType.ROOK, Colour.WHITE));
        cell[7][7].setPiece(PieceFactory.createPiece(PieceType.ROOK, Colour.WHITE));

        // place Knights
        cell[0][1].setPiece(PieceFactory.createPiece(PieceType.KNIGHT, Colour.BLACK));
        cell[0][6].setPiece(PieceFactory.createPiece(PieceType.KNIGHT, Colour.BLACK));
        cell[7][1].setPiece(PieceFactory.createPiece(PieceType.KNIGHT, Colour.WHITE));
        cell[7][6].setPiece(PieceFactory.createPiece(PieceType.KNIGHT, Colour.WHITE));

        // place Bishops
        cell[0][2].setPiece(PieceFactory.createPiece(PieceType.BISHOP, Colour.BLACK));
        cell[0][5].setPiece(PieceFactory.createPiece(PieceType.BISHOP, Colour.BLACK));
        cell[7][2].setPiece(PieceFactory.createPiece(PieceType.BISHOP, Colour.WHITE));
        cell[7][5].setPiece(PieceFactory.createPiece(PieceType.BISHOP, Colour.WHITE));

        // Place Queen
        cell[0][3].setPiece(PieceFactory.createPiece(PieceType.QUEEN, Colour.BLACK));
        cell[7][3].setPiece(PieceFactory.createPiece(PieceType.QUEEN, Colour.WHITE));

        // Place King
        cell[0][4].setPiece(PieceFactory.createPiece(PieceType.KING, Colour.BLACK));
        cell[7][4].setPiece(PieceFactory.createPiece(PieceType.KING, Colour.WHITE));

    }

    public Cell getCell(int row, int col) {
        return cell[row][col];
    }
}

//Factory Pattern - for piece creation
/* ---------- PieceFactory ------------ */
class PieceFactory {
    public static Piece createPiece(PieceType type, Colour colour) {
        switch(type) {
            case ROOK:
                return new Rook(colour, new RookStrategy());
            case KNIGHT:
                return new Knight(colour, new KnightStrategy());
            case BISHOP:
                return new Bishop(colour, new BishopStrategy());
            case QUEEN:
                return new Queen(colour, new QueenStrategy());
            case KING:
                return new King(colour, new KingStrategy());
            case PAWN:
                return new Pawn(colour, new PawnStrategy());
            default:
                throw new IllegalArgumentException("Invalid piece type");
        }
    }
}

/* ---------- Cell ----------- */
class Cell {
    private int row;
    private int col;
    private Piece piece;

    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
    }
    void setPiece(Piece piece) {
        this.piece = piece;
    }
    int getRow() {
        return row;
    }
    int getCol() {
        return col;
    }

    Piece getPiece() {
        return piece;
    }

    public boolean isOccupied() {
        return piece != null;
    }
}

interface MoveStrategy {
    boolean isValidMove(Board board, Move move, Colour colour);
}

/* ---------------- ROOK ---------------- */

class RookStrategy implements MoveStrategy {

    public boolean isValidMove(Board board, Move move, Colour colour) {

        if (move.fr != move.tr && move.fc != move.tc)
            return false;

        int rowStep = Integer.compare(move.tr, move.fr);
        int colStep = Integer.compare(move.tc, move.fc);

        int r = move.fr + rowStep;
        int c = move.fc + colStep;

        while (r != move.tr || c != move.tc) {

            if (board.getCell(r, c).isOccupied())
                return false;

            r += rowStep;
            c += colStep;
        }

        return true;
    }
}

/* ---------------- BISHOP ---------------- */

class BishopStrategy implements MoveStrategy {

    public boolean isValidMove(Board board, Move move, Colour colour) {

        int rowDiff = Math.abs(move.tr - move.fr);
        int colDiff = Math.abs(move.tc - move.fc);

        if (rowDiff != colDiff)
            return false;

        int rowStep = (move.tr > move.fr) ? 1 : -1;
        int colStep = (move.tc > move.fc) ? 1 : -1;

        int r = move.fr + rowStep;
        int c = move.fc + colStep;

        while (r != move.tr && c != move.tc) {

            if (board.getCell(r, c).isOccupied())
                return false;

            r += rowStep;
            c += colStep;
        }

        return true;
    }
}

/* ---------------- QUEEN ---------------- */

class QueenStrategy implements MoveStrategy {
    private final RookStrategy rook = new RookStrategy();
    private final BishopStrategy bishop = new BishopStrategy();

    public boolean isValidMove(Board board, Move move, Colour colour) {

        return rook.isValidMove(board, move, colour) ||
                bishop.isValidMove(board, move, colour);
    }
}

/* ---------------- KNIGHT ---------------- */

class KnightStrategy implements MoveStrategy {

    public boolean isValidMove(Board board, Move move, Colour colour) {

        int rowDiff = Math.abs(move.tr - move.fr);
        int colDiff = Math.abs(move.tc - move.fc);

        return (rowDiff == 2 && colDiff == 1) ||
                (rowDiff == 1 && colDiff == 2);
    }
}

/* ---------------- KING ---------------- */

class KingStrategy implements MoveStrategy {

    public boolean isValidMove(Board board, Move move, Colour colour) {

        int rowDiff = Math.abs(move.tr - move.fr);
        int colDiff = Math.abs(move.tc - move.fc);

        return rowDiff <= 1 && colDiff <= 1;
    }
}

/* ---------------- PAWN ---------------- */

class PawnStrategy implements MoveStrategy {

    public boolean isValidMove(Board board, Move move, Colour colour) {

        int direction = (colour == Colour.WHITE) ? -1 : 1;

        int rowDiff = move.tr - move.fr;
        int colDiff = Math.abs(move.tc - move.fc);

        Cell end = board.getCell(move.tr, move.tc);

        /* Normal move */
        if (colDiff == 0) {

            if (rowDiff == direction && !end.isOccupied())
                return true;

            /* First move double step */

            if ((colour == Colour.WHITE && move.fr == 6) ||
                    (colour == Colour.BLACK && move.fr == 1)) {

                if (rowDiff == 2 * direction &&
                        !board.getCell(move.fr + direction, move.fc).isOccupied() &&
                        !end.isOccupied()) {
                    return true;
                }
            }
        }

        /* Capture move */

        if (colDiff == 1 && rowDiff == direction) {

            if (end.isOccupied() &&
                    end.getPiece().getColour() != colour) {
                return true;
            }
        }

        return false;
    }
}

/* ------- Piece ---------- */
abstract class Piece {
    protected Colour colour;
    PieceType type;
    MoveStrategy moveStrategy;


    Piece(Colour colour, PieceType type, MoveStrategy moveStrategy) {
        this.colour = colour;
        this.type = type;
        this.moveStrategy = moveStrategy;
    }

    Colour getColour() {
        return colour;
    }

    public boolean canMove(Board board, Move move) {
        return moveStrategy.isValidMove(board, move, colour);
    }
}

class Rook extends Piece {
    Rook(Colour colour, MoveStrategy moveStrategy) {
        super(colour, PieceType.ROOK, moveStrategy);
    }
}
class Knight extends Piece {
    Knight(Colour colour, MoveStrategy moveStrategy) {
        super(colour, PieceType.KNIGHT, moveStrategy);
    }
}
class Bishop extends Piece {
    Bishop(Colour colour, MoveStrategy moveStrategy) {
        super(colour, PieceType.BISHOP, moveStrategy);
    }
}
class Queen extends Piece {
    Queen(Colour colour, MoveStrategy moveStrategy) {
        super(colour, PieceType.QUEEN, moveStrategy);
    }
}
class King extends Piece {
    King(Colour colour, MoveStrategy moveStrategy) {
        super(colour, PieceType.KING, moveStrategy);
    }
}
class Pawn extends Piece {
    Pawn(Colour colour, MoveStrategy moveStrategy) {
        super(colour, PieceType.PAWN, moveStrategy);
    }
}

/* ------- Move ---------- */
class Move{
    int fr;
    int fc;
    int tr;
    int tc;

    Move(int fr, int fc, int tr, int tc) {
        this.fr = fr;
        this.fc = fc;
        this.tr = tr;
        this.tc = tc;
    }
}

/* ------- Driver code ---------- */
public class ChessGame {
    public static void main(String[] args) {
        Player player1 = new Player(Colour.WHITE);
        Player player2 = new Player(Colour.BLACK);
        Game game = new Game(player1, player2);
        game.start();
        Move move = new Move(6, 0, 5, 0);
        game.makeMove(move);
    }
}

/*
How checkmate works in your design?

1. After every move -> updateGameStatus()

2. Check if king is under attack
   -> isCheck()

3. If king is under attack AND
   player has no legal moves
   -> CHECKMATE

4. If king not in check AND
   player has no legal moves
   -> STALEMATE
 */

/*
Why did you choose Strategy Pattern for movement?

Each chess piece has different movement rules.
Instead of putting many if-else conditions in Piece class,
we encapsulated movement behavior in separate strategy classes.

This follows:
- Open Closed Principle
- Single Responsibility Principle
 */