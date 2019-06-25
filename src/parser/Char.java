package parser;

class Char {
	private final char value;
	private final CharacterType type;

	enum CharacterType {
		LETTER, DIGIT, SPECIAL_CHAR, WS, END_OF_STREAM,
	}
	
	static Char of(char ch) { //새로운 Char타입의 객체 생성
		return new Char(ch, getType(ch));
	}
	
	static Char end() {
		return new Char(Character.MIN_VALUE, CharacterType.END_OF_STREAM);
	} // EOS 반환
	
	private Char(char ch, CharacterType type) { // 생성자
		this.value = ch;
		this.type = type;
	}
	
	char value() {
		return this.value;
	}
	
	CharacterType type() {
		return this.type;
	}
	
	private static CharacterType getType(char ch) {
		int code = (int)ch;
		if ( Character.isLetter(ch) || ch=='?') { //null? 와 같은 단어도 letter로 반환되야함
			return CharacterType.LETTER;
		}
		
		if ( Character.isDigit(ch) ) { // 숫자 일 경우
			return CharacterType.DIGIT;
		}
		
		switch ( ch ) { // Special Char 인 경우
			case '-': case '+': case '*': case '/':
			case '(': case ')':
			case '<': case '=': case '>':
			case '#': case '\'':
				return CharacterType.SPECIAL_CHAR;
		}
		
		if ( Character.isWhitespace(ch) ) { // 공백인 경우
			return CharacterType.WS;
		}
		
		throw new IllegalArgumentException("input=" + ch); // 예외 처리
	}
}
