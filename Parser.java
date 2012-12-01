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
	Declarations ds = new Declarations();
	while ( checkType())
	{
//not exactly the fix they wanted but i will see if it gets me into trouble
	Type t = type(token);
	//have to change this so it matches multiple types	
   if (token.type().equals(TokenType.Int)){
    match(TokenType.Int);
   } 
   else if (token.type().equals(TokenType.Bool)){
    match(TokenType.Bool);
   }
   else if (token.type().equals(TokenType.Char)){
    match(TokenType.Char);
   }
   else if (token.type().equals(TokenType.Float)){
    match(TokenType.Float);
   }
	
	Variable v = new Variable (match(TokenType.Identifier));
	ds.add(new Declaration(v,t));
	while(token.type().equals(TokenType.Comma))
		{
		match(TokenType.Comma);
		Variable v2 = new Variable (match(TokenType.Identifier));
		Declaration d = new Declaration (v2,t);
		ds.add(d);	
		}
	match(TokenType.Semicolon);
	
	}
        return ds;  // student exercise
    }
  
private boolean checkType(){
	if  (token.type().equals(TokenType.Int)  ||
		token.type().equals(TokenType.Bool) ||
		token.type().equals(TokenType.Char) ||
		token.type().equals(TokenType.Float))	
	return true;
	else
	return false;
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
  
    private Type type (Token s) {
        // Type  -->  int | bool | float | char 
        Type t = null;
	if(s.type().equals(TokenType.Int)){
	t = Type.INT; 	
	}
    else if(s.type().equals(TokenType.Bool)){
    t = Type.BOOL;   
    }
    else if(s.type().equals(TokenType.Char)){
    t = Type.CHAR;   
    }
    else if(s.type().equals(TokenType.Float)){
    t = Type.FLOAT;   
    }
        // student exercise
        return t;          
    }
  
    private Statement statement() {
        // Statement --> ; | Block | Assignment | IfStatement | WhileStatement
//something like this... not sure if its tokenType or maybe something else
	Statement s = null;	
	if (token.type().equals(TokenType.Semicolon)){
	//case ';':   
     match(TokenType.Semicolon);    
	 s = new Skip();}
	else if (token.type().equals(TokenType.Identifier)){
	//case 'Identifier':
	
	s = assignment(); 	
	}
	//case 'if'
	else if (token.type().equals(TokenType.If)){
    s = ifStatement();
	}
    // case 'while'
	else if (token.type().equals(TokenType.While)){
    s = whileStatement();
	}	
    else if (token.type().equals(TokenType.LeftBrace)){
        match(TokenType.LeftBrace);
        s = statements();
        match(TokenType.RightBrace);
    }
    // also block
        // student exercise
        return s;
    }
  
    private Block statements () {
        // Block --> '{' Statements '}'
	// while (members.hasNext()){}
	
        Block b = new Block();
	       while (isStatement()){
	           b.members.add(statement());
	}
        // student exercise
        return b;
    }

    private boolean isStatement (){
        return
        (token.type().equals(TokenType.Identifier))||
        (token.type().equals(TokenType.Semicolon))||
        (token.type().equals(TokenType.If))||
        (token.type().equals(TokenType.While))||
        (token.type().equals(TokenType.LeftBrace))   ;
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
        match(TokenType.If);
        match(TokenType.LeftParen);
       
        Expression e = expression();
        match(TokenType.RightParen);

        Statement thenBranch = statement();
        match(TokenType.Else);
        Statement elseBranch = statement ();
        return new Conditional(e, thenBranch, elseBranch);  // student exercise
    }
  
    private Loop whileStatement () {
        // WhileStatement --> while ( Expression ) Statement
        match(TokenType.While);
        match(TokenType.LeftParen);
        Expression test = expression();
        match(TokenType.RightParen);
        Statement body = statement();
        return new Loop(test, body);  // student exercise
    }

    private Expression expression () {
        // Expression --> Conjunction { || Conjunction }
	Expression e = conjunction();
	while (token.type().equals(TokenType.Or)){
		Operator op = new Operator (match(token.type()));
		Expression con2 = conjunction();
		e = new Binary(op , e , con2);	
	}
	return e;  
    }
  
    private Expression conjunction () {
        // Conjunction --> Equality { && Equality }
	Expression e = equality();
	while (token.type().equals(TokenType.And)){
		Operator op = new Operator (match(token.type()));
		Expression equ2 = equality();
		e = new Binary(op , e , equ2);	
	}
        return e;  
    }
  
    private Expression equality () {

        // Equality --> Relation [ EquOp Relation ]
	Expression e = relation();
	while (isEqualityOp()){
		Operator op = new Operator (match(token.type()));
		Expression rel2 = relation();
		e = new Binary(op , e , rel2);	
	}
        return e;  
    }

    private Expression relation (){
	Expression e = addition();
	while (isRelationalOp()){
		Operator op = new Operator (match(token.type()));
		Expression add2 = addition();
		e = new Binary(op , e , add2);	
	}
        // Relation --> Addition [RelOp Addition] 
        return e;  // student exercise
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
//finished for ints got to get other stuff..
	if (token.type().equals(TokenType.Int)){
    	int myVal = Integer.parseInt(token.value());	
	    match(TokenType.IntLiteral);
	    v = new IntValue (myVal);
        }
   else if (token.type().equals(TokenType.Char)){
        char myVal = (token.value()).charAt(0);    
        match(TokenType.CharLiteral);
        v = new CharValue (myVal);
        }
    // hot to construct a bool val from a string
   /* else if (token.type().equals(TokenType.Bool)){
        boolean myVal = (token.value()).charAt(0);    
        match(TokenType.Bool);
        v = new BoolValue (myVal);
        }*/
    else if (token.type().equals(TokenType.Char)){
        float myVal = Float.parseFloat(token.value());    
        match(TokenType.FloatLiteral);
        v = new FloatValue (myVal);
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
