package parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Scanner {
    // return tokens as an Iterator
    public static Iterator<Token> scan(String input) { // 파일을 ScanContext 형식으로 만들고 Iterator 형식으로 반환한다.
        // input을 String으로 받는다
        ScanContext context = new ScanContext(input);
        return new TokenIterator(context);
    }

    // return tokens as a Stream 
    public static Stream<Token> stream(String input) { // 반대로 Iterator 형식을 stream으로 반환한다.
        // input을 String으로 받는다
        Iterator<Token> tokens = scan(input);
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(tokens, Spliterator.ORDERED), false);
    }
}