package parser;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import static parser.BooleanNode.FALSE_NODE;
import static parser.BooleanNode.TRUE_NODE;


public class CuteInterpreter {

    static HashMap<String, Node> definedHashMap;// define을 저장 할 해쉬 맵
    // IdNode의 string 값을 저장하고 value에는 Node 자체를 갖는다.

    public static void main(String[] args) {
        definedHashMap = new HashMap<>();
        Scanner scan = new Scanner(System.in);
        while (true) {
            System.out.print("> ");
            String input = scan.nextLine(); // 스캔 시작
            if (input.equals("exit")) { // exit를 입력하면 종료
                break;
            }
            CuteParser cuteParser = new CuteParser(input); // 스캔 받은 string으로 parser를 만든다.
            CuteInterpreter interpreter = new CuteInterpreter();
            Node parseTree = cuteParser.parseExpr();
            Node resultNode = interpreter.runExpr(parseTree);
            NodePrinter nodePrinter = new NodePrinter(resultNode);
            nodePrinter.prettyPrint(); // print시작
        }
    }

    private void errorLog(String err) {
        System.out.println(err);
    }

    public Node runExpr(Node rootExpr) { // rootNode를 가지고 list이면 runList를 호출한다. 이외에는 형태에 맞게 반환
        if (rootExpr == null) return null;
        if (rootExpr instanceof IdNode) { // IdNode 일 때는 해쉬맵을 비교해야 한다.
            if (this.definedHashMap.containsKey(rootExpr.toString())) { // 만약 키값을 가지고 있으면
                return this.definedHashMap.get(rootExpr.toString()); // value를 가져와야하는데 function이나 binaryOP가 나왔을 때 계산하기 위하여
                // runExpr을 호출해서 반환하였다.
            } else return rootExpr;
        } else if (rootExpr instanceof IntNode) return rootExpr;
        else if (rootExpr instanceof BooleanNode) return rootExpr;
        else if (rootExpr instanceof ListNode) return runList((ListNode) rootExpr);
        else if (rootExpr instanceof  BinaryOpNode) return rootExpr;
        else if(rootExpr instanceof FunctionNode)return rootExpr;
        else errorLog("run Expr error");
        return null;
    }

    private Node runList(ListNode list) { // list일 때 function인지 binaryOp인지 확인 한 후 함수를 초훌한다.
        if (list.equals(ListNode.EMPTYLIST))
            return list;
        if (list.car() instanceof FunctionNode) {
            return runFunction((FunctionNode) list.car(), (ListNode) stripList(list.cdr()));
            //cdr자체가 list이므로 stripList를 통해서 한개의 리스트를 벗겨내야한다.
        }
        if (list.car() instanceof BinaryOpNode) {
            return runBinary(list);
        }
        if (list.car() instanceof IdNode) {
            if(definedHashMap.containsKey(list.car().toString())) {//define됐는지 확인
                if(definedHashMap.get(list.car().toString()) instanceof BinaryOpNode) {//define된 것이 Binary인지
                    list = ListNode.cons(runExpr(list.car()), list.cdr());//define된 Node로 만든다
                    return runBinary(list);//실행
                } else if(definedHashMap.get(list.car().toString()) instanceof FunctionNode) {//function인지 확인
                    if(list.cdr().cdr() == ListNode.EMPTYLIST){//NOT이나 null?처럼 하나의 입력만 받는 경우
                        list = ListNode.cons(runExpr(list.car()),list.cdr());//define된 Node로 만든다
                        if( list.car().toString() == "NOT"){//NOT명령어라면 NOT은 Tail이 LISTNode가 아닌 ValueNode일 수 있다
                            if(list.cdr().car() instanceof  ListNode){//대상이 List인지 확인
                                list = ListNode.cons(runExpr(list.car()), (ListNode) list.cdr().car());//맞다면 Tail의 head로 한 단계 더 내려간다.
                            }
                            return runFunction((FunctionNode)list.car(), list.cdr());//실행
                        } else{//그외 명령어인 경우
                            return runFunction((FunctionNode)runExpr(list.car()), (ListNode) list.cdr().car());//실행
                        }
                    }//두개의 입력을 받는 경우
                    list = ListNode.cons(runExpr(list.car()),list.cdr());//define된 Node로 만든다.
                    return runFunction((FunctionNode) list.car(), list.cdr());//실행
                }
                else{//lambda가 define됐을 때
                    list=ListNode.cons(runExpr(list.car()),list.cdr());
                    return runExpr(list);//실행
                }
            }else{//define 안됐을 때
                return runExpr(list.car());
            }
        }
        if (list.car() instanceof ListNode) {
            if (((ListNode) list.car()).car() instanceof FunctionNode) {
                if (((FunctionNode) ((ListNode) list.car()).car()).funcType == FunctionNode.FunctionType.LAMBDA) { // 람다일 경우 실행
                    return runExpr(make_lambdaHashMap(list));
                }
            }
            return runExpr(list.car());
        }
        return list;
    }

