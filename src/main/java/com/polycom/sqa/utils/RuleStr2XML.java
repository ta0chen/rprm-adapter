package com.polycom.sqa.utils;

import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

class Jnode {

    protected String leftAttribute;
    protected String leftDimension;
    protected Jnode leftNode;
    protected JnodeType nodeType;
    protected String nodeUUID;
    protected String operator;
    protected String rightAttribute;
    protected String rightDimension;
    protected Jnode rightNode;
    protected boolean whetherNot;
}

enum JnodeType {

    AND,
    OR,
    ELEMENT;

    public static JnodeType fromValue(final String v) {
        return valueOf(v);
    }

    public String value() {
        return name();
    }

}

public class RuleStr2XML {

	private static Logger logger = Logger.getLogger("sys");
	private final StringBuffer sb = new StringBuffer();
	
	public StringBuffer getXMLStr() {
		
		return sb;
	}

	public String[] Infix2Prefix(final String ruleStr) {

    	
    	final StringBuffer[] ConditionTable = new StringBuffer[10];
    	final String input = replace(ruleStr, ConditionTable);
    	
    	final int len = input.length();
        char c,tempChar;
        final Stack<Character> s1 = new Stack<Character>(); //stack for operators
        final Stack<Object> expression = new Stack<Object>();  //stack for prefix exp
        //scan the input infix exp from right to left 
        for (int i=len-1; i>=0; --i) { 
          c = input.charAt(i);
          if (Character.isDigit(c)) {
                //String s = String. valueOf(c);
                //int j = Integer.parseInt(s);    
                expression.push(c); 
          } else if (isOperator(c)) {
                while (!s1.isEmpty() && s1.peek() != ')' && priorityCompare(c, s1.peek()) < 0) { 
                      expression.push(s1.pop());  
                } 
                s1.push(c); 
          } else if (c == ')' ) {
                s1.push(c); 
          } else if (c == '(' ) { 
                while ((tempChar=s1.pop()) != ')' ) { 
                      expression.push(tempChar); 
                      if (s1.isEmpty()) { 
                            throw new IllegalArgumentException( 
                                  "bracket dosen't match, missing right bracket ')'."); 
                      } 
                } 
          } else if (c == ' ' ) {  
        	  // skip blank       
          } else { 
                throw new IllegalArgumentException( 
                            "wrong character '" + c + "'" ); 
          } 
        }  //end for
        
        while (!s1.isEmpty()) { 
          tempChar = s1.pop(); 
          expression.push(tempChar); 
        } 
        final int stack_size = expression.size();
        final String[] PrefixArr = new String[stack_size];
        //while (!expression.isEmpty()) { 
        for (int i = 0; i < stack_size; i++) {
          String tempStr = expression.pop().toString();
          if (tempStr.equals("&")) {
        	   tempStr = "&&";
          } else if (tempStr.equals("|")) {
        	   tempStr = "||";
          } else if (tempStr.matches("^\\d{1}$")) {
        	   tempStr = ConditionTable[Integer.parseInt(tempStr)].toString();
          } else {
        	   throw new IllegalArgumentException( 
                      "wrong character '" + tempStr + "' is found in prefix stack" ); 
          }
          
          PrefixArr[i] = tempStr;
        } 

/*
        for (int i = 0; i < stack_size; i++) { 
        	System.out.println("" + i + ": " + PrefixArr[i]); 
        }
*/       
        return PrefixArr;
        
	}
	
	private boolean isOperator( final char c) {
        return (c=='&' || c=='|'); 
    }
	
    private int priorityCompare( final char op1, final char op2) { 
        switch (op1) { 
        case '|' : case '&' :
              return 0; 
        } 
        return 1; 
    }
    
    public String replace(final String input, final StringBuffer[] CondTab) {
		
		final Pattern pattern = Pattern.compile("(!?[\\w]+\\.[\\w-~,:#@]+==[\\w\\+-~,\\.:#@]+)");
		Matcher matcher ;

		System.out.println("running method 'replace'");	
				
		String myStr = input;
		System.out.println(myStr);
		for (int i = 1 ; ; i++) {
			matcher = pattern.matcher(myStr);
			if (matcher.find()) {
				CondTab[i] = new StringBuffer();
				CondTab[i].append(matcher.group(1));
				myStr = matcher.replaceFirst(((Integer)i).toString());
				System.out.println(myStr);
			} else {
				break;
			}		
		}
		
		myStr = myStr.replaceAll("&&", "&");
		System.out.println(myStr);
		myStr = myStr.replaceAll("\\|\\|", "|");
		System.out.println(myStr);
		
		return myStr;

	}
	
