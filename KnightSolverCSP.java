package nKnightsCSP;

import java.util.ArrayList;

public class KnightSolverCSP {
	
	private int numKnights; //number of knights to be placed on the board
	private int boardSize; //size of the board
	private Knight[] knights;  //All of the knights to be assigned board positions
	private ArrayList<ArrayList<BoardPosition>> seenMoves; //Lists of states already seen
	private ArrayList<BoardPosition> availBoardPositions; //Board Positions still available
	private int knightPointer; //Pointer for the knight being assigned a position
	
	/*
	 * Initializes all of the components necessary for the game
	 */
	public KnightSolverCSP(int boardSize){
		if(boardSize < 3)
			throw new IllegalArgumentException("Board size too small.  Please pick a number greater than 2");
		this.boardSize = boardSize;
		numKnights = numKnights(boardSize);
		availBoardPositions = initializeBoard();
		knights = new Knight[numKnights];
		seenMoves = new ArrayList<ArrayList<BoardPosition>>(numKnights);
		for(int i = 0; i < numKnights; i++){
			seenMoves.add(new ArrayList<BoardPosition>());
		}
		for(int i = 0; i < knights.length; i++){
			knights[i] = new Knight(null);
		}
		knightPointer = 0;
	}
	
	
	/*
	 * Method Used to Solve the nKnights problem	
	 */
	public void solve(){
		long startTime = System.currentTimeMillis();
		while(knightPointer < numKnights && knightPointer > -1){ //until all knights assigned location or no solution found
			ArrayList<BoardPosition> curSeen = seenMoves.get(knightPointer); //Current list of seen positions
			//BoardPosition bp = getLCV(curSeen);
			BoardPosition bp = arbitraryBP(curSeen); //arbitrary assignment for a board position
			if(bp.getX() != -1){ //if a viable move is returned
				knights[knightPointer].setBoardPosition(bp); //set the knight position
				addSeen(bp, knightPointer); //add to the list of seen positions 
				availBoardPositions.remove(bp); //remove bp from the list of available board positions
				ArrayList<BoardPosition> bms = bp.posAttacked(); //get a list of positions that bp attacks
				for(BoardPosition conf : bms){
					for(int i = 0; i < availBoardPositions.size(); i++){
						if(availBoardPositions.get(i).getX() == conf.getX() && availBoardPositions.get(i).getY() == conf.getY()){
							availBoardPositions.remove(i); //remove conflicts from available positions
						}
					}
				}
				knightPointer++; //advance the knight pointer
			}else{
				seenMoves.get(knightPointer).clear(); //clears the seen move list for that knight
				knightPointer--; //reduces the knight pointer
				availBoardPositions.add(knights[knightPointer].getBoardPosition()); //add back the previous knight
				ArrayList<BoardPosition> bms = knights[knightPointer].getBoardPosition().posAttacked(); //get the list of conflicts for previous knight 
				ArrayList<BoardPosition> prevConflicts = allPreviousConflicts(); //list of all already assigned knights' conflicts
				for(BoardPosition lastConflict : bms){
					boolean seen = false;
					for(BoardPosition pconf : prevConflicts){
						if(pconf.getX() == lastConflict.getX() && pconf.getY() == lastConflict.getY()){
							seen = true;
						}
					}
					if(!seen){
						availBoardPositions.add(lastConflict); //adds the conflicting position back if it doesnt conflict with previously assigned positions
					}
				}
				knights[knightPointer].setBoardPosition(null); //reset the knight value
			}
		}
		long endTime = System.currentTimeMillis();
		System.out.println("Execution Time: " + (endTime-startTime));
	}
	
	/*
	 * Returns the least constraining board position
	 */
	private BoardPosition getLCV(ArrayList<BoardPosition> seen){
		int minConflicts = 10000000;
		BoardPosition min = new BoardPosition(-1,-1);
		for(BoardPosition bp : availBoardPositions){
			boolean isSeen = false;
			for(BoardPosition bpSeen : seen){ //check to see if bp has already been seen
				if(bp.getX() == bpSeen.getX() && bp.getY() == bpSeen.getY()){
					isSeen = true;
				}
			}
			boolean isPlaced = false;
			for(int i = 0; i < knightPointer; i++){
				Knight curKnight = knights[i]; //check to see if there is a knight already in that position
				if(curKnight.getBoardPosition().getX() == bp.getX() && curKnight.getBoardPosition().getY() == bp.getY()){
					isPlaced = true;
				}
			}
			if(!isSeen && !isPlaced){
				ArrayList<BoardPosition> bps = bp.posAttacked();
				if(bps.size()<minConflicts){
					min = bp; //set min if bp hasnt been seen or already have a knight placed there
					minConflicts = bps.size();
				}
			}
		}
		return min;
	}
	
