/* Generated By:JJTree: Do not edit this line. ASTTrue.java Version 6.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class ASTTrue extends SimpleNode {
  public ASTTrue(int id) {
    super(id);
  }

  public ASTTrue(JavaMMParser p, int id) {
    super(p, id);
  }

  public ASTTrue() {
    super(JavaMMParserTreeConstants.JJTTRUE);
  }

  @Override
  public String getType() {
    return "boolean";
  }

  @Override
  public String generateCode() {
    return "iconst_1\n";
  }

  @Override
  public StackLimitValues calculateStackLimit(StackLimitValues slv){
    return slv.incStack(1);
  }

}
/* JavaCC - OriginalChecksum=323c5ea191a757098ca0814864290d2b (do not edit this line) */
