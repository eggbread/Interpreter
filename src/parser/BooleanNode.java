package parser;

public class BooleanNode implements ValueNode {
    // False와 True를 BooleanNode로 받는다.
    public static BooleanNode FALSE_NODE = new BooleanNode(false);
    public static BooleanNode TRUE_NODE = new BooleanNode(true);
    Boolean value; // Boolean값의 value
    private BooleanNode(Boolean b) { // 생성자
        value = b;
    }
    @Override
    public String toString() { // true일 때와 false일 때
        return value ? "#T" : "#F";

    }
}
