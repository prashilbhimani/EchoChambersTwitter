//Author: ANupama & ROhit
package neo4j.neo4j;


import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.driver.v1.TransactionWork;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.*;
import static org.neo4j.driver.v1.Values.parameters;

public class HelloWorld implements AutoCloseable
{
    private final Driver driver;
    static GraphDatabaseFactory dbFact = null;
    static GraphDatabaseService db = null;

    public HelloWorld( String uri, String user, String password )
    {
        driver = GraphDatabase.driver( uri, AuthTokens.basic( user, password ) );
    }

    public void close() throws Exception
    {
        driver.close();
    }
    

  
    public void userMentions( final String user, final String user2,int count )
    {
        try ( Session session = driver.session() )
        {
            String greeting = session.writeTransaction( new TransactionWork<String>()
            {
                @Override
                public String execute( Transaction tx )
                {	
                 
                	StatementResult result = tx.run("MERGE (a:Person {name: {x}})"+
                								"MERGE (b:Person {name: {y}})"+
                								"WITH a,b"+
                								" MATCH (u:Person {name: {x}}),(r:Person {name: {y}})"+
                								"Create (u) -[:Mentions_times_"+count+"]-> (r)"
                							, parameters("x", user,"y",user2));
//                    return result.single().get( 0 ).asString();
                    return "Done";
                }
            } );
            System.out.println( greeting );
        }
    }

    public static void main( String... args ) throws Exception
    {
        try ( HelloWorld greeter = new HelloWorld( "bolt://35.196.233.107:7687", "neo4j", "rmehra" ) )
        {
        	
            greeter.userMentions( "Rikishi","Shayon",22 );
            greeter.userMentions( "Shayon","Prashil",6 );
            greeter.userMentions( "Prashil","Anupama",8 );
            greeter.userMentions( "Prashil","Shayon",4 );
            greeter.userMentions( "Rikishi","Anupama",7 );
            greeter.userMentions( "Sharan","Anupama",5 );
            greeter.userMentions( "Sharan","Rohit",18 );
            greeter.userMentions( "Rohit","Anupama",9 );
            greeter.userMentions( "Rohit","Shayon",2 );
            greeter.userMentions( "Anupama","Sharan",23 );
            greeter.userMentions( "Anupama","Rohit",1 );
        }
    }
}
// end::hello-world[]

// tag::hello-world-output[]
// hello, world, from node 1234
// end::hello-world-output[]