	/*
	 * Assigns an arbitrary BP with no LCV
	 */
	private BoardPosition arbitraryBP(ArrayList<BoardPosition> seen){
		BoardPosition bp_1 = new BoardPosition(-1,-1);
		for(BoardPosition bp : availBoardPositions){
			boolean isSeen = false;
			for(BoardPosition bpSeen : seen){
				if(bp.getX() == bpSeen.getX() && bp.getY() == bpSeen.getY()){
					isSeen = true;
				}
			}
			boolean isPlaced = false;
			for(int i = 0; i < knightPointer; i++){
				Knight curKnight = knights[i];
				if(curKnight.getBoardPosition().getX() == bp.getX() && curKnight.getBoardPosition().getY() == bp.getY()){
					isPlaced = true;
				}
			}
			if(!isSeen && !isPlaced){
				bp_1 = bp;
				break;
			}
		}
		return bp_1;
	}
	
	private ArrayList<BoardPosition> initializeBoard(){
		ArrayList<BoardPosition> boardPositions = new ArrayList<BoardPosition>();
		for(int i = 0; i < boardSize; i++){
			for(int j = 0; j < boardSize; j++){
				BoardPosition bp = new BoardPosition(i,j);
				boardPositions.add(bp);
			}
		}
		return boardPositions;
	}
	
	private int numKnights(int boardSize){
		int numKnights = 0;
		if(boardSize % 2 == 0 && boardSize > 2){
			numKnights = (boardSize * boardSize)/2;
		}else{
			numKnights = ((boardSize * boardSize)+1)/2;
		}
		return numKnights;
	}
	
	/*
	 * Returns a list of all conflicts from the already placed knights
	 */
	private ArrayList<BoardPosition> allPreviousConflicts(){
		ArrayList<BoardPosition> cons = new ArrayList<BoardPosition>();
		for(int i = 0; i < knightPointer; i++){
			BoardPosition bp = knights[i].getBoardPosition();
			ArrayList<BoardPosition> conflicts = bp.posAttacked();
			boolean alreadyContained = false;
			for(BoardPosition bp_ : conflicts){
				if(!alreadyContained){
					cons.add(bp_);
				}
			}
		}
		return cons;
	}
	
	public void printState(){
		System.out.print("Knights: [");
		for(int i = 0; i < knights.length; i++){
			System.out.print(knights[i] + " ");
		}
		System.out.println("]");
		System.out.println("Board Positions: " + availBoardPositions);
	}
	
	public void printSeen(){
		for(ArrayList<BoardPosition> seenList : seenMoves){
			System.out.println(seenList);
		}
	}
	
	/*
	 * Adds a bp to the already seen list for the inputted index
	 */
	private void addSeen(BoardPosition bp, int index){
		boolean seen = false;
		ArrayList<BoardPosition> seenList = seenMoves.get(index);
		for(BoardPosition seenBP : seenList){
			if(seenBP.getX() == bp.getX() && seenBP.getY() == bp.getY()){
				seen = true;
			}
		}
		if(!seen){
			seenMoves.get(index).add(bp);
		}
	}
	
	class BoardPosition{
		int x;
		int y;
		
		BoardPosition(int x, int y){
			this.x = x;
			this.y = y;
		}
		
		public int getX(){return x;}
		public int getY(){return y;}
		
		public void setX(int x){this.x = x;}
		public void setY(int y){this.y = y;}
		
		public String toString(){return "{" + x + "," + y + "} ";}
		
		public ArrayList<BoardPosition> posAttacked(){
			ArrayList<BoardPosition> bps = new ArrayList<BoardPosition>();
			int newX;
			int newY;
			
			newX = x+1;
			newY = y+2;
			if(newX < boardSize && newY < boardSize)
				bps.add(new BoardPosition(newX, newY));
			
			newY = y-2;
			if(newX < boardSize && newY >= 0)
				bps.add(new BoardPosition(newX, newY));
			
			newX = x+2;
			newY = y+1;
			if(newX < boardSize && newY < boardSize)
				bps.add(new BoardPosition(newX, newY));
			
			newY = y-1;
			if(newX < boardSize && newY >= 0)
				bps.add(new BoardPosition(newX, newY));
			
			newX = x-1;
			newY = y+2;
			if(newX >= 0 && newY < boardSize)
				bps.add(new BoardPosition(newX, newY));
			
			newY = y-2;
			if(newX >= 0 && newY >= 0)
				bps.add(new BoardPosition(newX, newY));
			
			newX = x-2;
			newY = y+1;
			if(newX >= 0 && newY < boardSize)
				bps.add(new BoardPosition(newX, newY));
			
			newY = y-1;
			if(newX >= 0 && newY >= 0)
				bps.add(new BoardPosition(newX, newY));
			
			return bps;		
		}
	}
	
	class Knight{
		BoardPosition bp;
		
		Knight(){}
		
		Knight(BoardPosition bp){
			this.bp = bp;
		}
		
		public void setBoardPosition(BoardPosition bp){this.bp = bp;}
		public BoardPosition getBoardPosition(){return bp;}
		
		public String toString(){return "Knight position: " + bp;}
		
		
	}

}
