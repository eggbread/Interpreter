package parser;

import java.util.Objects;

public class IdNode implements ValueNode {
    // 새로 수정된 IdNode Class
    private String idString; // String값을 value로 갖는다.
    public IdNode(String text) { // 생성자
        idString = text;
    }
    @Override
    public boolean equals(Object o){ // 값이 같은지 비교한다.
        if(this == o) return true;
        if(!(o instanceof IdNode)) return false;
        IdNode idNode = (IdNode)o;
        return Objects.equals(idString, idNode.idString);
    }
    @Override
    public String toString(){
        return idString;
    }
}
