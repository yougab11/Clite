import java.util.*;

public class Parser {
    // Recursive descent parser that inputs a C++Lite program and 
    // generates its abstract syntax.  Each method corresponds to
    // a concrete syntax grammar rule, which appears as a comment
    // at the beginning of the method.
  
    Token token;          // current token from the input stream
    Lexer lexer;
  
    public Parser(Lexer ts) { // Open the C++Lite source program
        lexer = ts;                          // as a token stream, and
        token = lexer.next();            // retrieve its first Token
    }
  
    private String match (TokenType t) {
        String value = token.value();
        if (token.type().equals(t))
            token = lexer.next();
        else
            error(t);
        return value;
    }
  
    private void error(TokenType tok) {
        System.err.println("Syntax error: expecting: " + tok 
                           + "; saw: " + token);
        System.exit(1);
    }
  
    private void error(String tok) {
        System.err.println("Syntax error: expecting: " + tok 
                           + "; saw: " + token);
        System.exit(1);
    }
  
    public Program program() {
        // Program --> void main ( ) '{' Declarations Statements '}'
 	
 	TokenType[ ] header = {TokenType.Int, TokenType.Main,
                          TokenType.LeftParen, TokenType.RightParen};
        for (int i=0; i<header.length; i++)   // bypass "int main ( )"
            match(header[i]);
	//end of program header

        match(TokenType.LeftBrace);
        	Declarations d = declarations();
        	Block b = statements();
        match(TokenType.RightBrace);
	Program p = new Program(d,b);
        return p ;  // student exercise
    }
  
    private Declarations declarations () {
        // Declarations --> { Declaration }
	
        return new Declarations();  // student exercise
    }
  
    private void declaration (Declarations ds) {
        // Declaration  --> Type Identifier { , Identifier } ;
//run While loop
//Type t = ds.type();
	/*
	while (TokenType isComma)
		{
		Identifier a = type t;
		}
	*/
	// student exercise
    }
  
    private Type type () {
        // Type  -->  int | bool | float | char 
        Type t = null;
	/*if(token.type().equals(TokenType.Int)){
	t = new Type("INT"); 	
	}*/
        // student exercise
        return t;          
    }
  
    private Statement statement() {
        // Statement --> ; | Block | Assignment | IfStatement | WhileStatement
//something like this... not sure if its tokenType or maybe something else
	Statement s = null;	
	if (token.type().equals(TokenType.Semicolon)){
	//case ';':       
	 s = new Skip();}
	else if (token.type().equals(TokenType.Identifier)){
	//case 'Identifier':
	
	s = assignment(); 	
	}
/*	
	else if (token.type().equals(TokenType.If)){
	}
	else if (token.type().equals(TokenType.While)){
	}	
*/
        // student exercise
        return s;
    }
  
    private Block statements () {
        // Block --> '{' Statements '}'
	// while (members.hasNext()){}
	
        Block b = new Block();
	while (token.type().equals(TokenType.Identifier)){
	b.members.add(statement());
	}
        // student exercise
        return b;
    }
  
    private Assignment assignment () {
        // Assignment --> Identifier = Expression ;
	
	//System.out.println(token.value());
	Variable var = new Variable (match(TokenType.Identifier));
	match(TokenType.Assign);
	Expression e = expression();
        match(TokenType.Semicolon);
	return new Assignment (var, e); //Assignment;  // student exercise
    }
  
    private Conditional ifStatement () {
        // IfStatement --> if ( Expression ) Statement [ else Statement ]
        return null;  // student exercise
    }
  
    private Loop whileStatement () {
        // WhileStatement --> while ( Expression ) Statement
        return null;  // student exercise
    }

    private Expression expression () {
        // Expression --> Conjunction { || Conjunction }
	
        return (conjunction());  // student exercise
    }
  
    private Expression conjunction () {
        // Conjunction --> Equality { && Equality }
        return (equality());  // student exercise
    }
  