    private ListNode make_lambdaHashMap(ListNode list) { // 람다에서 사용하는 변수와 해당 값을 맵핑
        HashMap<String, Node> lambdaHashMap = new HashMap<>();  // 리턴할 해쉬 맵 생성
        LinkedList<String> StringList = new LinkedList<>(); // 변수를 저장할 리스트
        ListNode number_temp = list.cdr(); // 변수의 값이 적혀있는 첫 부분
        ListNode String_temp = ((ListNode) ((ListNode) list.car()).cdr().car()); // 변수의 종류들을 가지고 있는 list
        ListNode before = ((ListNode) ((ListNode) list.car()).cdr().cdr().car()); // 입력받은 람다 식
        ListNode after; // 입력받은 람다식에 변수 대신 해당 값으로 치환하여 만든 식
        while (String_temp != ListNode.EMPTYLIST) { // 입력된 변수들을 순서대로 list에 저장
            StringList.addLast(((IdNode) String_temp.car()).toString());
            String_temp = String_temp.cdr();
        }
        while (number_temp != ListNode.EMPTYLIST) { // 입력된 변수 값과 list에 저장된 순서대로 맵핑
            lambdaHashMap.put(StringList.pop(), number_temp.car());
            number_temp = number_temp.cdr();
        }
        after = make(lambdaHashMap, before); // 입력된 람다식에 변수를 값으로 치환시켜주는 메소드
        return after;
    }

    private ListNode make(HashMap<String, Node> hashmap, ListNode node) { // 입력된 람다식에 변수를 값으로 치환시켜주는 메소드
        if (node == ListNode.EMPTYLIST) { // 제귀의 함수의 exit case
            return ListNode.EMPTYLIST;
        } else {
            ListNode result = make(hashmap, node.cdr()); // 제귀 호출
            if(node.car() instanceof ListNode){ // 중첩 리스트일 경우 리스트 안의 내용도 치환
                ListNode inner_result = make(hashmap, (ListNode)node.car());
                result = ListNode.cons(inner_result, result);
            }
            else {
                if (hashmap.containsKey(node.car().toString())) { // 현재 입력된 node의 car()값이 람다의 변수일 경우 값으로 치환
                    result = ListNode.cons(hashmap.get(node.car().toString()), result);
                } else { // 현재 입력된 node의 car()값이 람다의 변수가 아니면 그대로 입력
                    result = ListNode.cons(node.car(), result);
                }
            }
            return result;
        }
    }



