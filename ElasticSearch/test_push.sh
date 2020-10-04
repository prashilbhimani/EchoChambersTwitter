for i in `seq 1 $1`; do
	 curl -X PUT "localhost:9200/test/data/$i" -H 'Content-Type: application/json' -d '{"message" : "$i"}'
done