    private Expression equality () {

        // Equality --> Relation [ EquOp Relation ]
        return (relation());  // student exercise
    }

    private Expression relation (){
        // Relation --> Addition [RelOp Addition] 
        return (addition());  // student exercise
    }
  
    private Expression addition () {
        // Addition --> Term { AddOp Term }
        Expression e = term();
        while (isAddOp()) {
            Operator op = new Operator(match(token.type()));
            Expression term2 = term();
            e = new Binary(op, e, term2);
        }
        return e;
    }
  
    private Expression term () {
        // Term --> Factor { MultiplyOp Factor }
        Expression e = factor();
        while (isMultiplyOp()) {
            Operator op = new Operator(match(token.type()));
            Expression term2 = factor();
            e = new Binary(op, e, term2);
        }
        return e;
    }
  
    private Expression factor() {
        // Factor --> [ UnaryOp ] Primary 
        if (isUnaryOp()) {
            Operator op = new Operator(match(token.type()));
            Expression term = primary();
            return new Unary(op, term);
        }
        else return primary();
    }
  
    private Expression primary () {
        // Primary --> Identifier | Literal | ( Expression )
        //             | Type ( Expression )
        Expression e = null;
        if (token.type().equals(TokenType.Identifier)) {
            e = new Variable(match(TokenType.Identifier));
        } else if (isLiteral()) {
            e = literal();
        } else if (token.type().equals(TokenType.LeftParen)) {
            token = lexer.next();
            e = expression();       
            match(TokenType.RightParen);
        } else if (isType( )) {
            Operator op = new Operator(match(token.type()));
            match(TokenType.LeftParen);
            Expression term = expression();
            match(TokenType.RightParen);
            e = new Unary(op, term);
        } else error("Identifier | Literal | ( | Type");
        return e;
    }

    private Value literal( ) {
	Value v = null;
	if (token.type().equals(TokenType.IntLiteral)){
	
	int myVal = Integer.parseInt(token.value());	
	match(TokenType.IntLiteral);
	v = new IntValue (myVal);
	}
        return v;  // student exercise
    }
  

    private boolean isAddOp( ) {
        return token.type().equals(TokenType.Plus) ||
               token.type().equals(TokenType.Minus);
    }
    
    private boolean isMultiplyOp( ) {
        return token.type().equals(TokenType.Multiply) ||
               token.type().equals(TokenType.Divide);
    }
    
    private boolean isUnaryOp( ) {
        return token.type().equals(TokenType.Not) ||
               token.type().equals(TokenType.Minus);
    }
    
    private boolean isEqualityOp( ) {
        return token.type().equals(TokenType.Equals) ||
            token.type().equals(TokenType.NotEqual);
    }
    
    private boolean isRelationalOp( ) {
        return token.type().equals(TokenType.Less) ||
               token.type().equals(TokenType.LessEqual) || 
               token.type().equals(TokenType.Greater) ||
               token.type().equals(TokenType.GreaterEqual);
    }
    
    private boolean isType( ) {
        return token.type().equals(TokenType.Int)
            || token.type().equals(TokenType.Bool) 
            || token.type().equals(TokenType.Float)
            || token.type().equals(TokenType.Char);
    }
    
    private boolean isLiteral( ) {
        return token.type().equals(TokenType.IntLiteral) ||
            isBooleanLiteral() ||
            token.type().equals(TokenType.FloatLiteral) ||
            token.type().equals(TokenType.CharLiteral);
    }
    
    private boolean isBooleanLiteral( ) {
        return token.type().equals(TokenType.True) ||
            token.type().equals(TokenType.False);
    }
	
//	void display(){
//		System.out.println ("abstract syntax display");
//	}
    
    public static void main(String args[]) {
        Parser parser  = new Parser(new Lexer(args[0]));
        Program prog = parser.program();
        prog.display();           // display abstract syntax tree
    } //main

} // Parser