    private Node runFunction(FunctionNode operator, ListNode operand) { // 만약 functionNode일 경우
        switch (operator.funcType) { // 어떤 functionType인지 검사한다.
            case CAR: // 리스트의 head를 반환한다.
                Node rightSideOfOperandForCar = runExpr(operand); // operand를 가지고 runExpr을 호출한다.
                if (rightSideOfOperandForCar instanceof ListNode && ((ListNode) rightSideOfOperandForCar).car() instanceof QuoteNode) {
                    // 만약 runExpr의 결과값이 리스트노드이고 안에 quote로 시작한다면
                    rightSideOfOperandForCar = runQuote((ListNode) rightSideOfOperandForCar); // quote를 벗겨낸다.
                    if (((ListNode) rightSideOfOperandForCar).car() instanceof ListNode || ((ListNode) rightSideOfOperandForCar).car() instanceof BinaryOpNode
                            || ((ListNode) rightSideOfOperandForCar).car() instanceof IdNode || ((ListNode) rightSideOfOperandForCar).car() instanceof FunctionNode) {
                        // 중첩된 리스트에 타입이 리스트 해당 조건문에 있는 경우이면
                        return ListNode.cons(new QuoteNode(((ListNode) rightSideOfOperandForCar).car()), ListNode.EMPTYLIST);
                        //새로운 quote노드를 만들어서 cons함수를 호출하여 하나의 리스트를 생성해서 반환한다.
                    }
                }
                return ((ListNode) rightSideOfOperandForCar).car(); // 이외에는 단순히 car()를 반환하면 된다.
            case CDR: // 리스트의 tail을 반환한다.
                Node rightSideOfOperandForCdr = runExpr(operand); // runExpr실행
                if (rightSideOfOperandForCdr instanceof ListNode && ((ListNode) rightSideOfOperandForCdr).car() instanceof QuoteNode) { // quote 벗겨내기
                    rightSideOfOperandForCdr = runQuote((ListNode) rightSideOfOperandForCdr);
                }

                return ListNode.cons(new QuoteNode(((ListNode) rightSideOfOperandForCdr).cdr()), ListNode.EMPTYLIST); // cons를 통해서 위와 같이 새로운 리스트로 반환
            case CONS: // 리스트를 합친다.
                Node leftSideOfOperandForCons = runExpr(operand.car()); // operand.car()은 피연산자의 car를 나타냄
                Node rightSideOfOperandForCons = runExpr(operand.cdr().car()); // operand.cdr().car()은 피연산자의 car 옆의 원소를 나타냄
                // 두 노드의 처음 원소가 quote일 때 벗겨준다.
                if (leftSideOfOperandForCons instanceof ListNode && ((ListNode) leftSideOfOperandForCons).car() instanceof QuoteNode) {
                    leftSideOfOperandForCons = runQuote((ListNode) leftSideOfOperandForCons);
                }
                if (rightSideOfOperandForCons instanceof ListNode && ((ListNode) rightSideOfOperandForCons).car() instanceof QuoteNode) {
                    rightSideOfOperandForCons = runQuote((ListNode) rightSideOfOperandForCons);
                }
                // cons로 결합하는데 왼쪽을 left와 right를 묶어서 하나의 quoteNode로 만들고 오른쪽을 EmptyList로 만든다.
                return ListNode.cons(new QuoteNode(ListNode.cons(leftSideOfOperandForCons, (ListNode) rightSideOfOperandForCons)), ListNode.EMPTYLIST);

            case NULL_Q: // null인지 확인
                Node rightSideOfOperandForNull = runExpr(operand); // 피연산자를 runExpr을 호출한다.
                if (((ListNode) rightSideOfOperandForNull).car() instanceof QuoteNode) { // quote 벗겨내기
                    rightSideOfOperandForNull = runQuote((ListNode) rightSideOfOperandForNull);
                }
                if (rightSideOfOperandForNull == ListNode.EMPTYLIST) { // 만약 emptyList이면 true
                    return TRUE_NODE;
                }
                return FALSE_NODE; //이외에는 false
            case ATOM_Q: //원소인지 확인
                Node rightSideOfOperandForAtom = runExpr(operand);
                if (rightSideOfOperandForAtom instanceof ListNode && ((ListNode) rightSideOfOperandForAtom).car() instanceof QuoteNode) {
                    rightSideOfOperandForAtom = runQuote((ListNode) rightSideOfOperandForAtom);
                }
                if (rightSideOfOperandForAtom instanceof ListNode) { // 만약 quote를 벗겨낸 노드가 리스트 노드이면
                    if (rightSideOfOperandForAtom == ListNode.EMPTYLIST) { // emptyList인지 검사를 한다.
                        return TRUE_NODE;
                    } else return FALSE_NODE;
                } else return TRUE_NODE; // 리스트가 아닌 경우에는 TRUE
            case EQ_Q: // 왼쪽 오른쪽이 같은지 검사한다.
                Node first = runExpr(operand.car());
                Node second = runExpr(operand.cdr().car()); // runExpr을 통해서 왼쪽 오른쪽을 호출한다.
                //왼쪽 quote일 때 벗겨내기
                if (first instanceof ListNode) {
                    if (((ListNode) first).car() instanceof QuoteNode) {
                        first = runQuote((ListNode) first);
                    }
                }
                //오른쪽 quote일 때 벗겨내기
                if (second instanceof ListNode) {
                    if (((ListNode) second).car() instanceof QuoteNode) {
                        second = runQuote((ListNode) second);
                    }
                }
                // 최종 결과가 같은지 비교해서 알맞게 리턴한다.
                return first.toString().equals(second.toString()) ? TRUE_NODE : FALSE_NODE;

            case COND: // 조건문
                Node leftSideOfOperandForCond = runExpr(operand.car());
                // 조건문이 quote 인 경우에는 안에 값과 상관없이 TRUE가 반환되도록 한다.
                // cond안에 quote가 오는 경우는 car의 car 또는 car의 car의 car인 경우밖에 없다.
                // car의 car가 quote인 경우
                if (operand.car() instanceof ListNode) {
                    if (((ListNode) operand.car()).car() instanceof QuoteNode) {
                        if (operand.cdr().car() instanceof ListNode) { // 만약 #T일 때 반환해야하는 값이 리스트 일 때
                            return runList((ListNode) operand.cdr().car()); // 리스트를 실행한 후 반환한다.
                        } else return operand.cdr().car(); // 리스트가 아닌 단순 노드일 때는 그대로 반환
                        //car의 car의 car가 quote인 경우
                    } else if (((ListNode) operand.car()).car() instanceof ListNode
                            && ((ListNode) ((ListNode) operand.car()).car()).car() instanceof QuoteNode) {
                        return runExpr(((ListNode) leftSideOfOperandForCond).cdr().car()); // 오른쪽 값을 계산하여 리턴한다.
                    }
                }
                if (leftSideOfOperandForCond instanceof ListNode && ((ListNode) leftSideOfOperandForCond).car() instanceof QuoteNode) {
                    leftSideOfOperandForCond = runQuote(((ListNode) leftSideOfOperandForCond));
                }
                if (leftSideOfOperandForCond instanceof BooleanNode) { // 만약 단순히 booleanNode가 나온경우
                    if (operand.cdr().car() instanceof ListNode) { // 리턴해야 할 값이 ListNode이면 계산을 한 후 리턴한다.
                        return leftSideOfOperandForCond == TRUE_NODE ?
                                runList((ListNode) operand.cdr().car()) : runFunction(operator, operand.cdr());
                    }
                    // ListNode가 아닌경우 단순히 리턴한다.
                    else return leftSideOfOperandForCond == TRUE_NODE ?
                            operand.cdr().car() : runFunction(operator, operand.cdr());
                }
                if (((ListNode) leftSideOfOperandForCond).car() instanceof BooleanNode) { // 단순히 boolean이 아니라 안의 값이 booleanNode인 경우 위와 비슷하게 처리
                    return ((ListNode) leftSideOfOperandForCond).car() == TRUE_NODE ?
                            runExpr(((ListNode) leftSideOfOperandForCond).cdr().car()) : runFunction(operator, operand.cdr());
                } else {
                    return runList((ListNode) ((ListNode) leftSideOfOperandForCond).car()) == TRUE_NODE ?
                            runExpr(((ListNode) leftSideOfOperandForCond).cdr().car()) : runFunction(operator, operand.cdr());
                }
            case NOT: // BooleanNode를 반대로 리턴
                Node rightSideOfOperandForNot = runExpr(operand);
                if (rightSideOfOperandForNot instanceof ListNode && ((ListNode) rightSideOfOperandForNot).car() instanceof QuoteNode) {
                    rightSideOfOperandForNot = runQuote((ListNode) rightSideOfOperandForNot);
                }
                if (rightSideOfOperandForNot instanceof BooleanNode) { // 만약 단순히 booleanNode이면 반대로 리턴
                    return rightSideOfOperandForNot == TRUE_NODE ? FALSE_NODE : TRUE_NODE;
                } else if (operand.car() instanceof QuoteNode || operand.car() instanceof IntNode) { // Quote이거나 IntNode이면 TRUE이기 때문에 반대로 FALSE 리턴
                    return FALSE_NODE;
                } else { // 나머지 경우에는 car를 비교한다.
                    return ((ListNode) rightSideOfOperandForNot).car() == TRUE_NODE ? FALSE_NODE : TRUE_NODE;
                }
            case LAMBDA: // 함수 정의를 하는 경우 실행
                return ListNode.cons(operator, operand); // 입력된 operator와 operand를 붙혀서 리턴
            case DEFINE:
                Node id = operand.car();
                if (this.definedHashMap.containsKey(id.toString())) {
                    // 만약 a를 3 으로 define 한 후 ( define a 1 )이 들어왔을 때 runExpr을 먼저 호출하면
                    // 그 값을 가져오게 되서 ( define 3 1 ) 과 같이 오류가 나는 현상을 막기 위해 삭제하고 넣어 주었다.
                    this.definedHashMap.remove(id.toString());
                }
                Node leftSideOfOperandForDefine = runExpr(operand.car());
                Node rightSideOfOperandForDefine = runExpr(operand.cdr().car()); // idNode와 오른쪽 리스트의 결과를 가져온다.
                this.definedHashMap.put(leftSideOfOperandForDefine.toString(),rightSideOfOperandForDefine);

            default:
                break;
        }
        return null;
    }


