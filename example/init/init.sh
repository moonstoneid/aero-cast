# Copy ganache data
if [ -z "$(ls -A /ganache-data)" ]; then
   cp /data/ganache/data/* /ganache-data
fi

# Copy aggregator data
if [ -z "$(ls -A /aggregator-data)" ]; then
   cp /data/aggregator/aggdb.mv.db /aggregator-data
fi

# Copy publisher 1 data
if [ -z "$(ls -A /publisher-1-data)" ]; then
   cp /data/publisher-1/pub.ico /publisher-1-data
   cp /data/publisher-1/pubdb.mv.db /publisher-1-data
fi

# Copy publisher 2 data
if [ -z "$(ls -A /publisher-2-data)" ]; then
   cp /data/publisher-2/pub.ico /publisher-2-data
   cp /data/publisher-2/pubdb.mv.db /publisher-2-data
fi
