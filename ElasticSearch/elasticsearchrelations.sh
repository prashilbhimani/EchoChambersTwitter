first_request=$(curl -X POST "localhost:9200/test/_search?scroll=10m" -H 'Content-Type: application/json' -d '{"size": 10,"query": {"match_all" : {}}}' )
scroll_id=$(echo $first_request | jq '._scroll_id')
hits=$(echo $first_request | jq '.hits.hits')
while true; do
	i=0
	inputJson=$(jq -n '{ statements: [] }')
	for row in $(echo "$hits" | jq -r '.[] | @base64'); do
	    _jq() {
	    	echo ${row} | base64 --decode | jq -r ${1}
	    }
	    i=$((i+1))
	    from_id=$(_jq '._source.from_id')
	    to_id=$(_jq '._source.to_id')
	    old_tweet=$(_jq '._source.old_tweet')
	    new_tweet=$(_jq '._source.new_tweet')
	    query="MATCH (a:User),(b:User) WHERE a.id_str = \"$from_id\" AND b.id_str = \"$to_id\" CREATE (a)-[r:RETWEETED{new_tweet: \"$new_tweet\",old_tweet: \"$old_tweet\"}]->(b) RETURN r"
	    inputJson=$(echo $inputJson | jq --arg q "$query" '.statements += [{statement: $q}]')
	done
	if [ $i -eq 0 ]
 		then
		break
 	fi
 	curl -X POST -H 'Content-type: application/json' http://neo4j:comm@35.231.74.19:7474/db/data/transaction/commit -d "$inputJson"
 	request=$(curl -X POST "localhost:9200/_search/scroll" -H 'Content-Type: application/json' -d "{\"scroll\": \"5m\", \"scroll_id\": $scroll_id}" )
	hits=$(echo $request | jq '.hits.hits')
done
