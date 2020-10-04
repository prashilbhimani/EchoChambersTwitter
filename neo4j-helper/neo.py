from neo4j import GraphDatabase

driver = GraphDatabase.driver("bolt://35.231.74.19:7687", auth=("neo4j", "comm"))

def get_node_count(tx):
    result = tx.run("MATCH (n) RETURN count(*)")
    return result.single()[0]


def get_relation_count(tx):
    result = tx.run("MATCH (n)-[r]->() RETURN COUNT(r)")
    return result.single()[0]

def add_friend(tx, name, friend_name):
    tx.run("MERGE (a:Person {name: $name}) "
           "MERGE (a)-[:KNOWS]->(friend:Person {name: $friend_name})",
           name=name, friend_name=friend_name)

def load_users(tx):

    # tx.run("WITH \"https://storage.googleapis.com/pub_bucket_01/data.json\" as url "+
    #     "CALL apoc.load.json(url) YIELD value AS user "+
    #         "MERGE (u:User {id_str:user.id_str}) "+
    #         "SET u.screen_name=user.screen_name, u.verified=user.verified, u.followers_count=user.followers_count,"+
    #         " u.friends_count=user.friends_count, u.statuses_count=user.statuses_count, u.country_code=user.country_code,"+
    #         " u.tweets_text=user.tweets_text, u.tweets_ids=user.tweets_ids, u.hashtags=user.hashtags, u.urls=user.urls, u.user_mentions=user.user_mentions")
    tx.run("USING PERIODIC COMMIT 500 "
    "LOAD CSV WITH HEADERS FROM 'data2.csv' AS line "
    "MERGE (:User { id_str: line.id_str, verified: line.verified, followers_count: toInteger(line.followers_count), friends_count: toInteger(line.friends_count),statuses_count: toInteger(line.statuses_count), country_code: line.country_code, tweets_text:line.tweets_text, tweets_ids:line.tweets_ids, hashtags:line.hashtags, urls:line.urls, user_mentions:line.user_mentions})")

# def get_node_relation_count(tx,node_name):
#     result = tx.run("MATCH (n)-[r]->() RETURN COUNT(r)")
#     return result.single()[0]
#     MATCH(a: Person {username: 'user6'})-[r] - (b) RETURN Count(r)

# def print_friends(tx, name):
#     for record in tx.run("MATCH (a:Person)-[:KNOWS]->(friend) WHERE a.name = $name "
#                          "RETURN friend.name ORDER BY friend.name", name=name):
#         print(record["friend.name"])

with driver.session() as session:
    # session.write_transaction(add_friend, "Arthur", "Guinevere")
    # session.write_transaction(add_friend, "Arthur", "Lancelot")
    # session.write_transaction(add_friend, "Arthur", "Merlin")
    # session.read_transaction(print_friends, "Arthur")
    session.write_transaction(load_users)
    # c=session.read_transaction(get_node_count)
    # print("Count of nodes:",c)
    # d=session.read_transaction(get_relation_count)
    # print("Number of relationships:",d)
    a=""