/* global bp, bsync, PHILOSOPHER_COUNT,TicTacToeGameMain */

importPackage(Packages.il.ac.bgu.cs.bp.bpjs.TicTacToe.events);

bp.log.info('Tic-Tac-Toe - Let the game begin!');

var SquareCount = 0;
var countX = 0;
var countO = 0;

// GameRules

function addSquareBThreads(row, col) {

	//Detects mouse click	
	bp.registerBThread("ClickHandler(" + row + "," + col + ")", function() {
		while (true) {

			if (!isModelChecking) {
				bsync({
					waitFor : [ Click(row, col) ]
				});
			}

			bsync({
				request : [ X(row, col) ]
			});
		}
	});


	 //Block further marking of a square already marked by X or O.
	bp.registerBThread("SquareTaken(" + row + "," + col + ")", function() {
		while (true) {
			bsync({
				waitFor : [ X(row, col), O(row, col) ]
			});
			bsync({
				block : [ X(row, col), O(row, col) ]
			});
			SquareCount++;
		}
	});
}

//Enforce Turns
bp.registerBThread("EnforceTurns", function() {
	while (true) {
		bsync({
			waitFor : [ X(0,0), X(0,1), X(0,2), X(1,0), X(1,1), X(1,2), X(2,0), X(2,1), X(2,2) ],

			block : [ O(0,0), O(0,1), O(0,2), O(1,0), O(1,1), O(1,2), O(2,0), O(2,1), O(2,2) ]

		});

		bsync({
			waitFor : [ O(0,0), O(0,1), O(0,2), O(1,0), O(1,1), O(1,2), O(2,0), O(2,1), O(2,2) ],

			block : [ X(0,0), X(0,1), X(0,2), X(1,0), X(1,1), X(1,2), X(2,0), X(2,1), X(2,2) ]
		});
	}
});



//Player O defult strategy

bp.registerBThread("Sides", function() {
	while (true) {
		bsync({
			request : [ O(0,1,0).priority(0), O(1,0,0).priority(0), O(1,2).priority(0), O(2,1).priority(0) ]			
		});
				
	}
});



bp.registerBThread("Corners", function() {
	while (true) {
		
		bsync({
			request : [ O(0,0).priority(1), O(0,2).priority(1), O(2,0).priority(1), O(2,2).priority(1) ]
		});
				
	}
});

bp.registerBThread("AddThirdO", function() {
	
	var e;
	var wt;
	
	while (true) {
		bsync({
			waitFor : [ O(0, 0) ]
		});
		
		bsync({
			waitFor : [ O(0,1) ]
		});
			
						
		bsync({
			request : [ O(2,2,1)],
			block:    [O(0,0), O(0,1), O(0,2), O(1,0), O(1,1), O(1,2), O(2,0), O(2,1), O(2,2)]
		});	
	}
});

for (var r = 0; r < 3; r++) {
	for (var c = 0; c < 3; c++) {
		addSquareBThreads(r, c);
	}
}






//
////Detect if X Wins
//bp.registerBThread("DetectXWin", function() {
//while (true) {
//	if(countX == 3){
//		bsync({
//			request : [ StaticEvents.XWin ]			
//		});
//	}	
//}
//});
//
////Detect if O Wins
//bp.registerBThread("DetectOWin", function() {
//while (true) {
//	if(countO == 3){
//		bsync({
//			request : [ StaticEvents.OWin ]			
//		});
//	}	
//}
//});
//
////Detect if it's a draw
//bp.registerBThread("DetectDraw", function() {
//while (true) {
//	if(SquareCount == 9 && countX < 3 && countO < 3){
//		bsync({
//			request : [ StaticEvents.draw ]			
//		});
//	}	
//}
//});
//
////Declare Winner
//bp.registerBThread("DeclareWinner", function() {
//while (true) {
//	
//	bsync({
//		waitFor : [ StaticEvents.XWin, StaticEvents.OWin, StaticEvents.draw ]			
//	});
//			
//}
//});