    public Jnode Str2Tree(final String[] prefixExp) {

		final Stack<Jnode> s1 = new Stack<Jnode>();
		
		//System.out.println("the length is: " + prefixExp.length);
		//System.out.println(" " + prefixExp[0] + "\n" + prefixExp[4]);  
		
		for (int i = prefixExp.length-1 ; i >= 0 ; i--) {
			
			if(!(prefixExp[i].equals("||") || prefixExp[i].equals("&&"))) {
			     final Pattern pattern = Pattern.compile("^(!?[\\w]+)\\.([\\w-~,:#@]+)==([\\w\\+-~,\\.:#@]+)$");
			     final Matcher matcher = pattern.matcher(prefixExp[i]);
				 if(matcher.find()) {
					 final Jnode node = new Jnode();
					 node.nodeType = JnodeType.ELEMENT;
					 if (matcher.group(2).equalsIgnoreCase("userGroup")) {
						 node.operator = "contains";
					 } else {
						 node.operator = "==";
					 }
					 if (matcher.group(1).startsWith("!")) {
						 node.leftDimension = matcher.group(1).substring(1);
						 node.whetherNot = true;		 
					 } else {
						 node.leftDimension = matcher.group(1);
						 node.whetherNot = false;
					 }
					 node.leftAttribute = matcher.group(2); 
					 node.rightDimension = matcher.group(3);
					 s1.push(node);
				 } else {
					 System.out.println("The node format is invalid: " + prefixExp[i]);
					 return null;
				 }
				 
				 //System.out.println("nodetype: " + node.nodeType + "  nodeoperator: " + node.operator);
				 //System.out.println("leftDim: " + node.leftDimension + "  leftAtt: " + node.leftAttribute + "  rightAtt: " + node.rightAttribute);
				 
			} else {       // the element is && or ||
				 final Jnode node = new Jnode();
				 Jnode leftNode = new Jnode();
				 Jnode rightNode = new Jnode();
				 
				 if (!s1.isEmpty()) {
				     leftNode = s1.pop();
				 } else {
					 // error
					 System.out.println("abnormal");
					 return null;
				 } 
				 if (!s1.isEmpty()) {
				     rightNode = s1.pop();
				 } else {
					 // error
					 System.out.println("abnormal");
					 return null;
				 }	
				 

				 if (prefixExp[i].equals("||")) {
					 node.nodeType = JnodeType.OR;
				 } else if(prefixExp[i].equals("&&")){
					 node.nodeType = JnodeType.AND;
				 } else {
					 System.out.println("The str " + prefixExp[i] + " is not an expected string");
					 return null; 
				 }
				 node.leftNode = leftNode;
				 node.rightNode = rightNode;
				 node.whetherNot = false;
				 s1.push(node);
			}
		} //end for 
		
		return s1.pop();
		
	} //end Str2Tree(String[] prefixExp)
    
  
    public void ToXML(final String ruleStr) {

		//String rule1 = "((!site.site==site1)||(group.group==group1))&&(user.username==admin)";
		//String[] preRule1 = {"&&","||" ,"site.site==site1","group.group==group1", "user.username==admin"};
		
		//String rule2 = "((site==site1)||(group==group1))&&(user.username==admin)";
		//String[] preRule2 = {"||", "&&", "&&", "a.a==a1", "b.b==b1", "&&", "c.c==c1", "d.d==d1", "||", "||", "e.e==e1", "f.f==f1", "||", "g.g==g1", "h.h==h1"};


		logger.info("running method 'Infix2Prefix'");		
		final String[] preRule1 = Infix2Prefix(ruleStr);
	    for (int i = 0; i < preRule1.length; i++) { 
	    	logger.debug("" + i + ": " + preRule1[i]); 
	    }
		
	    logger.info("running method 'Str2Tree'");	
		final Jnode rootnode = Str2Tree(preRule1);
		if (rootnode == null) {
			logger.error("Error: the rootnode is null");
			return ;
		}
        
		logger.info("running method 'XmlGen'");
		XmlGen(rootnode);
		//System.out.println("the xml: \n" + sb);
	}
	
	public void XmlGen(final Jnode rootNode) {
	
    	if (rootNode == null) {
    		System.out.println("Error: the root node is null");
    		return ;
    	}
    	if (rootNode.nodeType == JnodeType.OR || rootNode.nodeType == JnodeType.AND) {
    		sb.append("<leftNode xsi:type=\"web:JNode\">");
    		XmlGen(rootNode.leftNode);
    		sb.append("</leftNode>");
    		sb.append("<nodeType>" + rootNode.nodeType + "</nodeType>");
			sb.append("<rightNode xsi:type=\"web:JNode\">");
			XmlGen(rootNode.rightNode);
			sb.append("</rightNode>");    		
			sb.append("<whetherNot>" + rootNode.whetherNot + "</whetherNot>");	
    	} else if (rootNode.nodeType == JnodeType.ELEMENT) {
    		if (rootNode.leftAttribute != null) {
				sb.append("<leftAttribute>" + rootNode.leftAttribute + "</leftAttribute>");
			}
			if (rootNode.leftDimension != null) {
				sb.append("<leftDimension>" + rootNode.leftDimension + "</leftDimension>");
			}
			if (rootNode.nodeType != null) {
				sb.append("<nodeType>" + rootNode.nodeType + "</nodeType>");
			}
			if (rootNode.nodeUUID != null) {
				sb.append("<nodeUUID>" + rootNode.nodeType + "</nodeUUID>");
			}			
			if (rootNode.operator != null) {
				sb.append("<operator>" + rootNode.operator + "</operator>");
			}
			if (rootNode.rightAttribute != null) {
				sb.append("<rightAttribute>" + rootNode.rightAttribute + "</rightAttribute>");
			}
			if (rootNode.rightDimension != null) {
				sb.append("<rightDimension>" + rootNode.rightDimension + "</rightDimension>");
			}
			sb.append("<whetherNot>" + rootNode.whetherNot + "</whetherNot>");		
    	}
    	  	  	
    }
}


