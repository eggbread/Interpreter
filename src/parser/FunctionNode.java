package parser;

import java.util.HashMap;
import java.util.Map;

public class FunctionNode implements ValueNode {
    public enum FunctionType { // 해당 Item을 하나의 타입으로 지정
        ATOM_Q {TokenType tokenType() {return TokenType.ATOM_Q;}},
        CAR {TokenType tokenType() {return TokenType.CAR;}},
        CDR {TokenType tokenType() {return TokenType.CDR;}},
        COND {TokenType tokenType() {return TokenType.COND;}},
        CONS {TokenType tokenType() {return TokenType.CONS;}},
        DEFINE {TokenType tokenType() {return TokenType.DEFINE;}},
        EQ_Q {TokenType tokenType() {return TokenType.EQ_Q;}},
        LAMBDA {TokenType tokenType() {return TokenType.LAMBDA;}},
        NOT {TokenType tokenType() {return TokenType.NOT;}},
        NULL_Q {TokenType tokenType() {return TokenType.NULL_Q;}};

        private static Map<TokenType, FunctionType> fromTokenType = new HashMap<TokenType,
                FunctionType>(); // HashMap 지정

        static {
            for(FunctionType fType : FunctionType.values()){
                fromTokenType.put(fType.tokenType(), fType);
            }
        }

        static FunctionType getFuncType(TokenType fType){
            return fromTokenType.get(fType);
        }

        abstract TokenType tokenType();

    }

    public FunctionType funcType;


    @Override
    public String toString(){
        return funcType.name();
    }
    public void setValue(TokenType tType){
        FunctionType fType = FunctionType.getFuncType(tType);
        funcType = fType;
    }
}