    private Node stripList(ListNode node) { // 리스트를 벗겨낸다.
        if (node.car() instanceof ListNode && node.cdr() == ListNode.EMPTYLIST) {
            Node listNode = node.car();
            return listNode;
        } else {
            return node;
        }
    }

    private Node runBinary(ListNode list) { // 연산 수행
        BinaryOpNode operator = (BinaryOpNode) list.car(); // 연산자를 가져온다.
        Node leftSideOfOperand = runExpr(list.cdr().car());
        Node rightSideOfOperand = runExpr(list.cdr().cdr().car()); // 피연산자 두 개를 runExpr을 통해서 가져온다.
        switch (operator.binType) { // 연산자가 무엇인지 확인한다.
            // 연산자에 맞게 연산 후 반환해준다.
            case PLUS:
                return new IntNode(String.valueOf(((IntNode) leftSideOfOperand).getValue() + ((IntNode) rightSideOfOperand).getValue()));
            case MINUS:
                return new IntNode(String.valueOf(((IntNode) leftSideOfOperand).getValue() - ((IntNode) rightSideOfOperand).getValue()));
            case TIMES:
                return new IntNode(String.valueOf(((IntNode) leftSideOfOperand).getValue() * ((IntNode) rightSideOfOperand).getValue()));
            case DIV:
                return new IntNode(String.valueOf(((IntNode) leftSideOfOperand).getValue() / ((IntNode) rightSideOfOperand).getValue()));
            case LT:
                return ((IntNode) leftSideOfOperand).getValue() < ((IntNode) rightSideOfOperand).getValue() ? TRUE_NODE : FALSE_NODE;
            case GT:
                return ((IntNode) leftSideOfOperand).getValue() > ((IntNode) rightSideOfOperand).getValue() ? TRUE_NODE : FALSE_NODE;
            case EQ:
                return ((IntNode) leftSideOfOperand).getValue() == ((IntNode) rightSideOfOperand).getValue() ? TRUE_NODE : FALSE_NODE;
            default:
                break;
        }
        return null;
    }

    private Node runQuote(ListNode node) { // quote노드의 안을 본다.
        return ((QuoteNode) node.car()).nodeInside();
    }

}