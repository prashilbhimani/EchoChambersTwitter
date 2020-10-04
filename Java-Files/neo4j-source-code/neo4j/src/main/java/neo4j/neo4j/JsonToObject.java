package neo4j.neo4j;

import static org.neo4j.driver.v1.Values.parameters;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.driver.v1.TransactionWork;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;


public class JsonToObject {

   
      
      
        public static void main(String args[]) throws Exception{

        	JsonToObject converter = new JsonToObject();
              
        	BufferedReader reader;
        	try ( HelloWorld greeter = converter.new HelloWorld( "bolt://35.231.74.19:7687", "neo4j", "comm" ) )
            {
//    			File file = new File("/src/main/resources/dummy.txt");
    			reader = new BufferedReader(new FileReader("src/main/resources/dummy.txt"));
    			
    			String line;
    			 while ((line=reader.readLine()) != null) {
    			     System.out.println(line);
    			   //converting JSON String to Java object
                   User ab = (User) converter.fromJson(line);
                   greeter.addUserNode(ab);
                   
    			  }
    			 greeter.addUserRelation("392414460", "112030336", "1234567890", "0987654321");
    			 greeter.addUserRelation("112030336","392414460",  "1234567890", "0987654321");
    			
    			
    			reader.close();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
             
              
                
        }
      
      

		public Object fromJson(String json) throws JsonGenerationException
                                                   , JsonMappingException, IOException{
                User myuser = new ObjectMapper().readValue(json, User.class);
               
              System.out.println(myuser);
                return myuser;
        }
      
        public static class User
        {	
        	private String id_str; 
        	private String screen_name;
        	private Boolean verified;
        	private Integer followers_count;
        	private Integer friends_count;
        	private Integer statuses_count;
        	private Object country_code;
        	private ArrayList<String> tweets_text;
        	private ArrayList<String> tweets_ids;
        	private ArrayList<String> hashtags;
        	private ArrayList<String> urls;
        	private ArrayList<String> user_mentions;

//            public User(String id_str, String screen_name, Boolean verified, Integer followers_count, Integer friends_count, Integer statuses_count, String country_code, ArrayList<String> tweets_text, ArrayList<String> tweets_ids, ArrayList<String> hashtags, ArrayList<String> urls,ArrayList<String> user_mentions ) {
//                super();
//                this.id_str = id_str;
//                this.screen_name = screen_name;
//                this.verified = verified;
//                this.followers_count = followers_count;
//                this.friends_count = friends_count;
//                this.statuses_count = statuses_count;
//                this.country_code = country_code;
//                this.tweets_text = tweets_text;
//                this.tweets_ids = tweets_ids;
//                this.hashtags = hashtags;
//                this.urls = urls;
//                this.user_mentions = user_mentions;
//                
//            }
         
            //Setters and Getters will be added here
         
            public String getId_str() {
				return id_str;
			}

			public void setId_str(String id_str) {
				this.id_str = id_str;
			}

			public String getScreen_name() {
				return screen_name;
			}

			public void setScreen_name(String screen_name) {
				this.screen_name = screen_name;
			}

			public Boolean getVerified() {
				return verified;
			}

			public void setVerified(Boolean verified) {
				this.verified = verified;
			}

			public Integer getFollowers_count() {
				return followers_count;
			}

			public void setFollowers_count(Integer followers_count) {
				this.followers_count = followers_count;
			}

			public Integer getFriends_count() {
				return friends_count;
			}

			public void setFriends_count(Integer friends_count) {
				this.friends_count = friends_count;
			}

			public Integer getStatuses_count() {
				return statuses_count;
			}

			public void setStatuses_count(Integer statuses_count) {
				this.statuses_count = statuses_count;
			}

			public Object getCountry_code() {
				return country_code;
			}

			public void setCountry_code(Object country_code) {
				if(country_code == null) {
					this.country_code = "NULL";
				}
				else {
					this.country_code = country_code;
				}	
			}

			public ArrayList<String> getTweets_text() {
				return tweets_text;
			}

			public void setTweets_text(ArrayList<String> tweets_text) {
				this.tweets_text = tweets_text;
			}

			public ArrayList<String> getTweets_ids() {
				return tweets_ids;
			}

			public void setTweets_ids(ArrayList<String> tweets_ids) {
				this.tweets_ids = tweets_ids;
			}

			public ArrayList<String> getHashtags() {
				return hashtags;
			}

			public void setHashtags(ArrayList<String> hashtags) {
				this.hashtags = hashtags;
			}

			public ArrayList<String> getUrls() {
				return urls;
			}

			public void setUrls(ArrayList<String> urls) {
				this.urls = urls;
			}

			public ArrayList<String> getUser_mentions() {
				return user_mentions;
			}

			public void setUser_mentions(ArrayList<String> user_mentions) {
				this.user_mentions = user_mentions;
			}

			@Override
            public String toString() {
//				
                return "User [id=" + id_str + ", name=" + screen_name + ", verified=" + verified + ", followers_count=" + 
                followers_count+", friends_count=" + friends_count+", statuses_count=" + statuses_count+", country_code=" + 
                country_code+", tweets_text=" + tweets_text+", tweets_ids=" + tweets_ids+", hashtags=" + hashtags+ ", urls=" + urls+ ", user_mentions=" + user_mentions+"]";
            }
        }
        public class HelloWorld implements AutoCloseable
        {
            private final Driver driver;
            GraphDatabaseFactory dbFact = null;
            GraphDatabaseService db = null;

            public HelloWorld( String uri, String user, String password )
            {
                driver = GraphDatabase.driver( uri, AuthTokens.basic( user, password ) );
            }

            public void close() throws Exception
            {
                driver.close();
            }
            
            public void addUserNode( User userobject )
            {
                try ( Session session = driver.session() )
                {
                    String greeting = session.writeTransaction( new TransactionWork<String>()
                    {
                        @Override
                        public String execute( Transaction tx )
                        {	
                        	
                        	StatementResult result = tx.run("MERGE (a:User {id_str: {a1},screen_name:{a2},verified:{a3},followers_count:{a4},friends_count:{a5},statuses_count:{a6},country_code:{a7},tweets_text:{a8},tweets_ids:{a9},hashtags:{a10},urls:{a11},user_mentions:{a12}})"
    							, parameters("a1", userobject.getId_str(),"a2",userobject.getScreen_name(),"a3",userobject.getVerified(),"a4",userobject.getFollowers_count(),"a5",userobject.getFriends_count(),"a6",userobject.getStatuses_count(),"a7",userobject.getCountry_code(),"a8",userobject.getTweets_text(),"a9",userobject.getTweets_ids(),"a10",userobject.getHashtags(),"a11",userobject.getUrls(),"a12",userobject.getUser_mentions()));
                            return "Done";
                        }
                    } );
                    System.out.println( greeting );
                }
            }
            
            public void addUserRelation(String id_str1, String id_str2, String string, String string2 )
            {
                try ( Session session = driver.session() )
                {
                    String greeting = session.writeTransaction( new TransactionWork<String>()
                    {
                        @Override
                        public String execute( Transaction tx )
                        {	
                        	
                        	StatementResult result = tx.run("MATCH (a:User),(b:User)"+
                        			"WHERE a.id_str = {x1} AND b.id_str = {x2}"+
                        			"MERGE (a)-[r:RETWEETED{new_tweet:{t1},old_tweet:{t2}}]->(b)"
                        			 , parameters("x1", id_str1,"x2",id_str2,"t1",string,"t2",string2));
                            return "Done";
                        }
                    } );
                    System.out.println( greeting );
                }
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
//                            return result.single().get( 0 ).asString();
                            return "Done";
                        }
                    } );
                    System.out.println( greeting );
                }
            }
//     
}
}
