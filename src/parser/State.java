package parser;

import static parser.TransitionOutput.GOTO_ACCEPT_ID;
import static parser.TransitionOutput.GOTO_ACCEPT_INT;
import static parser.TransitionOutput.GOTO_EOS;
import static parser.TransitionOutput.GOTO_FAILED;
import static parser.TransitionOutput.GOTO_MATCHED;
import static parser.TransitionOutput.GOTO_SHARP;
import static parser.TransitionOutput.GOTO_SIGN;
import static parser.TransitionOutput.GOTO_START;
import static parser.TokenType.FALSE;
import static parser.TokenType.INT;
import static parser.TokenType.MINUS;
import static parser.TokenType.PLUS;
import static parser.TokenType.TRUE;


enum State { // 상태를 비교한다. mDFA
	START { // 시작 상태
		@Override
		public TransitionOutput transit(ScanContext context) {
			Char ch = context.getCharStream().nextChar(); // context의 다음 char를 받는다.
			char v = ch.value();
			switch ( ch.type() ) { // TokenType을 받는다.
				case LETTER: // letter인 경우
					context.append(v);
					return GOTO_ACCEPT_ID; // ID형태로 이동
				case DIGIT: // Digit인 경우
					context.append(v);
					return GOTO_ACCEPT_INT; // INT형태로 이동
				case SPECIAL_CHAR: //special charactor가 들어온 경우 
					if (v == '+' || v == '-') { //부호인경우 상태반환
						context.append(v);
						return GOTO_SIGN;
					}
					else if (v == '#') {  //boolean인 경우 상태반환
						context.append(v);
						return GOTO_SHARP;
					}
					else { //그외에는 type을 알아내서 알맞은 상태로 반환
						if(TokenType.fromSpecialCharactor(v) != null) { // 위 함수가 null이 아닌 경우에

							context.append(v); // append 한 후
							return GOTO_MATCHED(TokenType.fromSpecialCharactor(v), context.getLexime()); // 해당 special char 형태를 알아 낸 다음 그 char과 함께 MATCHED 시킨다.
						}
					}
				case WS: // 공백일 경우
					return GOTO_START;
				case END_OF_STREAM: // Stream이 끝난 경우
					return GOTO_EOS;
				default:
					throw new AssertionError();
			}
		}
	},
	ACCEPT_ID { // ID 형태인 경우
		@Override
		public TransitionOutput transit(ScanContext context) {
			Char ch = context.getCharStream().nextChar();
			char v = ch.value();
			switch ( ch.type() ) {
				case LETTER:
				case DIGIT: // letter와 digit인 경우 accept
					context.append(v);
					return GOTO_ACCEPT_ID; // 다음 char을 동일한 형식으로 받는다.
				case SPECIAL_CHAR: // ID중간에 special char가 들어온 경우 Fail
					return GOTO_FAILED;
				case WS:
				case END_OF_STREAM: // 공백이거나 끝난 경우
					return GOTO_MATCHED(Token.ofName(context.getLexime()));
				default:
					throw new AssertionError();
			}
		}
	},
	ACCEPT_INT { // int형태
		@Override
		public TransitionOutput transit(ScanContext context) {
			Char ch = context.getCharStream().nextChar();
			switch ( ch.type() ) {
				case LETTER: // 중간에 letter가 들어온 경우 Fail
					return GOTO_FAILED;
				case DIGIT: // 계속해서 숫자가 들어오면 다음 char를 동일한 형태로 받는다
					context.append(ch.value());
					return GOTO_ACCEPT_INT;
				case SPECIAL_CHAR: // 중간에 Special char가 들어온 경우
					return GOTO_FAILED;
				case WS: // 공백인 경우 Int형을 반환
				case END_OF_STREAM:
					return GOTO_MATCHED(INT, context.getLexime());
				default:
					throw new AssertionError();
			}
		}
	},
	SHARP { // boolean (#)의 시작인 경우
		@Override
		public TransitionOutput transit(ScanContext context) {
			Char ch = context.getCharStream().nextChar();
			char v = ch.value();
			switch ( ch.type() ) {
				case LETTER: //
					switch ( v ) {
						case 'T': // #T인 경우 append 후 MATCHED
							context.append(v);
							return GOTO_MATCHED(TRUE, context.getLexime());
						case 'F': // #F인 경우 append 후 MATCHED
							context.append(v);
							return GOTO_MATCHED(FALSE, context.getLexime());
						default:
							return GOTO_FAILED;
					}
				default:
					return GOTO_FAILED;
			}
		}
	},
	SIGN { // 부호인 경우
		@Override
		public TransitionOutput transit(ScanContext context) {
			Char ch = context.getCharStream().nextChar();
			char v = ch.value();
			switch ( ch.type() ) {
				case LETTER: // letter가 들어온 경우 Fail
					return GOTO_FAILED;
				case DIGIT: // 숫자가 들어오면 GOTO_ACCEPT_INT로 간다.
					context.append(v);
					return GOTO_ACCEPT_INT;
				case SPECIAL_CHAR: // spcial char인 경우 Fail
					return GOTO_FAILED;
				case WS: // 공백인 경우
					String lexme = context.getLexime(); // lexime를 받는다.
					switch ( lexme ) { // +, - 인경우 각각 MATCHED 시켜준다.
						case "+":
							return GOTO_MATCHED(PLUS, lexme);
						case "-":
							return GOTO_MATCHED(MINUS, lexme);
						default:
							throw new AssertionError();
					}
				case END_OF_STREAM: // 끝인 경우 Fail
					return GOTO_FAILED;
				default:
					throw new AssertionError();
			}
		}
	},
	MATCHED {
		@Override
		public TransitionOutput transit(ScanContext context) {
			throw new IllegalStateException("at final state");
		}
	},
	FAILED{
		@Override
		public TransitionOutput transit(ScanContext context) {
			throw new IllegalStateException("at final state");
		}
	},
	EOS {
		@Override
		public TransitionOutput transit(ScanContext context) {
			return GOTO_EOS;
		}
	};
	
	abstract TransitionOutput transit(ScanContext context);
}
