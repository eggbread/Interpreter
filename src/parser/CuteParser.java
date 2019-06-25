package parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;

public class CuteParser {
    private Iterator<Token> tokens;
    private static Node END_OF_LIST = new Node() {
    };

    public CuteParser(String input) { // 파일 대신 String을 입력받는다.
        tokens = Scanner.scan(input);
    }

    private Token getNextToken() {
        if (!tokens.hasNext())
            return null;
        return tokens.next();
    }

    public Node parseExpr() {
        Token t = getNextToken();
        if (t == null) {
            System.out.println("No more token");
            return null;
        }
        TokenType tType = t.type();
        String tLexeme = t.lexme();
        switch (tType) {
            case ID:
                return new IdNode(tLexeme);
            case INT:
                if (tLexeme == null)
                    System.out.println("???");
                return new IntNode(tLexeme);
                //새로 구현된 IntNode
            case DIV:
            case EQ:
            case MINUS:
            case GT:
            case PLUS:
            case TIMES:
            case LT:
                BinaryOpNode biNode = new BinaryOpNode();
                biNode.binType = BinaryOpNode.BinType.getBinType(tType);
                return biNode; // BinaryOpNode에 대한 case를 받아서 반환한다.
            case ATOM_Q:
            case CAR:
            case CDR:
            case COND:
            case CONS:
            case DEFINE:
            case EQ_Q:
            case LAMBDA:
            case NOT:
            case NULL_Q:
                FunctionNode funcNode = new FunctionNode();
                funcNode.funcType = FunctionNode.FunctionType.getFuncType(tType);
                return funcNode; // FunctionNode에 대한 case를 받아서 반환한다.
            case FALSE:
                return BooleanNode.FALSE_NODE;
            case TRUE:
                return BooleanNode.TRUE_NODE; // 새로운 boolean 타입 반환
            // case L_PAREN 일 경우와 case R_PAREN 일 경우에 대해서 작성
            // L_PAREN 일 경우 parseExprList()를 호출하여 처리
            case L_PAREN:
                return parseExprList();
            case R_PAREN:
                return END_OF_LIST;
            case APOSTROPHE:
                QuoteNode quoteNode = new QuoteNode(parseExpr());
                ListNode listNode = ListNode.cons(quoteNode, ListNode.EMPTYLIST);
                return listNode;
                // '가 나오면 새로운 QuoteNode를 생성하여서 그 노드부터 나머지 리스트를 묶어 새로운
                // 리스트를 만들어서 반환해준다.
            case QUOTE:
                return new QuoteNode(parseExpr());
                //quote가 나오면 그 리스트를 반환
            default:
                // head 의 next 를 만들고 head 를 반환하도록 작성
                System.out.println("Parsing Error!");
                return null;
        }
    }

    private ListNode parseExprList() {
        Node head = parseExpr();
        if (head == null)
            return null;
        if (head == END_OF_LIST) // if next token is RPAREN
            return ListNode.EMPTYLIST;
        ListNode tail = parseExprList(); // 재귀적으로 리스트를 얻는다.
        if (tail == null)
            return null;
        return ListNode.cons(head, tail); // 새로운 리스트 반환
    }
}






