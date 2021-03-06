/* Generated By:JJTree: Do not edit this line. ASTArrayInit.java Version 6.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
import java.util.HashMap;
public
class ASTArrayInit extends SimpleNode {
  public ASTArrayInit(int id) {
    super(id);
  }

  public ASTArrayInit(JavaMMParser p, int id) {
    super(p, id);
  }

  @Override
  public String getType() {
    return "int[]";
  }

  @Override
  public String generateCode() {
    return super.generateCode() + "newarray int\n";
  }

  @Override
  public StackLimitValues calculateStackLimit(StackLimitValues slv){
    return super.calculateStackLimit(slv);
  }

  @Override
  public boolean isSemanticallyCorrect(HashMap<String, Integer> varInitTable) {
    if(!super.isSemanticallyCorrect(varInitTable))
      return false;
    Node[] children = this.jjtGetChildren();
    Node indexNode = children[0];

    if(!indexNode.getType().equals("int")){
      System.err.println("Array size must be an int. Found: " + indexNode.getType() + " at line " + ((SimpleNode)indexNode).line + ", column " + ((SimpleNode)indexNode).column + ".");
      return false;
    }
    return true;
  }
}
/* JavaCC - OriginalChecksum=03a21417967e93be6f52300d7e55a953 (do not edit this line) */
