package parser;

import java.util.HashMap;
import java.util.Map;

public class BinaryOpNode implements ValueNode {
    public enum BinType { // 각 타입 별 token 반환
        MINUS { TokenType tokenType() {return TokenType.MINUS;} },
        PLUS { TokenType tokenType() {return TokenType.PLUS;} },
        TIMES { TokenType tokenType() {return TokenType.TIMES;} },
        DIV { TokenType tokenType() {return TokenType.DIV;} },
        LT { TokenType tokenType() {return TokenType.LT;} },
        GT { TokenType tokenType() {return TokenType.GT;} },
        EQ { TokenType tokenType() {return TokenType.EQ;} };
        private static Map<TokenType, BinType> fromTokenType = new HashMap<TokenType, BinType>(); // 각 type을 한 map으로 갖도록 선언
        static { // map 초기화
            for (BinType bType : BinType.values()){
                fromTokenType.put(bType.tokenType(), bType);
            }
        }
        static BinType getBinType(TokenType tType){
            return fromTokenType.get(tType);
        } // TokenType으로 BinType을 얻는다
        abstract TokenType tokenType();
    }
    public BinType binType; // field
    public void setValue(TokenType tType){ // Setter
        BinType bType = BinType.getBinType(tType);
        binType = bType;
    }

    @Override
    public String toString(){
        return binType.name();
    }

}
