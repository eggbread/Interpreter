package parser;

import java.util.HashMap;
import java.util.Map;


public class Token {
	private final TokenType type;
	private final String lexme;
	
	static Token ofName(String lexme) {
		TokenType type = KEYWORDS.get(lexme);
		if ( type != null ) { // null이 아닌 경우에 생성
			return new Token(type, lexme);
		}
		else if ( lexme.endsWith("?") ) { // ?로 끝나면
			if ( lexme.substring(0, lexme.length()-1).contains("?") ) { // ? 전까지의 subString을 검사 ( ? 가 중복되는지)
				throw new ScannerException("invalid ID=" + lexme);
			}
			
			return new Token(TokenType.QUESTION, lexme); // ?가 하나만 있을 경우 QUESTION으로 반환
		}
		else if ( lexme.contains("?") ) { // 이외의 자리에 ?가 있는 경우
			throw new ScannerException("invalid ID=" + lexme);
		}
		else { // 이외의 경우에는 ID로 인식한다.
			return new Token(TokenType.ID, lexme);
		}
	}
	
	Token(TokenType type, String lexme) { // 생성자
		this.type = type;
		this.lexme = lexme;
	}
	
	public TokenType type() {
		return this.type;
	}
	
	public String lexme() {
		return this.lexme;
	}
	
	@Override
	public String toString() {
		return String.format("%s(%s)", type, lexme);
	}
	
	private static final Map<String,TokenType> KEYWORDS = new HashMap<>();
	static { // 각 KEYWORDS를 정의한다.
		KEYWORDS.put("define", TokenType.DEFINE);
		KEYWORDS.put("lambda", TokenType.LAMBDA);
		KEYWORDS.put("cond", TokenType.COND);
		KEYWORDS.put("quote", TokenType.QUOTE);
		KEYWORDS.put("not", TokenType.NOT);
		KEYWORDS.put("cdr", TokenType.CDR);
		KEYWORDS.put("car", TokenType.CAR);
		KEYWORDS.put("cons", TokenType.CONS);
		KEYWORDS.put("eq?", TokenType.EQ_Q);
		KEYWORDS.put("null?", TokenType.NULL_Q);
		KEYWORDS.put("atom?", TokenType.ATOM_Q);
	}
}
