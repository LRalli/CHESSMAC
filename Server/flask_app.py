from flask import Flask,jsonify
from flask_restful import reqparse
import random
from stockfish import Stockfish
from flask import request
import urllib.parse

stockfish = Stockfish("/home/LRalli/stockfish/stockfish-ubuntu-x86-64")

stock=[None] * 30

# app is an instance of the Flask class that will route incoming HTTP requests within the app
app = Flask(__name__)

{
    "Write Debug Log": "false",
    "Contempt": 0,
    "Min Split Depth": 0,
    "Threads": 1,
    "Ponder": "false",
    "Hash": 16,
    "MultiPV": 1,
    "Skill Level": 20,
    "Move Overhead": 30,
    "Minimum Thinking Time": 20,
    "Slow Mover": 80,
    "UCI_Chess960": "false",
}

# Initialize the parser for incoming HTTP requests arguments
parser = reqparse.RequestParser()

# /hello route with GET returns a json object with the state of the server
@app.route('/hello', methods=['GET'])
def hello():
    stato="OK"
    return jsonify({'state':stato})


# the / route with POST assesses if the move is valid, in which case it executes it on the
# stock engine and returns 2 boolean: validity of move and checkmate condition
@app.route('/', methods=['POST'])
def handle_root():
    index = request.args.get('index', type=int)
    move = request.args.get('move')
    mate = "false"
    valid = stock[index].is_move_correct(move)

    if valid:
        stock[index].make_moves_from_current_position([move])
        stockfish_best_move = stock[index].get_best_move()

        if stockfish_best_move is None:
            mate = "true"

    return {'valid': valid, 'mate': mate}


# The /bestMove route with GET returns the best possible move
# The /bestMove route with POST assesses if the posted move is the best one you can perform on the stock engine
@app.route("/bestMove", methods=["GET","POST"])
def bestMove():
    if request.method == 'GET':
        index = request.args.get('index', type = int)
        return {'move': stock[index].get_best_move() }
    elif request.method == 'POST':
        index = request.args.get("index", type=int)
        move = request.args.get("move")
        best = stock[index].get_best_move()
        correct = "false"

        if move == best:
            correct = "true"

        return {"correct": correct, "best": best}

"""
# Return true if queried move is valid. Used to build list of valid moves.
@app.route('/valid', methods=['GET'])
def checkmossa():
    index = request.args.get('index', type = int)
    move = request.args.get('move')
    valid = stock[index].is_move_correct(move)

    return {'valid': valid}
"""


# The /info route with GET returns the evaluation of the current position
# The /info route with POST sets the ELO rating and returns it
@app.route('/info', methods=['GET', 'POST'])
def info():
    if request.method == 'GET':
        index = request.args.get('index', type=int)
        return stock[index].get_evaluation()
    elif request.method == 'POST':
        index = request.args.get('index', type=int)
        elo = request.args.get('ELO')
        stock[index].set_elo_rating(elo)
        return {'ELO': elo }


# The /reset route handles a get request to reset the current stockfish instance and
# returns a json object with index and error fields
@app.route('/reset', methods=['GET'])
def reset():
    index = request.args.get('index', type = int)
    errore=False
    # stock[index].set_fen_position("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1")
    if(index>=0 and index<=9):
        stock[index] = None
    else:
        errore=True

    return jsonify({'reset_id': index, 'errore': errore})


# /stockfish handles a POST request to make a move from the current position
# returns a json object with valid, best_stockfish_move as response, and checkmate boolean
@app.route('/stockfish', methods=['POST'])
def stockfish():
    index = int(request.args.get('index'))
    move = request.args.get('move')
    mate = ""
    stockfish_best_move = ""

    # Convert castling notation if necessary
    if "000" in move:
        move = "e1c1"
    elif "00" in move:
        move = "e1g1"

    print(move)

    valid = stock[index].is_move_correct(move)
    print(valid)
    if valid:
        stock[index].make_moves_from_current_position([move])
        stockfish_best_move = stock[index].get_best_move()
        if stockfish_best_move is not None:
            stock[index].make_moves_from_current_position([stockfish_best_move])
            player_best_move = stock[index].get_best_move()
            if player_best_move is None:
                mate = "stockfish"
        else:
            mate = "player"

    return {'valid': valid, 'response': stockfish_best_move, 'mate': mate}


# The /undoStockfish route is used to revert back the fen of the stock engine
@app.route("/undoStockfish", methods=["POST"])
def undoStockfish():
    index = request.args.get("index", type=int)
    fen = urllib.parse.unquote(request.args.get('fen', ''))
    print(fen)
    stock[index].set_fen_position(fen)

    return {"response": "ok"}


