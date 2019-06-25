package parser;

import java.io.File;
import java.io.FileNotFoundException;


class ScanContext { // 문맥을 scan한다
	private final CharStream input;
	private StringBuilder builder;
	
	ScanContext(String input) { // 생성자
		// input을 String으로 받는다
		this.input = CharStream.from(input);
		this.builder = new StringBuilder();
	}
	
	CharStream getCharStream() {
		return input;
	}
	
	String getLexime() { // builder에 있는 정보를 string 형태로 받는다.
		String str = builder.toString();
		builder.setLength(0);
		return str;
	}
	
	void append(char ch) {
		builder.append(ch);
	}
}
