package parser;

public class QuoteNode implements Node {
    // 새로 추가된 QuoteNode Class
    Node quoted;
    public QuoteNode(Node quoted) { // 생성자
        this.quoted = quoted;
    }
    @Override
    public String toString(){
        return quoted.toString();
    }
    public Node nodeInside() { // 어떤 노드를 가지고 있는지 보여줌
        // TODO Auto-generated method stub
        return quoted;
    }
}
