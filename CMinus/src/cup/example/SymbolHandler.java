package cup.example;

import java.util.HashMap;
import java.util.Stack;

public class SymbolHandler {
    private Stack<HashMap<String, SymbolDetails>> contextStack;
    private HashMap<String, SymbolDetails> table;

    public SymbolHandler(Stack<HashMap<String, SymbolDetails>> contextStack, HashMap<String, SymbolDetails> table) {
        this.contextStack = contextStack;
        this.table = table;
    }

    public void addPredefinedFunction(String functionName) {
        SymbolDetails func = new SymbolDetails();
        func.symbolName = functionName;
        func.dataType = "VOID";
        func.symbolType = SymbolType.Function;
        func.symbolScope = IdentifierScope.Global;
        func.contextName = "Global";

        table.put(functionName, func);
        if (!contextStack.isEmpty()) {
            contextStack.peek().put(functionName, func);
        }
    }

    public void handleVarDeclaration(MultiTreeNode node, String currentContext, IdentifierScope scope) {
        String varName = node.getExtraData();
        String dataType = node.getChildren()[0].getExtraData();

        if (!contextStack.isEmpty() && contextStack.peek().containsKey(varName)) {
            System.err.println("Error: Variable '" + varName + "' is already defined in the current scope (" + currentContext + ").");
            return;
        }

        SymbolDetails details = new SymbolDetails();
        details.contextName = currentContext;
        details.symbolName = varName;
        details.dataType = dataType;
        details.symbolType = SymbolType.Variable;
        details.symbolScope = scope;

        table.put(varName, details);
        if (!contextStack.isEmpty()) {
            contextStack.peek().put(varName, details);
        }
    }

    public void handleFunctionDeclaration(MultiTreeNode node, String currentContext) {
        String funcName = node.getExtraData();
        String returnType = node.getChildren()[0].getExtraData();

        if (table.containsKey(funcName)) {
            System.err.println("Error: Function '" + funcName + "' is already defined in the global scope.");
            return;
        }

        SymbolDetails details = new SymbolDetails();
        details.contextName = "Global";
        details.symbolName = funcName;
        details.dataType = returnType;
        details.symbolType = SymbolType.Function;
        details.symbolScope = IdentifierScope.Global;

        table.put(funcName, details);

        contextStack.push(new HashMap<String, SymbolDetails>());

        MultiTreeNode paramsList = node.getChildren().length > 1 ? node.getChildren()[1] : null;
        if (paramsList != null && paramsList.getData().equals("ParametersList")) {
            for (MultiTreeNode param : paramsList.getChildren()) {
                handleVarDeclaration(param, funcName, IdentifierScope.Local);
            }
        }

        MultiTreeNode functionBody = node.getChildren()[node.getChildren().length - 1];
        if (functionBody != null && functionBody.getData().equals("CompoundStatement")) {
            handleCompoundBody(functionBody, funcName, IdentifierScope.Local);
        }

        contextStack.pop();
    }
    
    public void handleVariableUsage(MultiTreeNode node, String currentContext) {
        String varName = node.getData();

        if (!isSymbolInActiveScope(varName)) {
            System.err.println("Error: Variable '" + varName + "' is not declared in the current scope (" + currentContext + ").");
        }
    }
    
    public void handleExpression(MultiTreeNode node, String currentContext) {
        switch (node.getData()) {
            case "VarDeclaration":
                handleVariableUsage(node, currentContext);
                break;
                
            case "Assignment":
                String varName = node.getExtraData();
                handleVariableUsage(new MultiTreeNode(varName), currentContext);
                for (MultiTreeNode child : node.getChildren()) {
                    handleExpression(child, currentContext);
                }
                break;

            case "FunctionCall":
                String functionName = node.getExtraData();
                if (!table.containsKey(functionName)) {
                    System.err.println("Error: Function '" + functionName + "' is not declared in the current scope.");
                }

                for (MultiTreeNode param : node.getChildren()) {
                    handleExpression(param, currentContext);
                }
                break;

            default:
                for (MultiTreeNode child : node.getChildren()) {
                	if (child.getChildren().length == 0) {
                        handleVariableUsage(child, currentContext);
                    } else {
                    	handleExpression(child, currentContext);
                    }
                }
                break;
        }
    }

    public void handleCompoundBody(MultiTreeNode node, String currentContext, IdentifierScope scope) {
        contextStack.push(new HashMap<>());

        for (MultiTreeNode child : node.getChildren()) {
            switch (child.getData()) {
                case "VarDeclaration":
                    handleVarDeclaration(child, currentContext, scope);
                    break;

                case "Assignment":
                case "FunctionCall":
                case "Return":
                    handleExpression(child, currentContext);
                    break;

                default:
                    handleCompoundBody(child, currentContext, scope);
                    break;
            }
        }

        contextStack.pop();
    }

    private boolean isSymbolInActiveScope(String symbolName) {
        for (int i = contextStack.size() - 1; i >= 0; i--) {
            if (contextStack.get(i).containsKey(symbolName)) {
                return true;
            }
        }
        return false;
    }
}