# The quizStart route returns a random fen from the quiz collection and a stock engine index where that fen was loaded
@app.route("/quizStart", methods=["GET"])
def quizStart():
    global stock
    quiz_collection = [
"4r1rk/5K1b/7R/R7/8/8/8/8 w - - 0 1",
"8/1r6/8/3R4/k7/p1K5/4r3/R7 w - - 0 1",
"6k1/8/6K1/8/8/3r4/4r3/5R1R w - - 0 1",
"2rkr3/2ppp3/2n1n3/R2R4/8/8/3K4/8 w - - 0 1",
"4rkr1/1R1R4/4bK2/8/8/8/8/8 w - - 0 1",
"5K1k/6pp/7R/8/8/8/8/6R1 w - - 0 1",
"2k5/1q4b1/3K4/8/7R/8/7R/8 w - - 0 1",
"8/8/q5b1/7k/5Kp1/1R1R4/8/8 w - - 0 1",
"k7/3b4/1K6/8/8/5q2/2R1R3/8 w - - 0 1",
"8/1R1R4/8/p7/k1K5/r5r1/8/8 w - - 0 1",
"kr6/1p6/8/1p5R/6R1/8/1r6/5K2 w - - 0 1",
"6R1/8/8/7p/5K1k/r6r/8/6R1 w - - 0 1",
"4R3/8/5K2/7p/R5pk/5npr/8/8 w - - 0 1",
"kb6/p4q2/2K5/8/8/8/8/1R1R4 w - - 0 1",
"8/6p1/6rk/6np/R6R/6K1/8/8 w - - 0 1",
"kn1R4/ppp5/2q5/8/8/8/8/3RK3 w - - 0 1",
"1kb4R/1npp4/8/8/8/8/8/R5K1 w - - 0 1",
"8/8/1b6/kr6/pp6/1n6/7R/R3K3 w Q - 0 1",
"5K1k/7p/8/2p5/2rp4/8/p7/1B4B1 w - - 0 1",
"k7/p7/B2K4/8/8/8/3p2p1/4B3 w - - 0 1",
"8/5n2/8/6B1/8/4K3/7p/5B1k w - - 0 1",
"8/5p2/7p/5Kpk/4BB1p/7r/8/8 w - - 0 1",
"8/6N1/8/pp6/kp6/pp5K/2N5/8 w - - 0 1",
"8/8/8/7N/8/8/1p5p/N3K2k w - - 0 1",
"4K3/8/8/4N1pr/4b1pk/4N1nr/8/8 w - - 0 1",
"k7/ppK5/2N5/3N4/8/8/7p/8 w - - 0 1",
"7k/4K1pp/6pn/6N1/6N1/8/8/8 w - - 0 1",
"8/8/7p/5K1k/7p/8/2pn1N2/3N4 w - - 0 1",
"1k1B4/8/1K6/1n6/q7/8/8/3R4 w - - 0 1",
"6Bk/R4K2/8/8/8/8/8/8 w - - 0 1",
"kb6/6n1/K7/5p2/4p3/8/8/4R2B w - - 0 1",
"5Knk/7b/R7/8/7B/8/8/8 w - - 0 1",
"8/8/8/6nr/4nB1k/8/6K1/5R2 w - - 0 1",
"7k/5ppr/K5p1/8/8/8/2B5/2R5 w - - 0 1",
"7k/7p/5K1b/8/6R1/8/1B6/1q6 w - - 0 1",
"6B1/p1K5/k7/pp6/8/8/8/R7 w - - 0 1",
"8/8/pp6/kb2B3/4n3/K1R5/8/8 w - - 0 1",
"k7/pbK5/8/1B2n3/8/8/6p1/1R6 w - - 0 1",
"8/8/7p/5K1k/6pp/1R6/B4n1r/8 w - - 0 1",
"r7/kp6/pR1Q4/5q2/8/8/8/3K4 w - - 0 1",
"4Q3/kr6/pp6/8/8/8/6q1/R2K4 w - - 0 1",
"6rk/6n1/1R1Q4/7r/8/8/8/3K4 w - - 0 1",
"1k4r1/ppp5/8/8/2q5/8/5Q2/3K1R2 w - - 0 1",
"3R4/2q5/8/rpn5/kp5Q/2n5/1K6/8 w - - 0 1",
"5Q2/pp6/kp1R4/8/K7/8/4q3/8 w - - 0 1",
"1q1r3k/7p/7K/8/4R3/2p5/8/1Q6 w - - 0 1",
"kr6/1p6/p5R1/8/1q6/8/Q7/2K5 w - - 0 1",
"k7/p2bR3/Q7/8/3q4/8/8/2K5 w - - 0 1",
"k3r3/pR6/K7/2b5/8/8/1Q3q2/8 w - - 0 1",
"3rkr2/R3p3/8/4K3/8/7Q/5q2/8 w - - 0 1",
"2k5/1ppn4/1q6/8/Q7/8/5R2/4K3 w - - 0 1",
"k1r5/p1p5/N1K5/8/3q4/8/8/1R6 w - - 0 1",
"8/8/6Nr/5Kbk/R7/8/8/8 w - - 0 1",
"kr6/pp6/8/8/2N4R/8/8/3K4 w - - 0 1",
"4nrkr/5pp1/8/7N/8/8/8/3K2R1 w - - 0 1",
"2Nnkr2/3p3R/8/5n2/8/8/8/7K w - - 0 1",
"2R5/8/pn6/k1N5/8/1K6/6q1/8 w - - 0 1",
"5Kbk/R7/4q1P1/8/8/8/8/8 w - - 0 1",
"5Kbk/6pp/6pR/5P2/8/8/8/8 w - - 0 1",
"8/8/6rp/6pk/5b1p/5K2/6P1/6R1 w - - 0 1",
"8/6kp/4r1p1/q3r3/6K1/B7/8/2Q5 w - - 0 1",
"8/pk6/1p6/1B2r3/K7/2Q1q3/8/8 w - - 0 1",
"kb4q1/1p1B4/pK6/8/8/8/8/5Q2 w - - 0 1",
"qkb5/4p3/1K1p4/8/5Q2/6B1/8/8 w - - 0 1",
"B7/8/8/7K/4b3/Q7/7p/1q4bk w - - 0 1",
"8/8/B7/3qp3/2ppkpp1/8/4K3/3Q4 w - - 0 1",
"6bk/7p/7K/4N3/8/8/7B/8 w - - 0 1",
"kB1KN3/p7/n7/8/8/8/8/8 w - - 0 1",
"5K1k/6pp/6p1/6B1/6N1/8/8/8 w - - 0 1",
"8/8/7p/5K1k/6pp/1p6/2B2N2/8 w - - 0 1",
"8/8/5B2/8/2pN4/K7/pp6/kb6 w - - 0 1",
"kbK5/p7/2pN4/3p4/8/8/8/5B2 w - - 0 1",
"7B/8/pb6/kpn5/b1p5/1P6/1K6/8 w - - 0 1",
"kb1n4/8/KP6/8/B7/8/8/8 w - - 0 1",
"3B1K1k/6pp/4b3/7P/8/8/8/8 w - - 0 1",
"8/8/8/6pp/5p1k/5K1b/5P1B/8 w - - 0 1",
"k1r2q2/ppQ5/N7/8/8/8/8/3K4 w - - 0 1",
"4q2k/4N1pr/8/8/2Q5/8/4K3/8 w - - 0 1",
"rknN4/2p5/1rQ5/8/8/8/1q6/3K4 w - - 0 1",
"4r1kr/5b1p/5KN1/8/8/Q7/3q4/8 w - - 0 1",
"7k/4NKpp/4Q3/8/8/2q2p2/8/6r1 w - - 0 1",
"k1b5/8/NKn5/8/4q3/8/7Q/8 w - - 0 1",
"5rkr/5ppp/8/4K3/6N1/2Q5/q7/8 w - - 0 1",
"krQ5/p7/8/4q3/N7/8/8/3K4 w - - 0 1",
"8/1q6/4NQ1r/5npk/8/7K/8/6r1 w - - 0 1",
"k1r5/p1pq4/Qp1p4/8/3N4/8/3K4/8 w - - 0 1",
"3q2rk/5Q1p/6bK/4N3/8/8/8/8 w - - 0 1",
"8/8/5Q2/2q3pk/7b/8/4K1P1/8 w - - 0 1",
"8/b2Q4/kp2p3/p2q4/1P6/K7/8/8 w - - 0 1",
"8/8/8/pq6/kpp5/7Q/K1P5/8 w - - 0 1",
"5K1k/7b/8/4ppP1/8/6bQ/7q/8 w - - 0 1",
"1K2kb2/4p3/5P2/5Q1q/7r/8/8/8 w - - 0 1",
"k7/p1K2n2/p7/3p1r2/8/8/8/2R5 w - - 0 1",
"4k3/2r1p1p1/3pK3/8/8/8/8/5R2 w - - 0 1",
"7k/6R1/6Kn/8/8/8/8/8 w - - 0 1",
"8/8/8/8/1b6/1k6/8/KBB5 b - - 0 1",
"8/8/8/8/4bN2/5kP1/7P/7K b - - 0 1",
"7K/b4k1P/8/8/8/8/8/6R1 b - - 0 1",
"8/8/7P/1b1Q3K/5k1B/8/8/8 b - - 0 1",
"8/8/8/8/Nb6/8/P7/K1k5 b - - 0 1",
"K7/P1k5/2P5/8/8/7b/8/8 b - - 0 1",
"6qk/8/7K/7Q/8/8/8/8 w - - 0 1",
"8/8/8/8/1n6/7N/7P/5k1K b - - 0 1",
"8/2n5/8/P7/KPk5/P7/8/8 b - - 0 1",
"KBk5/2P5/3n4/8/8/8/8/8 b - - 0 1",
"8/8/p7/kpK5/p7/8/P7/8 w - - 0 1",
"7r/7r/4RP2/5RP1/7k/8/8/7K b - - 0 1",
"7r/r7/8/8/8/1kP5/1P1R4/1K4R1 b - - 0 1",
"2b1kb2/4p3/2K5/8/8/8/8/3BB3 w - - 0 1",
"8/ppprq2p/5Qpk/6N1/2P1B1P1/6P1/PP3P2/n6K w - - 1 0",
"r2k1bbr/pp6/nqp3p1/3p2N1/2PP1B2/1P4QB/P6P/R3R2K w - - 1 0",
"5rk1/p5r1/4p1p1/1p1b1NQ1/2pP4/P6R/q1P2PP1/4R1K1 w - - 1 0",
"4rk2/r4n2/B2R1RQ1/P1p5/8/2q4P/2P3PK/8 w - - 1 0",
"7k/3N2qp/b7/2p1Q1N1/Pp4PK/5p1P/1P3P2/6r1 w - - 1 0",
"8/4Bpb1/4b2k/1p2P1pp/4Q3/1r1NK1PP/4BP2/6r1 w - - 1 0",
"r1r1q2k/pp2BR1p/b3n1p1/P2BP3/2Pn4/8/3N1QPP/R5K1 w - - 1 0",
"r1bq1rk1/4bpn1/p1p1n3/1p1pPBP1/1P6/P1N1PN2/2Q2PP1/3RK2R w K - 1 0",
"1r2k3/5p1Q/1q2bR2/4P3/1p4PB/7P/1r1p4/2R2K2 w - - 1 0",
"8/2r1kpN1/p3p1pp/1p2p1b1/4q1P1/4B2P/PPP2Q2/1K1R4 w - - 1 0",
"8/2p5/2Pp2p1/6Pk/1r1bQ2P/8/5PK1/8 w - - 1 0",
"r1b1k3/pp4pp/2n1p2b/2p1q1N1/8/1PP1B1P1/P2Q2BP/5RK1 w - - 1 0",
"5rrk/2n2p1p/3q1PpQ/p2pNnR1/2pP2N1/P1P3R1/5P1P/7K w - - 1 0",
"4Q3/r4Npk/4p2p/3qP3/1p6/5nP1/5K1P/5R2 b - - 0 1",
"6rr/5p1n/pB1p1k2/P3pP2/1p1nq3/1P1Bb2Q/1P4PK/R4R2 b - - 0 1",
"6k1/5p2/4p2p/2NbP1P1/Pr5q/2R1p2P/2Q3P1/6K1 b - - 0 1"
]

    if None in stock:
        indici = [i for i, v in enumerate(stock) if v == None]
        index = indici[0]
        stock[index] = Stockfish("/home/LRalli/stockfish/stockfish-ubuntu-x86-64")
        random_fen = random.choice(quiz_collection)
        stock[index].set_fen_position(random_fen)
        print("started")
        return jsonify({"response": random_fen, "IDMatch": index})
    else:
        return jsonify({"response": 404})


# /fen  Handles a GET request to retrieve the FEN (Forsyth–Edwards Notation)
# position and the visual representation of the chessboard
# it expects an index argument to know which stockfish instance to use
# returns a json object with fen and state fields
@app.route('/fen', methods=['GET'])
def fen():
    index = request.args.get('index', type = int)
    return {'fen': stock[index].get_fen_position(),'state': stock[index].get_board_visual()}


# /startMatch handles a GET request to start a new stockfish instance
# returns a json object with response field
@app.route('/startMatch', methods=['GET'])
def startMatch():
    global stock
    if (None in stock):
        indici=[i for i,v in enumerate(stock) if v == None]
        index=indici[0]
        stock[index] = Stockfish("/home/LRalli/stockfish/stockfish-ubuntu-x86-64")
        stock[index].set_fen_position("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1")
        print("started")
        return jsonify({'response':index})
    else:
        return jsonify({'response':404})


# first line of code executed when this script is run as the main process (i.e. with `python this_script.py`)
if __name__ == '__main__':
    print('starting myHUB api...waiting')