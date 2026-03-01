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
STALEMATE

Color
----------
WHITE
BLACK


        Game
----------------------------
- Player currentPlayer
- Board board
------------------------
+ start()
+ makeMove(move)


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


 */



enum GameStatus {
    ACTIVE,
    CHECK,
    STALEMATE,
    CHECKMATE
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

class Game {
    private final Board board = Board.getInstance();
    private Colour currentTurn = Colour.WHITE;

    void start() {;
        board.initialize();
    }

    void makeMove(int startRow, int startCol, int endRow, int endCol) {
        Cell start = board.getCell(startRow, startCol);
        Cell end = board.getCell(endRow, endCol);

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

        if (!piece.isValidMove(board, start, end)) {
            System.out.println("Invalid move");
            return;
        }

        end.setPiece(piece);
        start.setPiece(null);
        System.out.println("Move successful");

        currentTurn = currentTurn == Colour.WHITE ? Colour.BLACK : Colour.WHITE;
    }

}

class Board {
    private static Board instance; // single obj
    private final Cell[][] cell = new Cell[8][8];

    public static Board getInstance() {
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
class PieceFactory {
    public static Piece createPiece(PieceType type, Colour colour) {
        switch(type) {
            case PieceType.ROOK:
                return new Rook(colour);
            case PieceType.KNIGHT:
                return new Knight(colour);
            case PieceType.BISHOP:
                return new Bishop(colour);
            case PieceType.QUEEN:
                return new Queen(colour);
            case PieceType.KING:
                return new King(colour);
            case PieceType.PAWN:
                return new Pawn(colour);
            default:
                throw new IllegalArgumentException("Invalid piece type");
        }
    }
}

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

    Piece getPiece() {
        return piece;
    }

    int getRow() {
        return row;
    }
    int getCol() {
        return col;
    }
}

abstract class Piece {
    protected Colour colour;

    Piece(Colour colour) {
        this.colour = colour;
    }

    Colour getColour() {
        return colour;
    }

    abstract boolean isValidMove(Board board, Cell start, Cell end) ;
}

class Rook extends Piece {
    Rook(Colour colour) {
        super(colour);
    }

    @Override
    boolean isValidMove(Board board, Cell start, Cell end) {
        return start.getRow() == end.getRow() || start.getCol() == end.getCol();
    }
}

class Knight extends Piece {
    Knight(Colour colour) {
        super(colour);
    }

    @Override
    boolean isValidMove(Board board, Cell start, Cell end) {
//        write logic
        return false;
    }
}

class Bishop extends Piece {
    Bishop(Colour colour) {
        super(colour);
    }

    @Override
    boolean isValidMove(Board board, Cell start, Cell end) {
//        write logic
        return false;
    }
}

class Queen extends Piece {
    Queen(Colour colour) {
        super(colour);
    }

    @Override
    boolean isValidMove(Board board, Cell start, Cell end) {
//        write logic
        return false;
    }
}

class King extends Piece {
    King(Colour colour) {
        super(colour);
    }

    @Override
    boolean isValidMove(Board board, Cell start, Cell end) {
//        write logic
        return false;
    }
}

class Pawn extends Piece {
    Pawn(Colour colour) {
        super(colour);
    }

    @Override
    boolean isValidMove(Board board, Cell start, Cell end) {
//        write logic
        return false;
    }
}


public class ChessGame {
    public static void main(String[] args) {
        Game game = new Game();
        game.start();
        game.makeMove(6, 0, 5, 0);
    }
}
