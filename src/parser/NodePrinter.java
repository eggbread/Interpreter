package parser;



public class NodePrinter {

    private StringBuffer sb = new StringBuffer();
    private Node root;

    public NodePrinter(Node root) { // 생성자
        this.root = root;
    }

    // ListNode, QuoteNode, Node에 대한 printNode 함수를 각각 overload 형식으로 작성
    private void printList(ListNode listNode) { // 리스트 노드의 출력
        if (listNode == ListNode.EMPTYLIST) { // 비어있는 리스트 일 때
            return;
        }
        if (listNode.car() instanceof QuoteNode) { // 만약 리스트 안의 첫 원소가 quote일 경우
            printNode((QuoteNode) listNode.car()); // quote형식으로 출력
        } else { // quote가 아닌경우 형식에 맞게 함수 호출
            printNode(listNode.car());
        }

            printList(listNode.cdr());
        }


    private void printNode(QuoteNode quoteNode) { // quote일 경우
        if (quoteNode.nodeInside() == null) {
            return;
        } else {
            sb.append("'"); // ' 출력
            printNode(quoteNode.nodeInside());
        }
    }

    private void printNode(Node node) { // quote와 list가 아닐 경우, 처음 호출한 경우
        if (node == null)
            return;
        else if (node instanceof QuoteNode) { // 타입 검사를 실행 후 알맞은 함수 호출
            printNode((QuoteNode) node);
        } else if (node instanceof ListNode) {
            //sb.append("'"); // ' 출력
            if (((ListNode) node).car() instanceof QuoteNode) { // 리스트 안의 첫 노드가 quote인 경우
                printList(((ListNode) node)); // 바로 quote 출력 함수 호출
            } else { // 아닌 경우 리스트 형태로 append
                sb.append("( ");
                printList(((ListNode) node));
                sb.append(") ");
            }
        } else { // 이 외의 노드 바로 append
            sb.append(node + " ");
        }
    }

    public void prettyPrint() {
        printNode(root);
        System.out.println("... " + sb.toString()); // sb를 파일에 출력이 아닌 콘솔창에 출력한다.
    }
}
