package cup.example;
import java_cup.runtime.ComplexSymbolFactory;
import java_cup.runtime.ComplexSymbolFactory.Location;
import java_cup.runtime.Symbol;
import java.lang.*;
import java.io.InputStreamReader;

%%

%class Lexer
%implements sym
%public
%unicode
%line
%column
%cup
%char
%{
	

    public Lexer(ComplexSymbolFactory sf, java.io.InputStream is){
		this(is);
        symbolFactory = sf;
    }
	public Lexer(ComplexSymbolFactory sf, java.io.Reader reader){
		this(reader);
        symbolFactory = sf;
    }
    
    private StringBuffer sb;
    private ComplexSymbolFactory symbolFactory;
    private int csline,cscolumn;

    public Symbol symbol(String name, int code){
		return symbolFactory.newSymbol(name, code,
						new Location(yyline+1,yycolumn+1, yychar), // -yylength()
						new Location(yyline+1,yycolumn+yylength(), yychar+yylength())
				);
    }
    public Symbol symbol(String name, int code, String lexem){
	return symbolFactory.newSymbol(name, code, 
						new Location(yyline+1, yycolumn +1, yychar), 
						new Location(yyline+1,yycolumn+yylength(), yychar+yylength()), lexem);
    }
    
    protected void emit_warning(String message){
    	System.out.println("scanner warning: " + message + " at : 2 "+ 
    			(yyline+1) + " " + (yycolumn+1) + " " + yychar);
    }
    
    protected void emit_error(String message){
    	System.out.println("scanner error: " + message + " at : 2" + 
    			(yyline+1) + " " + (yycolumn+1) + " " + yychar);
    }
%}

Newline    = \r | \n | \r\n
Whitespace = [ \t\f] | {Newline}
Number     = [0-9]+

/* comments */
Comment = {TraditionalComment}
TraditionalComment = "/*" {CommentContent} \*+ "/"
CommentContent = ( [^*] | \*+[^*/] )*

ident = ([:jletter:] | "_" ) ([:jletterdigit:] | [:jletter:] | "_" )*


%eofval{
    return symbolFactory.newSymbol("EOF",sym.EOF);
%eofval}

%state CODESEG

%%  

<YYINITIAL> {

  {Whitespace} {                              }
  {Comment}    { /* Ignore Comments */ }
  
  "else"       { return symbolFactory.newSymbol("ELSE", ELSE); }
  "if"         { return symbolFactory.newSymbol("IF", IF); }
  "int"        { return symbolFactory.newSymbol("INT", INT); }
  "return"     { return symbolFactory.newSymbol("RETURN", RETURN); }
  "void"       { return symbolFactory.newSymbol("VOID", VOID); }
  "while"      { return symbolFactory.newSymbol("WHILE", WHILE); }
  "return"	   { return symbolFactory.newSymbol("RETURN", RETURN); }
  
  ";"          { return symbolFactory.newSymbol("SEMI", SEMI); }
  ","          { return symbolFactory.newSymbol("COMMA", COMMA); }
  "+"          { return symbolFactory.newSymbol("PLUS", PLUS); }
  "-"          { return symbolFactory.newSymbol("MINUS", MINUS); }
  "*"          { return symbolFactory.newSymbol("TIMES", TIMES); }
  "/"          { return symbolFactory.newSymbol("DIVIDE", DIVIDE); }
  "%"		   { return symbolFactory.newSymbol("MODULUS", MODULUS); }

  "<="         { return symbolFactory.newSymbol("LEQ", LEQ); }
  "<"          { return symbolFactory.newSymbol("LT", LT); }
  ">="         { return symbolFactory.newSymbol("GEQ", GEQ); }
  ">"          { return symbolFactory.newSymbol("GT", GT); }
  "=="         { return symbolFactory.newSymbol("EQ", EQ); }
  "!="         { return symbolFactory.newSymbol("NEQ", NEQ); }
  "="          { return symbolFactory.newSymbol("ASSIGN", ASSIGN); }

  "("          { return symbolFactory.newSymbol("LPAREN", LPAREN); }
  ")"          { return symbolFactory.newSymbol("RPAREN", RPAREN); }
  "{"          { return symbolFactory.newSymbol("LBRACE", LBRACE); }
  "}"          { return symbolFactory.newSymbol("RBRACE", RBRACE); }
  
  {ident}	   { return symbolFactory.newSymbol("ID", ID, yytext()); }
  {Number}     { return symbolFactory.newSymbol("NUMBER", NUMBER, Integer.parseInt(yytext())); }

}



// error fallback
.|\n          { emit_warning("Unrecognized character '" +yytext()+"' -- ignored"); }
