package parser;

import java.io.*;

class CharStream {
	private final Reader reader;
	private Character cache;
	
	static CharStream from(String input) { // 파일을 stream 형태로 받는다
		// input을 String으로 받는다 Reader를 StringReader로 생성한다.
		return new CharStream(new StringReader(input));
	}
	
	CharStream(Reader reader) { // 생성자
		this.reader = reader;
		this.cache = null;
	}
	
	Char nextChar() { // 다음 단어를 가져온다
		if ( cache != null ) {
			char ch = cache;
			cache = null;
			
			return Char.of(ch); // 현재 있는 값을 리턴하면서 캐쉬를 비운다
		}
		else {
			try { // reader에 있는 정보를 가져온다.
				int ch = reader.read();
				if ( ch == -1 ) { // -1이면 end 반환
					return Char.end();
				}
				else { // 끝이 아니면 그 값을 반환한다.
					return Char.of((char)ch);
				}
			}
			catch ( IOException e ) {
				throw new ScannerException("" + e);
			}
		}
	}
	
	void pushBack(char ch) {
		cache = ch;
	}
}
