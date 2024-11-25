package cup.example;

public class ParserWithTree extends Parser {
	public ParserWithTree() { super(); }
	
	protected MultiTreeNode createDeclarationNode(MultiTreeNode declaration) {
		MultiTreeNode newNode = new MultiTreeNode("Declaration");
		newNode.addChild(declaration);
		
		return newNode;
	}
	
	protected MultiTreeNode createReturnNode(MultiTreeNode expr) {
	    MultiTreeNode returnNode = new MultiTreeNode("Return");
	    
	    if (expr != null) {
	        returnNode.addChild(expr);
	    }
	    
	    return returnNode;
	}

	
	protected MultiTreeNode createFunctionDeclarationNode(MultiTreeNode typeSpecifier, String identifierName, MultiTreeNode paramsList, MultiTreeNode compoundStatement) {
	    MultiTreeNode newNode = new MultiTreeNode("FunctionDeclaration", identifierName);
	    newNode.addChild(typeSpecifier);

	    if (paramsList != null && paramsList.getChildren().length > 0) {
	        newNode.addChild(paramsList);
	    }

	    if (compoundStatement != null) {
	        newNode.addChild(compoundStatement);
	    }

	    return newNode;
	}
	
	protected MultiTreeNode createFunctionCallNode(String functionName, MultiTreeNode params) {
	    MultiTreeNode callNode = new MultiTreeNode("FunctionCall", functionName);
	    callNode.addChild(params);
	    
	    return callNode;
	}
	
	protected MultiTreeNode createEmptyParamsList() {
	    return new MultiTreeNode("CallParamsList");
	}
	
	protected MultiTreeNode createTypeSpecifier(String typeName) { 
  		MultiTreeNode newNode = new MultiTreeNode("TypeSpecifier", typeName);
  		
  		return newNode;
  	}
	
	protected MultiTreeNode createListNode(String listName, MultiTreeNode firstChild) {
		MultiTreeNode newNode = new MultiTreeNode(listName);
		newNode.addChild(firstChild);
		
		return newNode;
	}
	
	protected MultiTreeNode createVarDeclaration(MultiTreeNode typeSpecifier, String identifierName, Integer value) {
		MultiTreeNode newNode = new MultiTreeNode("VarDeclaration", identifierName);
		
		newNode.addChild(typeSpecifier);		
		newNode.addChild(new MultiTreeNode("IntValue", "" + value));
		
		return newNode;
	}
	
	protected MultiTreeNode createVarDeclaration(MultiTreeNode typeSpecifier, String identifierName, MultiTreeNode valueNode) {
	    MultiTreeNode newNode = new MultiTreeNode("VarDeclaration", identifierName);
	    
	    newNode.addChild(typeSpecifier);
	    newNode.addChild(valueNode);
	    
	    return newNode;
	}
	
	protected MultiTreeNode createCompoundStatement(MultiTreeNode compoundBody) {
		MultiTreeNode newNode = new MultiTreeNode("CompoundStatement");
		
		if (compoundBody != null) { newNode.addChild(compoundBody); }
		
		return newNode;
	}
	
	protected MultiTreeNode createOperandNode(Object operand) {
	    if (operand instanceof Integer || operand instanceof String) {
	        return new MultiTreeNode(operand.toString());
	    } else if (operand instanceof MultiTreeNode) {
	        return (MultiTreeNode) operand;
	    } else {
	        throw new IllegalArgumentException("Unsupported operand type: " + operand);
	    }
	}
	
	protected MultiTreeNode createAssignmentNode(String variableName, MultiTreeNode valueNode) {
	    MultiTreeNode assignmentNode = new MultiTreeNode("Assignment", variableName);
	    assignmentNode.addChild(valueNode);
	    
	    return assignmentNode;
	}
	
	protected MultiTreeNode createArithmeticNode(String operator, MultiTreeNode leftOperand, MultiTreeNode rightOperand) {
	    MultiTreeNode node = new MultiTreeNode(operator);
	    
	    node.addChild(leftOperand);
	    node.addChild(rightOperand);
	    
	    return node;
	}

	protected MultiTreeNode createComparisonNode(String operator, Object leftOperand, Object rightOperand) {
		MultiTreeNode leftNode = createOperandNode(leftOperand);
	    MultiTreeNode rightNode = createOperandNode(rightOperand);
	    
	    MultiTreeNode comparisonNode = new MultiTreeNode(operator);
	    
	    comparisonNode.addChild(leftNode);
	    comparisonNode.addChild(rightNode);
	    
	    return comparisonNode;
	}
	
	protected MultiTreeNode createIfStatement(MultiTreeNode identifier, MultiTreeNode ifInstructions, MultiTreeNode elseInstructions) {
		MultiTreeNode newNode = identifier;
		
		newNode.addChild(ifInstructions);
		
		if (elseInstructions != null) { newNode.addChild(elseInstructions); }
		
		return newNode;
	}
	
	protected MultiTreeNode createWhileStatement(MultiTreeNode condition, MultiTreeNode body) {
	    MultiTreeNode whileNode = new MultiTreeNode("WhileLoop");
	    
	    whileNode.addChild(condition);
	    whileNode.addChild(body);
	    
	    return whileNode;
	}
	
}
