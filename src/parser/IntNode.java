package parser;

import java.util.Objects;

public class IntNode implements ValueNode {
    // 새로 수정된 IntNode
    private Integer value; // int 값을 value로 갖는다.
    @Override
    public String toString(){
        return value.toString();
    }
    public IntNode(String text) { // 생성자
        this.value = new Integer(text);
    }
    public Integer getValue(){
        return value;
    }

    @Override
    public boolean equals(Object o){ // 값이 같은지 비교
        if(this == o) return true;
        if(!(o instanceof IntNode)) return false;
        IntNode intNode = (IntNode) o;
        return Objects.equals(value, intNode.value);
    }
}
