package cup.example;

import java.util.HashMap;
import java.util.Map.Entry;

public class SymbolsTable {
	private MultiTree syntaxTree;
	private HashMap<String, SymbolDetails> table = new HashMap<String, SymbolDetails>();

	private void extractSymbolsFromNode(MultiTreeNode node, String currentContext, IdentifierScope scope) {
		String context = currentContext;
		IdentifierScope localScope = scope;

		if (node.getData().equals("VarDeclaration")) {
			SymbolDetails details = new SymbolDetails();
			details.contextName = currentContext;
			details.symbolName = node.getExtraData();

			if (node.getChildren().length > 0) {
				details.dataType = node.getChildren()[0].getExtraData();
			}

			details.symbolScope = scope;
			details.symbolType = SymbolType.Variable;

			table.put(details.symbolName, details);
		}

		if (node.getData().equals("FunctionDeclaration")) {
			SymbolDetails details = new SymbolDetails();

			details.contextName = currentContext;
			details.symbolName = node.getExtraData();

			if (node.getChildren().length > 0) {
				details.dataType = node.getChildren()[0].getExtraData();
			}

			details.symbolScope = scope;
			details.symbolType = SymbolType.Function;
			context = details.symbolName;
			localScope = IdentifierScope.Local;

			table.put(details.symbolName, details);
		}

		for (int i = 0; i < node.getChildren().length; ++i) {
			extractSymbolsFromNode(node.getChildren()[i], context, localScope);
		}
	}
	
	public SymbolsTable(MultiTree syntaxTree) {
		this.syntaxTree = syntaxTree;
	}
	
	public void createTable() {
		extractSymbolsFromNode(syntaxTree.getRoot(), "Global", IdentifierScope.Global);
	}
	
	public SymbolDetails getSymbol(String symbol) {
		if (table.containsKey(symbol)) {
			return table.get(symbol);
		}
		
		return null;
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
}
