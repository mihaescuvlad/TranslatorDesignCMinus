package cup.example;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Stack;

public class SymbolsTable {
	private MultiTree syntaxTree;
	private Stack<HashMap<String, SymbolDetails>> contextStack = new Stack<HashMap<String, SymbolDetails>>();
	private HashMap<String, SymbolDetails> table = new HashMap<String, SymbolDetails>();
	private SymbolHandler handler;
    
	public SymbolsTable(MultiTree syntaxTree) {
        this.syntaxTree = syntaxTree;

        handler = new SymbolHandler(contextStack, table);

        contextStack.push(new HashMap<String, SymbolDetails>());

        handler.addPredefinedFunction("input");
        handler.addPredefinedFunction("output");
    }

    public void createTable() {
        extractSymbolsFromNode(syntaxTree.getRoot(), "Global", IdentifierScope.Global);
    }

    public SymbolDetails getSymbol(String symbol) {
        return table.get(symbol);
    }

    public void printTable() {
        for (Entry<String, SymbolDetails> mapEntry : table.entrySet()) {
            String symbol = mapEntry.getKey();
            SymbolDetails details = mapEntry.getValue();

            System.out.println("------------ SYMBOL: " + symbol + " -----------------");
            System.out.println("Data Type: " + details.dataType);
            System.out.println("Context: " + details.contextName);
            System.out.println("Symbol Type: " + details.symbolType);
            System.out.println("Symbol Scope: " + details.symbolScope);
        }
    }
    
    private void extractSymbolsFromNode(MultiTreeNode node, String currentContext, IdentifierScope scope) {
        switch (node.getData()) {
            case "VarDeclaration":
                handler.handleVarDeclaration(node, currentContext, scope);
                break;

            case "FunctionDeclaration":
                handler.handleFunctionDeclaration(node, currentContext);
                break;

            case "Assignment":
            case "Operand":
            case "FunctionCall":
                handler.handleExpression(node, currentContext);
                break;

            case "CompoundStatement":
                handler.handleCompoundBody(node, currentContext, scope);
                break;

            default:
                for (MultiTreeNode child : node.getChildren()) {
                    extractSymbolsFromNode(child, currentContext, scope);
                }
                break;
        }
    }
}
