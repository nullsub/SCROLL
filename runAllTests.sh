#!/bin/bash
if [ -f results.csv ]; then
    mv results.csv resultsOld.csv
fi
#array=( '0' '1' '2' )
array=( '0' )
for i in "${array[@]}"
do
	echo Running test for $i
	sh ./cleanAll.sh
	CACHE_CONF=$i ./gradlew jmh
	sed -i 's/scroll.benchmarks/scroll.'"$i"'.benchmarks/g' build/reports/jmh/results.csv
	cat build/reports/jmh/results.csv >> results.csv
done
mv results.csv results`date --iso-8601=minutes`.csv
