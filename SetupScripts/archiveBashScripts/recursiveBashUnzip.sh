#!/bin/bash
mkdir 'small'
for day in $( ls '04/'); do
	mkdir "small/$day"
    for hour in $( ls "04/$day"); do
    	mkdir "small/$day/$hour"
    	echo "Created small/$day/$hour"
    	for min in $( ls "04/$day/$hour"); do
    		bunzip2 "04/$day/$hour/$min"
    	done
    	echo "Unziped 04/$day/$hour/"
    	rm -f "04/$day/$hour/*.bz2"
    	for min in $( ls "04/$day/$hour"); do
    		jq -c '. | {"hashtags": .entities["hashtags"], "retweeted_hashtags": .retweeted_status["extended_tweet"]["entities"]["hashtags"]}' "04/$day/$hour/$min" > "small/$day/$hour/$min"
    	done
    	rm -f "04/$day/$hour/*"
  	done
  	echo "Copying data for $day"
  	gsutil -m cp -r "small/$day" "gs://cu-boulder-echo-chambers-twitter-data/April1/"
done
read -p "Enter"