/* Generated By:JJTree: Do not edit this line. ASTAssignment.java Version 6.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public
class ASTAssignment extends SimpleNode {
  public ASTAssignment(int id) {
    super(id);
  }

  public ASTAssignment(JavaMMParser p, int id) {
    super(p, id);
  }

  public static boolean isIntegerAssignment(Node node) {
    return node instanceof ASTAssignment && node.jjtGetChild(1) instanceof ASTInteger;
  }

  public String getAssignedIdentifier() {
    if (this.children[0] instanceof ASTAccess)
      return this.children[0].jjtGetChild(0).toString();
    else return this.children[0].toString();
  }

  @Override
  public String generateCode() {
      // Array store
      if (this.children[0] instanceof ASTAccess) {
        StringBuilder childrenCode = new StringBuilder();
        childrenCode.append(this.children[0].jjtGetChild(0).generateCode());
        childrenCode.append(this.children[0].jjtGetChild(1).generateCode());
        childrenCode.append(this.children[1].generateCode());
        return childrenCode.toString() + "iastore\n";
      }

      String identifier = this.children[0].toString();
      VarSymbol local = this.symbolTable.getLocalVarSymbol(identifier);
      VarSymbol global = this.symbolTable.getVarSymbol(identifier);
      if (local != null)
        return this.generateStoreLocal(local);
      return this.generateStoreGlobal(global);
  }


  private String generateStoreLocal(VarSymbol symbol) {
    if (children[1] instanceof ASTAdd && children[1].jjtGetChild(0).toString().equals(children[0].toString())) {
      Node leftOperand = children[1].jjtGetChild(0);
      Node rightOperand = children[1].jjtGetChild(1);
      if (leftOperand.toString().equals(children[0].toString()) && rightOperand instanceof ASTInteger)
        return "iinc " + symbol.getOrder() + " " + rightOperand + "\n";
      if (rightOperand.toString().equals(children[0].toString()) && leftOperand instanceof ASTInteger)
        return "iinc " + symbol.getOrder() + " " + leftOperand + "\n";
    }

    int order = symbol.getOrder();
    String type = symbol.getVarType();

    String orderStr = order >= 0 && order <= 3 ? "_" + order : " " + order;
    String childrenCode = this.children[1].generateCode();

    switch (type) {
      case "int":
      case "boolean": return childrenCode + "istore" + orderStr + "\n";
      default: return childrenCode + "astore" + orderStr + "\n";
    }
  }

  private String generateStoreGlobal(VarSymbol symbol) {
    String thisCode = "aload_0\n";
    String childrenCode = this.children[1].generateCode();
    String storeCode = "putfield " + this.symbolTable.getClassName() + "/field_" + this.children[0] + " " + ASTType.generateCodeChar(symbol.getVarType()) + "\n";
    return thisCode + childrenCode + storeCode;
  }

  @Override
  public StackLimitValues calculateStackLimit(StackLimitValues slv){
    String varName = (this.children[0] instanceof ASTAccess ? this.children[0].jjtGetChild(0).toString() : this.children[0].toString());
    boolean isGlobalStore = this.symbolTable.getLocalVarSymbol(varName) == null;
    if (isGlobalStore) { // For the aload_0
      slv.incStack(1);
    }

    if (this.children[0] instanceof ASTAccess)
      slv.incStack(2);
    this.children[1].calculateStackLimit(slv);
    if(this.children[0] instanceof ASTAccess)
      slv.decStack(3);
    else if (isGlobalStore)
      slv.decStack(2);
    else slv.decStack(1);
    return slv;
  }

  @Override
  public Node constantPropagation(HashMap<String,Node> vm){ //if right side of assignment is a constant the name of the variable and a copy of its value is added to the constant variable hashmap
    super.constantPropagation(vm);
    Node variableNode = children[0];
    Node valueNode = children[1];
    if(variableNode instanceof ASTAccess)
      return this;

    if(valueNode instanceof ASTInteger) //if the variable is being assigned with a constant it gets added/replaces to the constant variable hashmap
      vm.put(variableNode.toString(),new ASTInteger((ASTInteger) valueNode));
    else if(valueNode instanceof ASTTrue)
      vm.put(variableNode.toString(),new ASTTrue());
    else if(valueNode instanceof ASTFalse)
      vm.put(variableNode.toString(),new ASTFalse());
    else if(vm.containsKey(variableNode.toString())) // if the variable is being assigned with a non constant it gets removed from the constant variable hashmap
      vm.remove(variableNode.toString());
    return this;
  }

  @Override
  public void whileAnalyser(Set<String> wa){  //adds variable to changed variables set
    super.whileAnalyser(wa);
    Node variableNode = children[0];
    wa.add(variableNode.toString());
  }

  public boolean isSemanticallyCorrect(HashMap<String, Integer> varInitTable) {
    if(!super.isSemanticallyCorrect(varInitTable))
      return false;
    Node[] children = this.jjtGetChildren();
    Node variableNode = children[0];
    Node valueNode = children[1];

    if(!variableNode.getType().equals(valueNode.getType())){
      System.err.println("Error: Assignment of different types. Can't assign " + valueNode.getType() + " to " + variableNode.getType() + " at line " + ((SimpleNode)variableNode).line + ", column " + ((SimpleNode)variableNode).column + ".");
      return false;
    }

    varInitTable.put(variableNode.toString(),2); //variable initialized -> 2

    return true;
  }

  public void fillGraph(HashMap<String, LiveNode> graph, List<LiveNode> activeList) {
    ((SimpleNode) this.children[1]).fillGraph(graph, activeList); //Adds used nodes to graph

    String varName = this.getAssignedIdentifier();
    boolean isLocal = this.symbolTable.getLocalVarSymbol(varName) != null;
    boolean isGlobal = this.symbolTable.getVarSymbol(varName) != null;
    LiveNode node = graph.get(varName);

    if(this.children[0] instanceof ASTAccess) {
      String arrayName = this.children[0].jjtGetChild(0).toString();
      LiveNode arrayNode = graph.get(arrayName);
      if(arrayNode == null) return;

      ((SimpleNode)this.children[0].jjtGetChild(1)).fillGraph(graph, activeList);
      ((SimpleNode)this.children[0].jjtGetChild(0)).fillGraph(graph, activeList);
    }
    else if(!isLocal && isGlobal) { //add "this" node to active list
      ((SimpleNode) this.children[0]).fillGraph(graph, activeList);
    }
    else if(node != null) activeList.add(node); //Inserts assigned node to list but doesn't create any edges

  }

}
/* JavaCC - OriginalChecksum=ebdcc8c80afc3db73e749a438d45e4c5 (do not edit this line) */